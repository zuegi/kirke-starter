package ch.wesr.starter.kirkespringbootstarter.bus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class KirkeEventSourceConsumer {

    public KirkeEventSourceConsumer() {
        log.debug(this.getClass().getSimpleName() +" has been started");
    }


    @Bean
    public Consumer<String> eventSourceConsumer() {

        return v-> {
            log.info("Received: " + v);
        };
    }
}
