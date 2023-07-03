package ch.wesr.starter.kirkespringbootstarter.bus.publish;

import ch.wesr.starter.kirkespringbootstarter.bus.EventSubscriber;
import ch.wesr.starter.kirkespringbootstarter.bus.handler.PublishEventHandler;
import ch.wesr.starter.kirkespringbootstarter.bus.impl.KirkeMessage;
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
public class KirkeDomainEventPublisher implements EventSubscriber {

    public final static String BEAN_NAME = "domainHandler";

    Topic topic;

    private final JCSMPSession jcsmpSession;

    private final ObjectMapper objectMapper;

    public KirkeDomainEventPublisher(JCSMPSession jcsmpSession, ObjectMapper objectMapper) {
        this.jcsmpSession = jcsmpSession;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void setup() throws JCSMPException {
        this.topic =  JCSMPFactory.onlyInstance().createTopic("kirke/topic");
        // Publish-only session is now hooked up and running!
        jcsmpSession.addSubscription(topic);
    }

    @SneakyThrows
    @Override
    public void handleEvent(KirkeMessage kirkeMessage) {
//        final JCSMPSession session = solaceFactory.createSession();
        String msg = objectMapper.writeValueAsString(kirkeMessage);
        PublishEventHandler pubEventHandler = new PublishEventHandler();
        /** Anonymous inner-class for handling publishing events */
        XMLMessageProducer prod = jcsmpSession.getMessageProducer(pubEventHandler);


        TextMessage jcsmpMsg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        jcsmpMsg.setText(msg);
        jcsmpMsg.setDeliveryMode(DeliveryMode.DIRECT);

        log.debug("Sending {}", msg);
        prod.send(jcsmpMsg, topic);
    }
}
