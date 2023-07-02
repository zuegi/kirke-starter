package ch.wesr.starter.kirkespringbootstarter.bus.handler;

import ch.wesr.starter.kirkespringbootstarter.annotation.AggregatedEventIdentifier;
import ch.wesr.starter.kirkespringbootstarter.bus.KirkePayLoad;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.impl.EventRepositoryImpl;
import ch.wesr.starter.kirkespringbootstarter.gateway.SpringContext;
import ch.wesr.starter.kirkespringbootstarter.gateway.TargetIdentifierResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
//@Component
public class DomainHandler implements EventSubscriber{

    public final static String BEAN_NAME = "domainHandler";

    private final ObjectMapper objectMapper;

    public DomainHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleEvent(KirkePayLoad kirkePayLoad) {
        log.debug("handleEvent({})", kirkePayLoad.payload());
        try {
            Object event = objectMapper.readValue(kirkePayLoad.payload().toString(), kirkePayLoad.source());
            UUID targetIdentifier = TargetIdentifierResolver.resolve(event, AggregatedEventIdentifier.class);
            log.debug("[{}]  {}: {}", targetIdentifier, kirkePayLoad.source(), kirkePayLoad);

            EventRepositoryImpl eventRepositoryImpl = SpringContext.getBean(EventRepositoryImpl.class);
            log.debug("[{}] found method: {} to be invoked on {}", targetIdentifier,"on", eventRepositoryImpl.getClass().getSimpleName());
            eventRepositoryImpl.on(kirkePayLoad);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}
