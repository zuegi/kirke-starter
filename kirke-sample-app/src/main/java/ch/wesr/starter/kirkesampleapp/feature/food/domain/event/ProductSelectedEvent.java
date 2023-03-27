package ch.wesr.starter.kirkesampleapp.feature.food.domain.event;


import ch.wesr.starter.kirkespringbootstarter.annotation.AggregatedEventIdentifier;

import java.util.UUID;

public record ProductSelectedEvent(@AggregatedEventIdentifier UUID foodCartId, UUID productId, int quantity) {
}
