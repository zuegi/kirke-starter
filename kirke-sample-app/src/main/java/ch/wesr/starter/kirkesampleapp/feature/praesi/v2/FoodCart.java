package ch.wesr.starter.kirkesampleapp.feature.praesi.v2;

import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.CreateFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.DeselectProductCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.SelectedProductCommand;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Aggregate
@Data
@Entity
public class FoodCart {

    @Id
    @Column(name = "foodCartId", updatable = false, nullable = false)
    private UUID foodCartId;
    @ElementCollection
    private Map<UUID, Integer> selectedProducts;
    private boolean confirmed;

    public static FoodCart create(CreateFoodCartCommand createCommand) {
        FoodCart foodCart = new FoodCart();
        foodCart.foodCartId = createCommand.foodCartId();
        foodCart.confirmed = false;
        foodCart.selectedProducts = new HashMap<>();
        return foodCart;
    }

    public void addSelectedProduct(SelectedProductCommand selectedCommand) {
        selectedProducts.merge(selectedCommand.productId(), selectedCommand.quantity(), Integer::sum);
    }

    public void deSelectedProduct(DeselectProductCommand deselectCommand) {
        selectedProducts.computeIfPresent(
                deselectCommand.productId(),
                (productId, quantity) -> quantity -= deselectCommand.quantity()
        );
    }
}
