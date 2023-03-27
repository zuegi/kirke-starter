package ch.wesr.starter.kirkesampleapp.feature.food.infrastructure.rest;


import ch.wesr.starter.kirkesampleapp.feature.food.domain.FoodCart;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.ConfirmFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.SelectProductCommand;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FoodCartControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    EventRepository eventRepository;

    @Test
    void create_food_cart_and_add_selected_product_and_confirm_valid() throws Exception {
        // given
        UUID uuid = createFoodCart();

        UUID selectProductUuid = UUID.randomUUID();
        SelectProductCommand selectProductCommand = new SelectProductCommand(uuid, selectProductUuid, 1);
        addSelectedProductCommand(uuid, selectProductCommand);

        ConfirmFoodCartCommand confirmFoodCartCommand = new ConfirmFoodCartCommand(uuid);

        // when foodCart is confirmed
        this.mockMvc.perform(
                        post("/api/foodcart/confirm")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asJsonString(confirmFoodCartCommand)))
                .andExpect(status().isOk());

        // then
        Optional<Object> byTargetIdentifier = eventRepository.findByTargetIdentifier(uuid);
        FoodCart foodCart =  (FoodCart) byTargetIdentifier.get();
        Assertions.assertThat(foodCart).isNotNull()
                .extracting(FoodCart::getFoodCartId, FoodCart::isConfirmed)
                .contains(uuid, true);

        Assertions.assertThat(foodCart)
                .extracting("selectedProducts")
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .containsEntry(selectProductUuid, 1);
    }

    @Test
    void create_foodcart_and_add_selected_product_valid() throws Exception {
        // given
        UUID uuid = createFoodCart();

        UUID selectProductUuid = UUID.randomUUID();
        SelectProductCommand selectProductCommand = new SelectProductCommand(uuid, selectProductUuid, 1);
        // when
        FoodCart foodCart = addSelectedProductCommand(uuid, selectProductCommand);
        // then
        Assertions.assertThat(foodCart).isNotNull()
                .extracting(FoodCart::getFoodCartId, FoodCart::isConfirmed)
                .contains(uuid, false);

    }

    private FoodCart addSelectedProductCommand(UUID uuid, SelectProductCommand selectProductCommand) throws Exception {
        // when
        this.mockMvc.perform(
                post("/api/foodcart/product/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(selectProductCommand)))
                .andExpect(status().isOk());

        // then
        Optional<Object> byTargetIdentifier = eventRepository.findByTargetIdentifier(uuid);
        return (FoodCart) byTargetIdentifier.get();
    }

    @Test
    void create_foodcart_valid() throws Exception {
        createFoodCart();
    }

    private UUID createFoodCart() throws Exception {
        String contentAsString = this.mockMvc.perform(post("/api/foodcart/create"))
                /*.andDo(print())*/
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertThat(contentAsString).isNotEmpty();

        return UUID.fromString(contentAsString);
    }

    private static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
