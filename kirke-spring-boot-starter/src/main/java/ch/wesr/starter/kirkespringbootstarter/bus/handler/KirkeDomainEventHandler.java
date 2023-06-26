package ch.wesr.starter.kirkespringbootstarter.bus.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solacesystems.jcsmp.*;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Eine mögliche Implementierung mit Solace
 */

@Slf4j
@Component
public class KirkeDomainEventHandler implements EventSubscriber {

    Topic topic;

    private final SpringJCSMPFactory solaceFactory;

    private final ObjectMapper objectMapper;

    public KirkeDomainEventHandler(SpringJCSMPFactory solaceFactory, ObjectMapper objectMapper) {
        this.solaceFactory = solaceFactory;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void setup() {
        this.topic =  JCSMPFactory.onlyInstance().createTopic("kirke/topic");
    }

    @SneakyThrows
    @Override
    public void handleEvent(Object event) {
        final JCSMPSession session = solaceFactory.createSession();
        String msg = objectMapper.writeValueAsString(event);
        PublishEventHandler pubEventHandler = new PublishEventHandler();
        /** Anonymous inner-class for handling publishing events */
        XMLMessageProducer prod = session.getMessageProducer(pubEventHandler);
        // Publish-only session is now hooked up and running!

        TextMessage jcsmpMsg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        jcsmpMsg.setText(msg);
        jcsmpMsg.setDeliveryMode(DeliveryMode.PERSISTENT);

        log.info("============= Sending {}", msg);
        prod.send(jcsmpMsg, topic);
    }
}
