package ch.wesr.starter.kirkespringbootstarter.eventsourcing.impl;


import ch.wesr.starter.kirkespringbootstarter.annotation.Aggregate;
import ch.wesr.starter.kirkespringbootstarter.annotation.AggregatedEventIdentifier;
import ch.wesr.starter.kirkespringbootstarter.annotation.EventSourceHandler;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.EventRepository;
import ch.wesr.starter.kirkespringbootstarter.gateway.AggregatedMethodResolver;
import ch.wesr.starter.kirkespringbootstarter.gateway.TargetIdentifierResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Component
public class EventRepositoryImpl implements EventRepository {

    Map<UUID, Map<Class<?>, String>> eventMap;
    ObjectMapper objectMapper;

    public EventRepositoryImpl() {
        objectMapper = new ObjectMapper();
        // ist ein Hack aber funktioniert
        // com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.Instant` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310"
        objectMapper.registerModule(new JavaTimeModule());
        eventMap = new LinkedHashMap<>();
    }

    @Override
    public void on(Object event) {

        UUID targetIdentifier = TargetIdentifierResolver.resolve(event, AggregatedEventIdentifier.class);
        log.debug("[{}]  {}: {}", targetIdentifier, event.getClass().getSimpleName(), event);

        try {

            String eventAsString = objectMapper.writeValueAsString(event);
            if (eventMap.containsKey(targetIdentifier)) {
                Map<Class<?>, String> classStringMap = eventMap.get(targetIdentifier);
                classStringMap.put(event.getClass(), eventAsString);
            } else {
                Map<Class<?>, String> classStringMap = new LinkedHashMap<>();
                classStringMap.put(event.getClass(), eventAsString);
                eventMap.put(targetIdentifier, classStringMap);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        log.debug("[{}] eventmap: {}", targetIdentifier, eventMap.toString());
    }

    @Override
    public Optional<Object> findByTargetIdentifier(UUID targetIdentifier) {
        // finde das persistierte TargetIdentifier
        // Lade die Events aus dem Store und fahre die Events gegen das Aggregate
        if (eventMap.containsKey(targetIdentifier)) {
            Map<Class<?>, String> classStringMap = eventMap.get(targetIdentifier);
            var ref = new Object() {Object aggregate = null;};

            // FIXME Cache einbauen... bzw. Snapshot oder in der Art etwas ähnliches
            // bzw. vermeide so lange wie möglich Snapshots

            // an dieser Stelle wird ueber alle Events iteriert und
            // die Aggregate Methoden - annotiert mit @EventSourceHandler - aufgerufen
            classStringMap.forEach((key, value) -> {
                log.debug("Event with targetIdentifier [{}] key: {}, value: {}", targetIdentifier, key, value);
                try {
                    Object event = objectMapper.readValue(value, key);
                    log.debug("[{}] event name: {}", targetIdentifier, event.getClass().getSimpleName());

                    Method method = extractMethod(event);

                    if (ref.aggregate == null) {
                        ref.aggregate = method.getDeclaringClass().newInstance();
                    }

                    method.invoke(ref.aggregate, event);
                } catch (JsonProcessingException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    // FIXME Exception Handling
                    throw new RuntimeException(e);
                }
            });

            return Optional.of(ref.aggregate);
        }

        return Optional.empty();
    }

    private static Method extractMethod(Object event) {
        List<Method> methodList = new AggregatedMethodResolver()
                .filterClassAnnotatedWith(Aggregate.class)
                .filterMethodAnnotatedWith(EventSourceHandler.class)
                .filterMethodParameter(event)
                .resolve();
        assert methodList.size() == 1;
        return methodList.get(0);
    }


}

