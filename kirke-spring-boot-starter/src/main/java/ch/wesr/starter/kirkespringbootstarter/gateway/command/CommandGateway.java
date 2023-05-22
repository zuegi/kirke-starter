package ch.wesr.starter.kirkespringbootstarter.gateway.command;


import ch.wesr.starter.kirkespringbootstarter.annotation.CommandHandler;
import ch.wesr.starter.kirkespringbootstarter.annotation.TargetAggregateIdentifier;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.EventRepository;
import ch.wesr.starter.kirkespringbootstarter.gateway.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class CommandGateway {

    private final EventRepository eventRepository;

    public CommandGateway(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public UUID send(Object command) {

        // den Wert des @TargetAggregateIdentifer aus dem Command via Reflection auslesen
        UUID targetIdentifier = TargetIdentifierResolver.resolve(command, TargetAggregateIdentifier.class);
        log.debug("[{}] read targetIdentifer from command: {}", targetIdentifier, command);
        // extrahiere die Methode, welche mit @CommandHandler annotiert ist und command als signature parameter hat
        List<Method> methodList = getMethods(command);
        Method method = assertCorrectNumberOfMethods(methodList);

        try {
            Object aggregateObject = eventRepository.findByTargetIdentifier(targetIdentifier).orElse( createNewAggregateObject(method));
            log.debug("[{}] found aggregate: {}", targetIdentifier, aggregateObject.toString());
            // an dieser stellen das aus dem repository erstellte Aggregate Object aufrufen
            log.debug("[{}] invoke method: {}({},{})", targetIdentifier, method.getName(), aggregateObject, command);
            method.invoke(aggregateObject, command);

            return targetIdentifier;

        } catch (InvocationTargetException | IllegalAccessException e) {
            // FIXME Exception definieren
            throw new RuntimeException(e);
        }
    }

    private static List<Method> getMethods(Object command) {
        return new AggregatedMethodResolver()
                .filterMethodAnnotatedWith(CommandHandler.class)
                .filterMethodParameter(command)
                .resolve();
    }

    private static Object createNewAggregateObject(Method method)  {
        try {
            AggregateClassResolver aggregateClassResolver = new AggregateClassResolver(method);
            return aggregateClassResolver.resolve();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            // FIXME Exception definieren
            throw new RuntimeException(e);
        }
    }

    private Method assertCorrectNumberOfMethods(List<Method> methodList) {
        if (methodList.size() == 0) {
            throw new NoAnnotatedMethodFoundException(CommandGatewayMessage.NO_WAY);
        }
        if (methodList.size() > 1) {
            throw new ToManyAnnotatedMethodException(CommandGatewayMessage.TO_MANY);
        }
        return methodList.get(0);
    }
}
