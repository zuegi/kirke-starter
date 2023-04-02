package ch.wesr.starter.kirkespringbootstarter.gateway.command;


import ch.wesr.starter.kirkespringbootstarter.annotation.AggregatedEventIdentifier;
import ch.wesr.starter.kirkespringbootstarter.annotation.EventHandler;
import ch.wesr.starter.kirkespringbootstarter.gateway.AggregatedMethodResolver;
import ch.wesr.starter.kirkespringbootstarter.gateway.SpringContext;
import ch.wesr.starter.kirkespringbootstarter.gateway.TargetIdentifierResolver;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

@Slf4j
/**
 * NOTE: This class should never be made a spring bean as its method are static for using in pojos
 */
public class AggregateLifeCycle {

    // mit dieser Methode wollen wir ein existierendes Bean aufrufen, dazu hilft uns die Klasse SpringContext
    public static void apply(Object event) {
        UUID targetIdentifier = TargetIdentifierResolver.resolve(event, AggregatedEventIdentifier.class);
        log.debug("[{}]  {}: {}", targetIdentifier, event.getClass().getSimpleName(), event);

        List<Method> methods = new AggregatedMethodResolver()
                .filterMethodAnnotatedWith(EventHandler.class)
                .filterMethodParameter(event)
                .resolve();

        methods.forEach(method -> {
            Class<?> declaringClass = method.getDeclaringClass();
            Object bean = SpringContext.getBean(declaringClass);
            log.debug("[{}] found method: {} to be invoked on {}", targetIdentifier, method.getName(), bean.getClass().getSimpleName());

            try {
                method.invoke(bean, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("[{}] Error calling method: {} on bean: {}\n{}", targetIdentifier, method.getName(), bean.getClass().getSimpleName(), e);
            }

        });


    }

}
