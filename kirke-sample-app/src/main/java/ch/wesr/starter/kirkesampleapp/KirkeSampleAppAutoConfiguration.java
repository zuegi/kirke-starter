package ch.wesr.starter.kirkesampleapp;

import ch.wesr.starter.kirkespringbootstarter.bus.DomainHandler;
import ch.wesr.starter.kirkespringbootstarter.bus.KirkeEventBus;
import ch.wesr.starter.kirkespringbootstarter.bus.ViewHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({KirkeEventBus.class, ViewHandler.class, DomainHandler.class})
public class KirkeSampleAppAutoConfiguration {
}
