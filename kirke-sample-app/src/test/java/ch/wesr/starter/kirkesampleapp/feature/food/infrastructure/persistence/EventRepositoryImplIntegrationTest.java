package ch.wesr.starter.kirkesampleapp.feature.food.infrastructure.persistence;


import ch.wesr.starter.kirkesampleapp.AbstractIntegrationTest;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.FoodCart;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.event.FoodCartCreatedEvent;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.event.ProductSelectedEvent;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.impl.EventRepositoryImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

class EventRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    EventRepositoryImpl eventRepositoryImpl;

    @Test
    void on() {
        // given
        UUID foodCartCreatedEventId = UUID.randomUUID();
        UUID productSelectedEventID = UUID.randomUUID();


        var foodCartEvent = new FoodCartCreatedEvent(foodCartCreatedEventId);
        var productSelectedEvent = new ProductSelectedEvent(foodCartCreatedEventId, productSelectedEventID, 2);
        eventRepositoryImpl.on(foodCartEvent);
        eventRepositoryImpl.on(productSelectedEvent);

        // when
        Optional<Object> byTargetIdentifier = eventRepositoryImpl.findByTargetIdentifier(foodCartCreatedEventId);
        Assertions.assertThat(byTargetIdentifier.isPresent()).isTrue();
        FoodCart foodCart = (FoodCart) byTargetIdentifier.get();

        // then
        Assertions.assertThat(foodCart)
                .isNotNull()
                .extracting(FoodCart::getFoodCartId, FoodCart::isConfirmed)
                .contains(foodCartCreatedEventId,false);

        Assertions.assertThat(foodCart.getSelectedProducts())
                .containsEntry(productSelectedEventID, 2);

    }

}
