package ch.wesr.starter.kirkesampleapp.feature.food.infrastructure.persistence;


import ch.wesr.starter.kirkesampleapp.feature.food.domain.event.ConfirmedFoodCartEvent;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.event.FoodCartCreatedEvent;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.event.ProductDeselectedEvent;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.event.ProductSelectedEvent;
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
        FoodCartView foodCartView = new FoodCartView(event.foodCartId(), Collections.EMPTY_MAP );
        repository.save(foodCartView);
    }

    @EventHandler
    public void on(ProductSelectedEvent event) {
        FoodCartView foodCartView = repository.findById(event.foodCartId())
                .orElseThrow(() -> new IllegalArgumentException("FoodCart with foodCartId [e" + event.foodCartId() +"] not found"));
        foodCartView.getProducts().merge(event.productId(), event.quantity(), Integer::sum);
    }

    @EventHandler
    public void on(ProductDeselectedEvent event) {
        FoodCartView foodCartView = repository.findById(event.foodCartId())
                .orElseThrow(() -> new IllegalArgumentException("FoodCart with foodCartId [e" + event.foodCartId() +"] not found"));

        foodCartView.getProducts().computeIfPresent(
                event.productId(),
                (productId, quantity) -> quantity -= event.quantity());
    }

    @EventHandler
    public void on(ConfirmedFoodCartEvent event) {
        FoodCartView foodCartView = repository.findById(event.foodCartId())
                .orElseThrow(() -> new IllegalArgumentException("FoodCart with foodCartId [e" + event.foodCartId() +"] not found"));
        foodCartView.setConfirmed(true);
    }

    @QueryHandler
    public FoodCartView handle(FindFoodCartQuery foodCartQuery) {
        log.debug("[{}]  save {}: {}", foodCartQuery.foodCartId(), foodCartQuery.getClass().getSimpleName(), foodCartQuery);
        return repository.findById(foodCartQuery.foodCartId()).orElse(null);
    }
}
