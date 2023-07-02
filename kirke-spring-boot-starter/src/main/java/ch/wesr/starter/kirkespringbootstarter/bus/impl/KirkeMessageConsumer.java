package ch.wesr.starter.kirkespringbootstarter.bus.impl;

import ch.wesr.starter.kirkespringbootstarter.bus.KirkePayLoad;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solacesystems.jcsmp.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KirkeMessageConsumer implements XMLMessageListener {

    private final ObjectMapper objectMapper;
    private final JCSMPSession session;
    private final EventRepository eventRepository;


    public KirkeMessageConsumer(JCSMPSession jcsmpSession, ObjectMapper objectMapper, EventRepository eventRepository) {
        this.objectMapper = objectMapper;
        this.session = jcsmpSession;
        this.eventRepository = eventRepository;
    }

    @PostConstruct
    private void setup() {
        try {
            XMLMessageConsumer cons = session.getMessageConsumer(this);
            cons.start();
        } catch (JCSMPException e) {
            throw new RuntimeException(e);
        }
        log.info("Connected. Awaiting message...");
    }

    public void onReceive(BytesXMLMessage msg) {
        if (msg instanceof TextMessage) {
            String messageAsString = ((TextMessage) msg).getText();
            log.debug("TextMessage received: {}", messageAsString);
            try {
                KirkePayLoad kirkePayLoad = objectMapper.readValue(messageAsString, KirkePayLoad.class);
                eventRepository.on(kirkePayLoad);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.error("Unexcepted type of message received: {}", msg);
        }
    }

    public void onException(JCSMPException e) {
        log.error("Consumer received exception:", e);
    }

}
