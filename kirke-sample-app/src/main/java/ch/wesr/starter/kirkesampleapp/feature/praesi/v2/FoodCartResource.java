package ch.wesr.starter.kirkesampleapp.feature.praesi.v2;

import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.CreateFoodCartCommand;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/foodCart", produces = MediaType.APPLICATION_JSON_VALUE)
public class FoodCartResource {

    private final FoodCartService foodCartService;

    public FoodCartResource(FoodCartService foodCartService) {
        this.foodCartService = foodCartService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createFoodCart() {
        UUID uuid = UUID.randomUUID();
        foodCartService.createFoodCart(new CreateFoodCartCommand(uuid));
        return ResponseEntity.ok(uuid.toString());
    }
}
