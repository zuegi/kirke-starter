package ch.wesr.starter.kirkesampleapp.feature.praesi.v2;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface FoodCartRepository extends CrudRepository<FoodCart, UUID> {
}
