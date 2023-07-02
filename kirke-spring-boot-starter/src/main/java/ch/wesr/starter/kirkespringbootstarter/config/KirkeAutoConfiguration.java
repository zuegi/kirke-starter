package ch.wesr.starter.kirkespringbootstarter.config;

import ch.wesr.starter.kirkespringbootstarter.bus.KirkeEventBus;
import ch.wesr.starter.kirkespringbootstarter.bus.handler.DomainHandler;
import ch.wesr.starter.kirkespringbootstarter.bus.handler.KirkeDomainEventHandler;
import ch.wesr.starter.kirkespringbootstarter.bus.handler.ViewHandler;
import ch.wesr.starter.kirkespringbootstarter.bus.impl.KirkeInlineEventBusImpl;
import ch.wesr.starter.kirkespringbootstarter.bus.impl.KirkeSolaceEventBusImpl;
import ch.wesr.starter.kirkespringbootstarter.bus.impl.KirkeMessageConsumer;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.EventRepository;
import ch.wesr.starter.kirkespringbootstarter.eventsourcing.impl.EventRepositoryImpl;
import ch.wesr.starter.kirkespringbootstarter.gateway.SpringContext;
import ch.wesr.starter.kirkespringbootstarter.gateway.command.CommandGateway;
import ch.wesr.starter.kirkespringbootstarter.gateway.query.QueryGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solace.labs.spring.boot.autoconfigure.SolaceJavaAutoConfiguration;
import com.solacesystems.jcsmp.InvalidPropertiesException;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.SpringJCSMPFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration
@ConditionalOnClass({CommandGateway.class, QueryGateway.class, KirkeEventBus.class})
@Import({SolaceJavaAutoConfiguration.class})
public class KirkeAutoConfiguration {

    private final ApplicationContext context;
    private final SpringJCSMPFactory solaceFactory;

    private final ObjectMapper objectMapper;

    public KirkeAutoConfiguration(ApplicationContext context, SpringJCSMPFactory solaceFactory, ObjectMapper objectMapper) {
        this.context = context;
        this.solaceFactory = solaceFactory;
        this.objectMapper = objectMapper;
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
    @ConditionalOnProperty(prefix = "kirke", name = "event", havingValue = "solace")
    protected KirkeEventBus kirkeSolaceEventBus() {
        KirkeEventBus kirkeEventBus = new KirkeSolaceEventBusImpl(jcsmpSession(), objectMapper);
        log.debug("KirkeEventBus: {} has been started", kirkeEventBus);
        return kirkeEventBus;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "kirke", name = "event", havingValue = "inline")
    protected KirkeEventBus kirkeInlineEventBus() {
        KirkeEventBus kirkeEventBus = new KirkeInlineEventBusImpl(domainHandler(), viewHandler());
        log.debug("KirkeEventBus: {} has been started", kirkeEventBus);
        return kirkeEventBus;
    }


    @Bean
    @ConditionalOnProperty(prefix = "kirke", name = "event", havingValue = "inline")
    protected DomainHandler domainHandler() {
        DomainHandler domainHandler =  new DomainHandler(objectMapper);
        log.debug("Inline DomainHandler: {} has been started", domainHandler);
        return domainHandler;
    }

    @Bean
    @ConditionalOnProperty(prefix = "kirke", name = "event", havingValue = "solace")
    protected JCSMPSession jcsmpSession() {
        try {
            return solaceFactory.createSession();
        } catch (InvalidPropertiesException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean(name = KirkeDomainEventHandler.BEAN_NAME)
    @ConditionalOnProperty(prefix = "kirke", name = "event", havingValue = "solace")
    protected KirkeDomainEventHandler kirkeDomainEventHandler() {
        KirkeDomainEventHandler kirkeDomainEventHandler = new KirkeDomainEventHandler(jcsmpSession(), objectMapper);
        log.debug("KirkeDomainEventHandler: {} has been started", kirkeDomainEventHandler);
        return kirkeDomainEventHandler;
    }

    @Bean
    @ConditionalOnProperty(prefix = "kirke", name = "event", havingValue = "solace")
    protected KirkeMessageConsumer kirkeMessageConsumer() {
        KirkeMessageConsumer kirkeMessageConsumer = new KirkeMessageConsumer(jcsmpSession(), objectMapper, eventRepository());
        log.debug("KirkeMessageConsumer: {} has been started", kirkeMessageConsumer);
        return kirkeMessageConsumer;
    }

    @Bean
    @ConditionalOnMissingBean
    protected ViewHandler viewHandler() {
        ViewHandler viewHandler = new ViewHandler(objectMapper);
        log.debug("ViewHandler: {} has been started", viewHandler);
        return viewHandler;
    }
}
