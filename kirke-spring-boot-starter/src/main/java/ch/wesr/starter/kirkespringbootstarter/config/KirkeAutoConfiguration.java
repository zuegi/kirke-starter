package ch.wesr.starter.kirkespringbootstarter.config;

import ch.wesr.starter.kirkespringbootstarter.bus.KirkeEventBus;
import ch.wesr.starter.kirkespringbootstarter.bus.KirkeEventSourceConsumer;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.EventRepository;
import ch.wesr.starter.kirkespringbootstarter.gateway.SpringContext;
import ch.wesr.starter.kirkespringbootstarter.gateway.command.CommandGateway;
import ch.wesr.starter.kirkespringbootstarter.gateway.query.QueryGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnClass({CommandGateway.class, QueryGateway.class, KirkeEventBus.class, KirkeEventSourceConsumer.class})
public class KirkeAutoConfiguration {

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    SpringContext springContext() {
        return new SpringContext();
    }

    @Bean
    @ConditionalOnMissingBean
    EventRepository eventRepository() {
        EventRepository eventRepository = new EventRepository();
        log.debug("EventRepository: {} has been started", eventRepository);
        return eventRepository;
    }

    @Bean
    @ConditionalOnMissingBean
    CommandGateway commandGateway() {
        CommandGateway commandGateway = new CommandGateway(eventRepository());
        log.debug("CommandGateway: {} has been started", commandGateway);
        return commandGateway;
    }

    @Bean
    @ConditionalOnMissingBean
    QueryGateway queryGateway() {
        QueryGateway queryGateway = new QueryGateway();
        log.debug("QueryGateway: {} has been started", queryGateway);
        return queryGateway;
    }

    @Bean
    @ConditionalOnMissingBean
    KirkeEventBus kirkeEventBus() {
        KirkeEventBus kirkeEventBus = new KirkeEventBus(applicationContext);
        log.debug("KirkeEventBus: {} has been started", kirkeEventBus);
        return kirkeEventBus;
    }

    @Bean
    @ConditionalOnMissingBean
    KirkeEventSourceConsumer kirkeEventSourceConsumer() {
        KirkeEventSourceConsumer kirkeEventSourceConsumer = new KirkeEventSourceConsumer();
        return kirkeEventSourceConsumer;
    }
}
