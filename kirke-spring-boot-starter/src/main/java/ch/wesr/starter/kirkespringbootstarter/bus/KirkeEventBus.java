package ch.wesr.starter.kirkespringbootstarter.bus;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class KirkeEventBus {

    private ApplicationContext context;
    private Map<String, EventSubscriber> subscribers;
    private boolean hasInitialized = false;

    public KirkeEventBus(ApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    private void initialize() {
        subscribers = this.context.getBeansOfType(EventSubscriber.class);
        this.hasInitialized = true;
    }

    public void publish(Object event) {
        for (String key : subscribers.keySet()) {
            log.debug("key: {}", key);
            subscribers.get(key).handleEvent(event);
        }
    }
}
