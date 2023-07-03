package ch.wesr.starter.kirkespringbootstarter.gateway.command;


import ch.wesr.starter.kirkespringbootstarter.bus.KirkeEventBus;
import ch.wesr.starter.kirkespringbootstarter.bus.impl.KirkeMessage;
import ch.wesr.starter.kirkespringbootstarter.gateway.SpringContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AggregateLifeCycle {

    public static void apply(Object event) {
        KirkeEventBus eventBus = SpringContext.getBean(KirkeEventBus.class);
        ObjectMapper objectMapper = SpringContext.getBean(ObjectMapper.class);

        try {
            String eventAsString = objectMapper.writeValueAsString(event);
            KirkeMessage kirkeMessage = new KirkeMessage( event.getClass(), eventAsString);
            log.debug("Publish payload: {}", kirkeMessage);
            eventBus.publish(kirkeMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

}
