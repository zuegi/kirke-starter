package ch.wesr.starter.kirkesampleapp.feature.food.domain.event;


import ch.wesr.starter.kirkespringbootstarter.annotation.AggregatedEventIdentifier;

import java.util.UUID;

public record ConfirmedFoodCartEvent(@AggregatedEventIdentifier UUID foodCartId) {
}
