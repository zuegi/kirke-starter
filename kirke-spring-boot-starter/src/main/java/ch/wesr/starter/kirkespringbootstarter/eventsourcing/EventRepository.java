package ch.wesr.starter.kirkespringbootstarter.eventsourcing;

import ch.wesr.starter.kirkespringbootstarter.bus.impl.KirkeMessage;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository {
    UUID on(KirkeMessage event);

    Optional<Object> findByTargetIdentifier(UUID targetIdentifier);
}
