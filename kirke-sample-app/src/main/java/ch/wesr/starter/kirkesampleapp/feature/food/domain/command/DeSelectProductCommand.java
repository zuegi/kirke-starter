package ch.wesr.starter.kirkesampleapp.feature.food.domain.command;


import ch.wesr.starter.kirkespringbootstarter.annotation.TargetAggregateIdentifier;

import java.util.UUID;

public record DeSelectProductCommand(@TargetAggregateIdentifier UUID foodCartId, UUID productId, int quantity){
}
