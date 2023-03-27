package ch.wesr.starter.kirkesampleapp.feature.food.domain;


import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.ConfirmFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.CreateFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.SelectProductCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.event.ConfirmedFoodCartEvent;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.event.FoodCartCreatedEvent;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.event.ProductSelectedEvent;
import ch.wesr.starter.kirkespringbootstarter.annotation.Aggregate;
import ch.wesr.starter.kirkespringbootstarter.annotation.CommandHandler;
import ch.wesr.starter.kirkespringbootstarter.annotation.EventSourceHandler;
import ch.wesr.starter.kirkespringbootstarter.gateway.command.AggregateLifeCycle;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@ToString
@Getter
@Aggregate
public class FoodCart {

    // FIXME AggregateIdentifier
    // Aktuell gehen wir davon aus, das an dieser Stelle immer eine UUID als AggregateIdentifier definiert ist
    // was natuerlich spaeter
//    @AggregateIdentfier
    private UUID foodCartId;
    private Map<UUID, Integer> selectedProducts;
    private boolean confirmed;

    /**********************
     * CommandHandler     *
     *********************/
    @CommandHandler
    public void handle(CreateFoodCartCommand command) {
        log.info("Ich bin ein {}: {}",  command.getClass().getSimpleName(), command);
         // create an event
        AggregateLifeCycle.apply(new FoodCartCreatedEvent(command.uuid()));

    }

    @CommandHandler
    public void handle(SelectProductCommand command) {
        log.info("Ich bin ein {}: {}",  command.getClass().getSimpleName(), command);
        AggregateLifeCycle.apply(new ProductSelectedEvent(command.foodCartId(), command.productId(), command.quantity()));
    }

    @CommandHandler
    public void handle(ConfirmFoodCartCommand command) {
        log.info("Ich bin ein {}: {}",  command.getClass().getSimpleName(), command);
        AggregateLifeCycle.apply(new ConfirmedFoodCartEvent(command.foodCartId()));
    }

    /**********************
     * EventSourceHandler *
     *********************/

    // der EventHandler wird dann verwendet um den State des Aggregates zu erstellen
    @EventSourceHandler
    public void on(FoodCartCreatedEvent event) {
        log.info("Ich bin ein {}: {}", event.getClass().getSimpleName(), event);
        foodCartId = event.foodCartId();
        selectedProducts = new HashMap<>();
        confirmed = false;
    }

    @EventSourceHandler
    public void on(ProductSelectedEvent event) {
        log.info("Ich bin ein {}: {}", event.getClass().getSimpleName(), event);
        selectedProducts.merge(event.productId(), event.quantity(), Integer::sum);
    }
    @EventSourceHandler
    public void on(ConfirmedFoodCartEvent event) {
        log.info("Ich bin ein {}: {}", event.getClass().getSimpleName(), event);
        this.confirmed = true;
    }


}
