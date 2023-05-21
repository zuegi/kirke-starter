package ch.wesr.starter.kirkespringbootstarter.eventsourcing;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository {
    void on(Object event);

    Optional<Object> findByTargetIdentifier(UUID targetIdentifier);
}
