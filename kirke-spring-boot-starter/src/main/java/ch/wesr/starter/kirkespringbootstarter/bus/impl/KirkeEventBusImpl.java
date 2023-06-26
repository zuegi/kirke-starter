package ch.wesr.starter.kirkespringbootstarter.bus.impl;

import ch.wesr.starter.kirkespringbootstarter.bus.KirkeEventBus;
import ch.wesr.starter.kirkespringbootstarter.bus.handler.EventSubscriber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@Slf4j
public class KirkeEventBusImpl implements KirkeEventBus {

    private final ApplicationContext context;
    private Map<String, EventSubscriber> subscribers;

    public KirkeEventBusImpl(ApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    private void initialize() {
        subscribers = this.context.getBeansOfType(EventSubscriber.class);
    }

    @Override
    public void publish(Object event) {
        for (String key : subscribers.keySet()) {
            log.debug("key: {}", key);
            subscribers.get(key).handleEvent(event);
        }
    }
}
