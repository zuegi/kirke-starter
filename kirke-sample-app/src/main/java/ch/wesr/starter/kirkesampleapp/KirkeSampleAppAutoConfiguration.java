package ch.wesr.starter.kirkesampleapp;

import ch.wesr.starter.kirkespringbootstarter.config.KirkeAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(KirkeAutoConfiguration.class)
public class KirkeSampleAppAutoConfiguration {
}
