package ch.wesr.starter.kirkesampleapp.feature.food.shared;


import ch.wesr.starter.kirkesampleapp.AbstractIntegrationTest;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.CreateFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.SelectedProductCommand;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.EventRepository;
import ch.wesr.starter.kirkespringbootstarter.gateway.command.CommandGateway;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;


@Slf4j
class CommandGatewayIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    CommandGateway commandGateway;

    @Autowired
    EventRepository eventRepository;

    @Test
    void assert_uuid_is_the_same() {
        log.info("EventRepository: {}", eventRepository);
        log.info("CommandGateway: {}", commandGateway);
        UUID foodCartId = UUID.randomUUID();
        CreateFoodCartCommand createFoodCartCommand = new CreateFoodCartCommand(foodCartId);
        UUID foodCartIdFromGateway = commandGateway.send(createFoodCartCommand);
        Assertions.assertThat(foodCartId).isEqualTo(foodCartIdFromGateway);
    }

    @Test
    void assert_uuid_is_the_same_after_added_select_product_command() {
        log.info("EventRepository: {}", eventRepository);
        log.info("CommandGateway: {}", commandGateway);
        UUID foodCartId = UUID.randomUUID();
        CreateFoodCartCommand createFoodCartCommand = new CreateFoodCartCommand(foodCartId);
        commandGateway.send(createFoodCartCommand);

        SelectedProductCommand selectedProductCommand = new SelectedProductCommand(foodCartId, UUID.randomUUID(), 1);
        UUID stillTheSameFoodCartIdFromGateway = commandGateway.send(selectedProductCommand);

        Assertions.assertThat(foodCartId).isEqualTo(stillTheSameFoodCartIdFromGateway);


        Optional<Object> byTargetIdentifier = eventRepository.findByTargetIdentifier(foodCartId);
        Assertions.assertThat(byTargetIdentifier).isPresent();

        Assertions.assertThat(byTargetIdentifier.get())
                .isNotNull()
                .extracting("foodCartId")
                .isEqualTo(foodCartId);

    }
}
