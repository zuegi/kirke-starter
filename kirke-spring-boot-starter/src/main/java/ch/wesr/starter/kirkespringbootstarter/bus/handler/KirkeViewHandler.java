package ch.wesr.starter.kirkespringbootstarter.bus.handler;

import ch.wesr.starter.kirkespringbootstarter.annotation.AggregatedEventIdentifier;
import ch.wesr.starter.kirkespringbootstarter.annotation.EventHandler;
import ch.wesr.starter.kirkespringbootstarter.bus.impl.KirkeMessage;
import ch.wesr.starter.kirkespringbootstarter.gateway.AggregatedMethodResolver;
import ch.wesr.starter.kirkespringbootstarter.gateway.SpringContext;
import ch.wesr.starter.kirkespringbootstarter.gateway.TargetIdentifierResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solacesystems.jcsmp.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;


@Slf4j
public class KirkeViewHandler implements XMLMessageListener {

    private final ObjectMapper objectMapper;
    private final JCSMPSession session;

    public KirkeViewHandler(ObjectMapper objectMapper, JCSMPSession session) {
        this.objectMapper = objectMapper;
        this.session = session;
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

    @Override
    public void onReceive(BytesXMLMessage bytesXMLMessage) {
        if (bytesXMLMessage instanceof TextMessage) {
            String messageAsString = ((TextMessage) bytesXMLMessage).getText();
            log.debug("TextMessage received: {}", messageAsString);
            try {
                KirkeMessage kirkeMessage = objectMapper.readValue(messageAsString, KirkeMessage.class);
                // hier gibt es das Objekt mit der Annotation wahrscheinlich nicht mehr
                Object event = objectMapper.readValue(kirkeMessage.payload().toString(), kirkeMessage.source());

                UUID targetIdentifier = TargetIdentifierResolver.resolve(event, AggregatedEventIdentifier.class);
                log.debug("[{}]  {}: {}", targetIdentifier, kirkeMessage.source(), kirkeMessage);
                // Projector Methoden mit der Annotation EventHandler bedienen
                List<Method> methods = new AggregatedMethodResolver()
                        .filterMethodAnnotatedWith(EventHandler.class)
                        .filterMethodParameter(event)
                        .resolve();

                methods.forEach(method -> {
                    Class<?> declaringClass = method.getDeclaringClass();
                    Object bean = SpringContext.getBean(declaringClass);
                    log.debug("[{}] found method: {} to be invoked on {}", targetIdentifier, method.getName(), bean.getClass().getSimpleName());

                    try {
                        // Aufruf der Bean mit EventHandler Annotationen, z.B. FoodCartProjector
                        method.invoke(bean, event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error("[{}] Error calling method: {} on bean: {} \n {}", targetIdentifier, method.getName(), bean.getClass().getSimpleName(), e.getMessage());
                    }

                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.error("Unexcepted type of message received: {}", bytesXMLMessage);
        }
    }

    @Override
    public void onException(JCSMPException e) {
        log.error(this.getClass().getSimpleName() +" received exception:", e);
    }
}
