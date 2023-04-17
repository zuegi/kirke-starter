package ch.wesr.starter.kirkesampleapp.feature.praesi.v2;

import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.CreateFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.DeselectProductCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.SelectedProductCommand;
import ch.wesr.starter.kirkesampleapp.feature.praesi.v1.FoodCart;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class FoodCartUnitTest {
    @Test
    void createFoodCart() {
        UUID selectedProductId= UUID.randomUUID();
        CreateFoodCartCommand createFoodCartCommand = new CreateFoodCartCommand(selectedProductId);
        ch.wesr.starter.kirkesampleapp.feature.praesi.v1.FoodCart foodCart = ch.wesr.starter.kirkesampleapp.feature.praesi.v1.FoodCart.create(createFoodCartCommand);
        SelectedProductCommand selectedProductCommand = new SelectedProductCommand(createFoodCartCommand.foodCartId(), selectedProductId, 2);
        foodCart.addSelectedProduct(selectedProductCommand);

        Assertions.assertThat(foodCart).isNotNull().extracting(FoodCart::getFoodCartId).isEqualTo(createFoodCartCommand.foodCartId());
        Assertions.assertThat(foodCart.getSelectedProducts())
                .isNotEmpty()
                .containsEntry(selectedProductId, 2);

        SelectedProductCommand anotherSelectedProductCommand = new SelectedProductCommand(createFoodCartCommand.foodCartId(), selectedProductId, 3);
        foodCart.addSelectedProduct(anotherSelectedProductCommand);

        Assertions.assertThat(foodCart.getSelectedProducts())
                .isNotEmpty()
                .containsEntry(selectedProductId, 5);

        DeselectProductCommand removeSelectProductCommand = new DeselectProductCommand(createFoodCartCommand.foodCartId(), selectedProductCommand.productId(), 4);
        foodCart.deSelectedProduct(removeSelectProductCommand);

        Assertions.assertThat(foodCart.getSelectedProducts())
                .isNotEmpty()
                .containsEntry(selectedProductId, 1);
    }

}
