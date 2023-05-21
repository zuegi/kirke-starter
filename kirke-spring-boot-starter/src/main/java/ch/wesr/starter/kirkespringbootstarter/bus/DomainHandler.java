package ch.wesr.starter.kirkespringbootstarter.bus;

import ch.wesr.starter.kirkespringbootstarter.annotation.AggregatedEventIdentifier;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.impl.EventRepositoryImpl;
import ch.wesr.starter.kirkespringbootstarter.gateway.SpringContext;
import ch.wesr.starter.kirkespringbootstarter.gateway.TargetIdentifierResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class DomainHandler implements EventSubscriber{

    @Override
    public void handleEvent(Object event) {
        log.debug("handleEvent({})", event);
        UUID targetIdentifier = TargetIdentifierResolver.resolve(event, AggregatedEventIdentifier.class);
        log.debug("[{}]  {}: {}", targetIdentifier, event.getClass().getSimpleName(), event);
        EventRepositoryImpl eventRepositoryImpl = SpringContext.getBean(EventRepositoryImpl.class);
        log.debug("[{}] found method: {} to be invoked on {}", targetIdentifier,"on", eventRepositoryImpl.getClass().getSimpleName());
        eventRepositoryImpl.on(event);
    }
}
