package ch.wesr.starter.kirkesampleapp.feature.food.infrastructure.persistence;


import ch.wesr.starter.kirkesampleapp.feature.food.domain.FoodCart;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.event.FoodCartCreatedEvent;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.event.ProductSelectedEvent;
import ch.wesr.starter.kirkespringbootstarter.bus.KirkePayLoad;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.EventRepository;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.impl.EventRepositoryImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class EventRepositoryImplUnitTest {


    @Test
    void on() {
        // given
        UUID foodCartCreatedEventId = UUID.randomUUID();
        UUID productSelectedEventID = UUID.randomUUID();

        EventRepository eventRepositoryImpl = new EventRepositoryImpl();
        var foodCartEvent = new FoodCartCreatedEvent(foodCartCreatedEventId);
        var productSelectedEvent = new ProductSelectedEvent(foodCartCreatedEventId, productSelectedEventID, 2);
        KirkePayLoad foodCartEventPayLoad = new KirkePayLoad(foodCartEvent.getClass(), foodCartEvent);
        KirkePayLoad productSelectedEventPayLoad = new KirkePayLoad(productSelectedEvent.getClass(), productSelectedEvent);
        eventRepositoryImpl.on(foodCartEventPayLoad);
        eventRepositoryImpl.on(productSelectedEventPayLoad);

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

    @Test
    void testemich() {

        Map<UUID, List<String>> map = new HashMap<>();

        UUID key = UUID.randomUUID();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Ich bin ein String");
        strings.add("Ich bin auch ein String");
        map.put(key, strings);

        map.entrySet().stream().map(me -> "Key: " + me.getKey() + "\n"
                + "Values: " + String.join(", ", me.getValue()))
                .forEach(System.out::print);



    }

}
