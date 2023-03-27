package ch.wesr.starter.kirkesampleapp.feature.food.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Map;
import java.util.UUID;

@Entity
public class FoodCartView {

    public FoodCartView() {
    }

    public FoodCartView(UUID foodCartId, Map<UUID, String> products) {
        this.foodCartId = foodCartId;
        this.products = products;
    }

    @Id
    @Column(name = "foodCartId", updatable = false, nullable = false)
//    @Type(type= "uuid-char")
    private UUID foodCartId;

    @ElementCollection
    private Map<UUID, String> products;

}
