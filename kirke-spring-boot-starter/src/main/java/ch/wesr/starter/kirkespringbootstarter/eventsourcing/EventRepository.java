package ch.wesr.starter.kirkespringbootstarter.eventsourcing;


import ch.wesr.starter.kirkespringbootstarter.annotation.Aggregate;
import ch.wesr.starter.kirkespringbootstarter.annotation.AggregatedEventIdentifier;
import ch.wesr.starter.kirkespringbootstarter.annotation.EventHandler;
import ch.wesr.starter.kirkespringbootstarter.annotation.EventSourceHandler;
import ch.wesr.starter.kirkespringbootstarter.gateway.AggregatedFieldResolver;
import ch.wesr.starter.kirkespringbootstarter.gateway.AggregatedMethodResolver;
import ch.wesr.starter.kirkespringbootstarter.gateway.TargetIdentifierResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Component
public class EventRepository {

    Map<UUID, Map<Class<?>, String>> eventMap;
    ObjectMapper objectMapper;

    public EventRepository() {
        objectMapper = new ObjectMapper();
        // ist ein Hack aber funktioniert
        // com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.Instant` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310"
        objectMapper.registerModule(new JavaTimeModule());
        eventMap = new LinkedHashMap<>();
    }

    @EventHandler
    public void on(Object event) {

        UUID targetIdentifier = TargetIdentifierResolver.resolve(event,AggregatedEventIdentifier.class);
        log.debug("[{}]  {}: {}", targetIdentifier, event.getClass().getSimpleName(), event);
        List<Field> fieldList = new AggregatedFieldResolver()
                .filterClasses(event.getClass())
                .filterFieldAnnotationWith(AggregatedEventIdentifier.class)
                .resolve();
        // FIXME fieldlist.size mit Exceptions
        assert fieldList.size() == 1;

        // FIXME kann man schoener machen
        try {
            Field field = fieldList.get(0);
            field.setAccessible(true);
            Object objectId = field.get(event);
            String eventAsString = objectMapper.writeValueAsString(event);
            if (eventMap.containsKey(objectId)) {
                Map<Class<?>, String> classStringMap = eventMap.get(objectId);
                classStringMap.put(event.getClass(), eventAsString);
            } else {
                Map<Class<?>, String> classStringMap = new LinkedHashMap<>();
                classStringMap.put(event.getClass(), eventAsString);
                eventMap.put((UUID) objectId, classStringMap);
            }
        } catch (IllegalAccessException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        log.debug("[{}] eventmap: {}", targetIdentifier, eventMap.toString());
    }

    public Optional<Object> findByTargetIdentifier(UUID targetIdentifier) {
        // falls einen persistiertes TargetIdentifier gibt
        // Lade die serialisierten Events aus dem Store und de-serialize die Events
        // fahre die Events gegen das Aggregate mit den EventHandler Methoden nach
        // gib das Aggregate zurueck

        if (eventMap.containsKey(targetIdentifier)) {
            Map<Class<?>, String> classStringMap = eventMap.get(targetIdentifier);


            var ref = new Object() {
                Object aggregate = null;
            };
            classStringMap.forEach((key, value) -> {
                log.debug("[{}] key: {}, value: {}", targetIdentifier, key, value);
                try {
                    Object event = objectMapper.readValue(value, key);
                    log.debug("[{}] event name: {}", targetIdentifier, event.getClass().getSimpleName());

                    List<Method> methodList = new AggregatedMethodResolver()
                            .filterClassAnnotatedWith(Aggregate.class)
                            .filterMethodAnnotatedWith(EventSourceHandler.class)
                            .filterMethodParameter(event)
                            .resolve();
                    assert methodList.size() == 1;

                    if (ref.aggregate == null) {
                        ref.aggregate = methodList.get(0).getDeclaringClass().newInstance();
                    }

                    methodList.forEach(method -> {
                        try {
                            method.invoke(ref.aggregate, event);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    });

                } catch (JsonProcessingException | InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            });

            return Optional.of(ref.aggregate);
        }

        return Optional.empty();
    }


}

