package ch.wesr.starter.kirkesampleapp.feature.food.shared;


import ch.wesr.starter.kirkesampleapp.AbstractIntegrationTest;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.CreateFoodCartCommand;
import ch.wesr.starter.kirkesampleapp.feature.food.domain.command.SelectedProductCommand;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.impl.EventRepositoryImpl;
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
    EventRepositoryImpl eventRepositoryImpl;

    @Test
    void assert_uuid_is_the_same() {
        log.info("!!!!!!!!!!!!!!!!!!!!! Start assert_uuid_is_the_same Test !!!!!!!!!!!!!!!!!!!!");
        log.info("EventRepository: {}", eventRepositoryImpl);
        log.info("CommandGateway: {}", commandGateway);
        UUID foodCartId = UUID.randomUUID();
        CreateFoodCartCommand createFoodCartCommand = new CreateFoodCartCommand(foodCartId);
        String foodCartIdAsString = commandGateway.send(createFoodCartCommand);
        Assertions.assertThat(foodCartId.toString()).isEqualTo(foodCartIdAsString);
        log.info("!!!!!!!!!!!!!!!!!!!!! End assert_uuid_is_the_same Test !!!!!!!!!!!!!!!!!!!!");
    }

    @Test
    void testemich() {
        log.info("!!!!!!!!!!!!!!!!!!!!! Start testemich Test !!!!!!!!!!!!!!!!!!!!");
        log.info("EventRepository: {}", eventRepositoryImpl);
        log.info("CommandGateway: {}", commandGateway);
        UUID foodCartId = UUID.randomUUID();
        CreateFoodCartCommand createFoodCartCommand = new CreateFoodCartCommand(foodCartId);
        String foodCartIdAsString = commandGateway.send(createFoodCartCommand);

        SelectedProductCommand selectedProductCommand = new SelectedProductCommand(foodCartId, UUID.randomUUID(), 1);
        String stillTheSameFoodCartIdAsString = commandGateway.send(selectedProductCommand);

        Assertions.assertThat(foodCartId.toString()).isEqualTo(stillTheSameFoodCartIdAsString);


        Optional<Object> byTargetIdentifier = eventRepositoryImpl.findByTargetIdentifier(foodCartId);
        Assertions.assertThat(byTargetIdentifier).isPresent();

        Assertions.assertThat(byTargetIdentifier.get())
                .isNotNull()
                .extracting("foodCartId")
                .isEqualTo(foodCartId);

        log.info("!!!!!!!!!!!!!!!!!!!!! Ende testemich Test !!!!!!!!!!!!!!!!!!!!");
    }
}
