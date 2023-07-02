package ch.wesr.starter.kirkespringbootstarter.gateway.command;


import ch.wesr.starter.kirkespringbootstarter.bus.KirkeEventBus;
import ch.wesr.starter.kirkespringbootstarter.bus.KirkePayLoad;
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
            KirkePayLoad kirkePayLoad = new KirkePayLoad( event.getClass(), eventAsString);
            log.debug("Publish payload: {}", kirkePayLoad);
            eventBus.publish(kirkePayLoad);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

}
