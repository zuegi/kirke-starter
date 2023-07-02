package ch.wesr.starter.kirkespringbootstarter.eventsourcing;

import ch.wesr.starter.kirkespringbootstarter.bus.KirkePayLoad;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository {
    void on(KirkePayLoad event);

    Optional<Object> findByTargetIdentifier(UUID targetIdentifier);
}
