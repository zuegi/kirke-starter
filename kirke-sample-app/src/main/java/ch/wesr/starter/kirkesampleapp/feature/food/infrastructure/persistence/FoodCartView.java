package ch.wesr.starter.kirkesampleapp.feature.food.infrastructure.persistence;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
public class FoodCartView {

    public FoodCartView() {
    }

    public FoodCartView(UUID foodCartId, Map<UUID, Integer> products) {
        this.foodCartId = foodCartId;
        this.products = products;
    }

    @Id
    @Column(name = "foodCartId", updatable = false, nullable = false)
//    @Type(type= "uuid-char")
    private UUID foodCartId;

    @ElementCollection
    private Map<UUID, Integer> products;

    private boolean confirmed;

}
