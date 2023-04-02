package ch.wesr.starter.kirkesampleapp.feature.food.infrastructure.persistence;


import ch.wesr.starter.kirkesampleapp.feature.food.domain.event.FoodCartCreatedEvent;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.query.FindFoodCartQuery;
import ch.wesr.starter.kirkespringbootstarter.annotation.EventHandler;
import ch.wesr.starter.kirkespringbootstarter.annotation.QueryHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;


@Slf4j
@Component
public class FoodCartProjector {
    private final FoodCartViewRepository repository;

    public FoodCartProjector(FoodCartViewRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void on(FoodCartCreatedEvent event) {
        log.debug("[{}] save {}: {}", event.foodCartId(), event.getClass().getSimpleName(), event);
        FoodCartView foodCartView = new FoodCartView(event.foodCartId(), Collections.emptyMap());
        repository.save(foodCartView);

    }

    @QueryHandler
    public FoodCartView handle(FindFoodCartQuery foodCartQuery) {
        log.debug("[{}]  save {}: {}", foodCartQuery.foodCartId(), foodCartQuery.getClass().getSimpleName(), foodCartQuery);
        return repository.findById(foodCartQuery.foodCartId()).orElse(null);
    }
}
