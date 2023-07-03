package ch.wesr.starter.kirkespringbootstarter.bus.handler;

import ch.wesr.starter.kirkespringbootstarter.annotation.AggregatedEventIdentifier;
import ch.wesr.starter.kirkespringbootstarter.bus.EventSubscriber;
import ch.wesr.starter.kirkespringbootstarter.bus.impl.KirkeMessage;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.impl.EventRepositoryImpl;
import ch.wesr.starter.kirkespringbootstarter.gateway.SpringContext;
import ch.wesr.starter.kirkespringbootstarter.gateway.TargetIdentifierResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
//@Component
public class DomainHandler implements EventSubscriber {

    public final static String BEAN_NAME = "domainHandler";

    private final ObjectMapper objectMapper;

    public DomainHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleEvent(KirkeMessage kirkeMessage) {
        log.debug("handleEvent({})", kirkeMessage.payload());
        try {
            Object event = objectMapper.readValue(kirkeMessage.payload().toString(), kirkeMessage.source());
            UUID targetIdentifier = TargetIdentifierResolver.resolve(event, AggregatedEventIdentifier.class);
            log.debug("[{}]  {}: {}", targetIdentifier, kirkeMessage.source(), kirkeMessage);

            EventRepositoryImpl eventRepositoryImpl = SpringContext.getBean(EventRepositoryImpl.class);
            log.debug("[{}] found method: {} to be invoked on {}", targetIdentifier,"on", eventRepositoryImpl.getClass().getSimpleName());
            eventRepositoryImpl.on(kirkeMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}
