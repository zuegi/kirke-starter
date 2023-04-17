package ch.wesr.starter.kirkesampleapp.feature.praesi.v2;

import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.CreateFoodCartCommand;
import org.springframework.stereotype.Service;

@Service
public class FoodCartService {

    private final FoodCartRepository foodCartRepository;

    public FoodCartService(FoodCartRepository foodCartRepository) {
        this.foodCartRepository = foodCartRepository;
    }

    public void createFoodCart(CreateFoodCartCommand foodCartCommand) {
        FoodCart foodCart = FoodCart.create(foodCartCommand);
        foodCartRepository.save(foodCart);
    }
}
