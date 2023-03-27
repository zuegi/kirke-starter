package ch.wesr.starter.kirkespringbootstarter.config;

import ch.wesr.starter.kirkespringbootstarter.eventsourcing.EventRepository;
import ch.wesr.starter.kirkespringbootstarter.gateway.command.CommandGateway;
import ch.wesr.starter.kirkespringbootstarter.gateway.query.QueryGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnClass({CommandGateway.class, QueryGateway.class})
public class KirkeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    EventRepository eventRepository() {
        log.info("EventRespository has been started");
        return new EventRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    CommandGateway commandGateway() {
        log.info("CommandGateway has been started");
        return new CommandGateway(eventRepository());
    }

    @Bean
    @ConditionalOnMissingBean
    QueryGateway queryGateway() {
        log.info("QueryGateway has been started");
        return new QueryGateway();
    }
}
