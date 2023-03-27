package ch.wesr.starter.kirkesampleapp.feature.food.infrastructure.rest;


import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.ConfirmFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.CreateFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.SelectProductCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.query.FindFoodCartQuery;
import ch.wesr.starter.kirkesampleapp.feature.food.infrastructure.persistence.FoodCartView;
import ch.wesr.starter.kirkespringbootstarter.gateway.command.CommandGateway;
import ch.wesr.starter.kirkespringbootstarter.gateway.query.QueryGateway;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/foodcart", produces = MediaType.APPLICATION_JSON_VALUE)
public class FoodCartController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public FoodCartController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping("/create")
    public ResponseEntity<String> handle() {
        return ResponseEntity.ok(commandGateway.send(new CreateFoodCartCommand(UUID.randomUUID())));
    }

    @PostMapping(value = "/product/add",  consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> handle(@RequestBody SelectProductCommand command) {
        return ResponseEntity.ok(commandGateway.send(command));
    }

    @PostMapping(path = "/confirm")
    public ResponseEntity<String> handle(@RequestBody ConfirmFoodCartCommand command) {
        return ResponseEntity.ok(commandGateway.send(command));
    }

    @GetMapping("/{foodCartId}")
    public ResponseEntity<FoodCartView> handle(@PathVariable String foodCartId) {

         return ResponseEntity.ok(
                 queryGateway.query(new FindFoodCartQuery(UUID.fromString(foodCartId)), FoodCartView.class));


    }
}