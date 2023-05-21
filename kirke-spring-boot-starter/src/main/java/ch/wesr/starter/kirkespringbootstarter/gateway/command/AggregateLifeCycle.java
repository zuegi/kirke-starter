package ch.wesr.starter.kirkespringbootstarter.gateway.command;


import ch.wesr.starter.kirkespringbootstarter.bus.KirkeEventBus;
import ch.wesr.starter.kirkespringbootstarter.gateway.SpringContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AggregateLifeCycle {

    public static void apply(Object event) {
        KirkeEventBus eventBus = SpringContext.getBean(KirkeEventBus.class);
        eventBus.publish(event);

    }

}
