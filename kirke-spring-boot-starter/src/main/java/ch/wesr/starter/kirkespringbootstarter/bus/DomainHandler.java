package ch.wesr.starter.kirkespringbootstarter.bus;

import ch.wesr.starter.kirkespringbootstarter.annotation.AggregatedEventIdentifier;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.EventRepository;
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
        EventRepository eventRepository = SpringContext.getBean(EventRepository.class);
        log.debug("[{}] found method: {} to be invoked on {}", targetIdentifier,"on", eventRepository.getClass().getSimpleName());
        eventRepository.on(event);
    }
}
