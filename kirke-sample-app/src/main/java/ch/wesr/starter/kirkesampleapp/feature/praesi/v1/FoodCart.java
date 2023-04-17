package ch.wesr.starter.kirkesampleapp.feature.praesi.v1;

import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.CreateFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.DeselectProductCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.SelectedProductCommand;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Aggregate
@Data
public class FoodCart {
    private UUID foodCartId;
    private Map<UUID, Integer> selectedProducts;
    private boolean confirmed;

    public static FoodCart create(CreateFoodCartCommand createCommand) {
        FoodCart foodCart = new FoodCart();
        foodCart.foodCartId = createCommand.foodCartId();
        foodCart.confirmed = false;
        foodCart.selectedProducts =new HashMap<>();
        return foodCart;
    }

    public void addSelectedProduct(SelectedProductCommand selectedCommand) {
        selectedProducts.merge(selectedCommand.productId(), selectedCommand.quantity(), Integer::sum);
    }

    public void deSelectedProduct(DeselectProductCommand deselectCommand ) {
        selectedProducts.computeIfPresent(
                deselectCommand.productId(),
                (productId, quantity) -> quantity -= deselectCommand.quantity()
        );
    }
}
