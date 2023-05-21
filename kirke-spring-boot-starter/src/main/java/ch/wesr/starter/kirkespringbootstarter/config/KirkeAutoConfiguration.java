package ch.wesr.starter.kirkespringbootstarter.config;

import ch.wesr.starter.kirkespringbootstarter.bus.KirkeEventBus;
import ch.wesr.starter.kirkespringbootstarter.bus.impl.KirkeEventBusImpl;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.EventRepository;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.impl.EventRepositoryImpl;
import ch.wesr.starter.kirkespringbootstarter.gateway.SpringContext;
import ch.wesr.starter.kirkespringbootstarter.gateway.command.CommandGateway;
import ch.wesr.starter.kirkespringbootstarter.gateway.query.QueryGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnClass({CommandGateway.class, QueryGateway.class, KirkeEventBus.class})
public class KirkeAutoConfiguration {

    final
    ApplicationContext context;

    public KirkeAutoConfiguration(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    @ConditionalOnMissingBean
    protected SpringContext springContext() {
        return new SpringContext();
    }

    @Bean
    @ConditionalOnMissingBean
    protected EventRepository eventRepository() {
        EventRepositoryImpl eventRepositoryImpl = new EventRepositoryImpl();
        log.debug("EventRepository: {} has been started", eventRepositoryImpl);
        return eventRepositoryImpl;
    }

    @Bean
    @ConditionalOnMissingBean
    protected CommandGateway commandGateway() {
        CommandGateway commandGateway = new CommandGateway(eventRepository());
        log.debug("CommandGateway: {} has been started", commandGateway);
        return commandGateway;
    }

    @Bean
    @ConditionalOnMissingBean
    protected QueryGateway queryGateway() {
        QueryGateway queryGateway = new QueryGateway();
        log.debug("QueryGateway: {} has been started", queryGateway);
        return queryGateway;
    }

    @Bean
    @ConditionalOnMissingBean
    protected KirkeEventBus kirkeEventBus() {
        KirkeEventBus kirkeEventBus = new KirkeEventBusImpl(context);
        log.debug("KirkeEventBus: {} has been started", kirkeEventBus);
        return kirkeEventBus;
    }
}
