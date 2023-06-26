package ch.wesr.starter.kirkespringbootstarter.bus.impl;

import ch.wesr.starter.kirkespringbootstarter.annotation.AggregatedEventIdentifier;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.impl.EventRepositoryImpl;
import ch.wesr.starter.kirkespringbootstarter.gateway.SpringContext;
import ch.wesr.starter.kirkespringbootstarter.gateway.TargetIdentifierResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.XMLMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class MessageConsumer implements XMLMessageListener {

    private ObjectMapper objectMapper;


    public MessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void onReceive(BytesXMLMessage msg) {
        if (msg instanceof TextMessage) {
            log.info("============= TextMessage received: {}", ((TextMessage) msg).getText());

            try {
                Object event = objectMapper.readValue(((TextMessage) msg).getText(), Object.class);
                log.debug("handleEvent({})", event);
                UUID targetIdentifier = TargetIdentifierResolver.resolve(event, AggregatedEventIdentifier.class);
                log.debug("[{}]  {}: {}", targetIdentifier, event.getClass().getSimpleName(), event);
                EventRepositoryImpl eventRepositoryImpl = SpringContext.getBean(EventRepositoryImpl.class);
                log.debug("[{}] found method: {} to be invoked on {}", targetIdentifier,"on", eventRepositoryImpl.getClass().getSimpleName());
                eventRepositoryImpl.on(event);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("============= Message received.");
        }
    }

    public void onException(JCSMPException e) {
        log.info("Consumer received exception:", e);
    }

}
