package ch.wesr.starter.kirkesampleapp.feature.food.infrastructure.rest;


import ch.wesr.starter.kirkesampleapp.AbstractIntegrationTest;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.FoodCart;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.ConfirmFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.CreateFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.DeselectProductCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.SelectedProductCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.infrastructure.persistence.FoodCartView;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureMockMvc
class FoodCartControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void create_valid_food_cart_add_7_products_and_deselect_2_products() throws Exception {
        UUID foodCartId = createFoodCart();
        UUID selectProductUuid = UUID.randomUUID();
        addSelectedProductCommand(foodCartId, selectProductUuid, 1);
        addSelectedProductCommand(foodCartId, selectProductUuid, 6);
        deselectProductCommand(foodCartId, selectProductUuid, 2);
        confirmFoodCart(foodCartId);

        FoodCartView foodCartView = getFoodCartViewBy(foodCartId);

        Assertions.assertThat(foodCartView).isNotNull()
                .extracting(FoodCartView::getFoodCartId, FoodCartView::isConfirmed)
                .contains(foodCartId, true);

        Assertions.assertThat(foodCartView.getProducts())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .containsKey(selectProductUuid)
                .containsValue(5);
    }


    @Test
    void create_valid_food_cart_and_add_twice_same_product() throws Exception {
        UUID foodCartId = createFoodCart();
        UUID selectProductUuid = UUID.randomUUID();
        addSelectedProductCommand(foodCartId, selectProductUuid, 1);
        addSelectedProductCommand(foodCartId, selectProductUuid, 6);
        confirmFoodCart(foodCartId);

        FoodCartView foodCartView = getFoodCartViewBy(foodCartId);

        Assertions.assertThat(foodCartView).isNotNull()
                .extracting(FoodCartView::getFoodCartId, FoodCartView::isConfirmed)
                .contains(foodCartId, true);

        Assertions.assertThat(foodCartView.getProducts())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .containsKey(selectProductUuid)
                .containsValue(7);
    }

    @Test
    void create_valid_food_cart_and_query_for() throws Exception {
        UUID foodCartId = createFoodCart();
        UUID selectProductUuid = UUID.randomUUID();
        addSelectedProductCommand(foodCartId, selectProductUuid, 2);
        confirmFoodCart(foodCartId);

        FoodCartView foodCartView = getFoodCartViewBy(foodCartId);

        Assertions.assertThat(foodCartView).isNotNull()
                .extracting(FoodCartView::getFoodCartId, FoodCartView::isConfirmed)
                .contains(foodCartId, true);

        Assertions.assertThat(foodCartView.getProducts())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .containsKey(selectProductUuid)
                .containsValue(2);


    }

    private FoodCartView getFoodCartViewBy(UUID foodCartId) throws Exception {
        String contentAsString = this.mockMvc.perform(
                        get("/api/foodcart/" + foodCartId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(contentAsString, FoodCartView.class);
    }

    private void confirmFoodCart(UUID foodCartId) throws Exception {
        ConfirmFoodCartCommand confirmFoodCartCommand = new ConfirmFoodCartCommand(foodCartId);

        // when foodCart is confirmed
        this.mockMvc.perform(
                        post("/api/foodcart/confirm")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asJsonString(confirmFoodCartCommand)))
                .andExpect(status().isOk());
    }

    private void addSelectedProductCommand(UUID foodCartId, UUID selectProductUuid, Integer quantity) throws Exception {
        SelectedProductCommand selectedProductCommand = new SelectedProductCommand(foodCartId, selectProductUuid, quantity);
        // when
        this.mockMvc.perform(
                        post("/api/foodcart/product/add")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asJsonString(selectedProductCommand)))
                .andExpect(status().isOk());

    }


    private void deselectProductCommand(UUID foodCartId, UUID selectProductUuid, int quantity) throws Exception {
        DeselectProductCommand deselectProductCommand = new DeselectProductCommand(foodCartId, selectProductUuid, quantity);
        this.mockMvc.perform(
                        post("/api/foodcart/product/deselect")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asJsonString(deselectProductCommand)))
                .andExpect(status().isOk());
    }

    @Test
    void create_food_cart_and_add_selected_product_and_confirm_valid() throws Exception {
        // given
        UUID uuid = createFoodCart();

        UUID selectProductUuid = UUID.randomUUID();
        SelectedProductCommand selectedProductCommand = new SelectedProductCommand(uuid, selectProductUuid, 1);
        addSelectedProductCommand(uuid, selectedProductCommand);

        ConfirmFoodCartCommand confirmFoodCartCommand = new ConfirmFoodCartCommand(uuid);

        // when foodCart is confirmed
        this.mockMvc.perform(
                        post("/api/foodcart/confirm")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asJsonString(confirmFoodCartCommand)))
                .andExpect(status().isOk());

        // then
        log.info("EventRepository: {}", eventRepository);
        Optional<Object> byTargetIdentifier = eventRepository.findByTargetIdentifier(uuid);
        FoodCart foodCart = (FoodCart) byTargetIdentifier.get();
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
        SelectedProductCommand selectedProductCommand = new SelectedProductCommand(uuid, selectProductUuid, 1);
        // when
        FoodCart foodCart = addSelectedProductCommand(uuid, selectedProductCommand);
        // then
        Assertions.assertThat(foodCart).isNotNull()
                .extracting(FoodCart::getFoodCartId, FoodCart::isConfirmed)
                .contains(uuid, false);

    }

    private FoodCart addSelectedProductCommand(UUID uuid, SelectedProductCommand selectedProductCommand) throws Exception {
        // when
        this.mockMvc.perform(
                        post("/api/foodcart/product/add")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(asJsonString(selectedProductCommand)))
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
        UUID uuid = UUID.randomUUID();
        CreateFoodCartCommand command = new CreateFoodCartCommand(uuid);
        String contentAsString = this.mockMvc.perform(
                post("/api/foodcart/create")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(command)))
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
