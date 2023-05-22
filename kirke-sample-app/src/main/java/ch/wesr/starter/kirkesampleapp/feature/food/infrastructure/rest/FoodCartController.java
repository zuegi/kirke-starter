package ch.wesr.starter.kirkesampleapp.feature.food.infrastructure.rest;


import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.ConfirmFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.CreateFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.DeselectProductCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.SelectedProductCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.query.FindFoodCartQuery;
import ch.wesr.starter.kirkesampleapp.feature.food.infrastructure.persistence.FoodCartView;
import ch.wesr.starter.kirkespringbootstarter.gateway.command.CommandGateway;
import ch.wesr.starter.kirkespringbootstarter.gateway.query.QueryGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/api/foodcart", produces = MediaType.APPLICATION_JSON_VALUE)
public class FoodCartController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public FoodCartController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping(value = "/create", consumes = {MediaType.APPLICATION_JSON_VALUE},
    produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UUID> handle(@RequestBody CreateFoodCartCommand createFoodCartCommand) {
        return ResponseEntity.ok(commandGateway.send(createFoodCartCommand));
    }

    @PostMapping(value = "/product/add", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UUID> handle(@RequestBody SelectedProductCommand command) {
        return ResponseEntity.ok(commandGateway.send(command));
    }

    @PostMapping(path = "/product/deselect", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UUID> handle(@RequestBody DeselectProductCommand command) {
        return ResponseEntity.ok(commandGateway.send(command));
    }

    @PostMapping(path = "/confirm",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UUID> handle(@RequestBody ConfirmFoodCartCommand command) {
        return ResponseEntity.ok(commandGateway.send(command));
    }

    @GetMapping(path = "/{foodCartId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<FoodCartView> handle(@PathVariable String foodCartId) {
        return ResponseEntity.ok(
                queryGateway.query(new FindFoodCartQuery(UUID.fromString(foodCartId)), FoodCartView.class));
    }
}
