package ch.wesr.starter.kirkespringbootstarter.bus.impl;

import ch.wesr.starter.kirkespringbootstarter.bus.KirkeEventBus;
import ch.wesr.starter.kirkespringbootstarter.bus.handler.PublishEventHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solacesystems.jcsmp.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KirkeSolaceEventBusImpl implements KirkeEventBus {

    public final static String BEAN_NAME = "domainHandler";

    Topic topic;

    private final JCSMPSession jcsmpSession;

    private final ObjectMapper objectMapper;

    public KirkeSolaceEventBusImpl(JCSMPSession jcsmpSession, ObjectMapper objectMapper) {
        this.jcsmpSession = jcsmpSession;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void initialize() throws JCSMPException {
        this.topic =  JCSMPFactory.onlyInstance().createTopic("kirke/command");
        // Publish-only session is now hooked up and running!
        jcsmpSession.addSubscription(topic);
    }

    @Override
    public void publish(KirkeMessage kirkeMessage) {
        try {
            String msg = objectMapper.writeValueAsString(kirkeMessage);
            PublishEventHandler pubEventHandler = new PublishEventHandler();
            /** Anonymous inner-class for handling publishing events */
            XMLMessageProducer prod = jcsmpSession.getMessageProducer(pubEventHandler);


            TextMessage jcsmpMsg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
            jcsmpMsg.setText(msg);
            jcsmpMsg.setDeliveryMode(DeliveryMode.DIRECT);

            log.debug("Sending {}", msg);
            prod.send(jcsmpMsg, topic);
        } catch (JsonProcessingException | JCSMPException e) {
            throw new RuntimeException(e);
        }

    }
}
