package ch.wesr.starter.kirkesampleapp;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
//@Import({KirkeEventBusImpl.class, ViewHandler.class, DomainHandler.class, KirkeDomainEventHandler.class})
@ImportAutoConfiguration
public class AbstractIntegrationTest {
}
