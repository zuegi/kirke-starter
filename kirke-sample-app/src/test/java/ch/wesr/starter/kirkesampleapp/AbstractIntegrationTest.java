package ch.wesr.starter.kirkesampleapp;

import ch.wesr.starter.kirkespringbootstarter.bus.DomainHandler;
import ch.wesr.starter.kirkespringbootstarter.bus.KirkeEventBus;
import ch.wesr.starter.kirkespringbootstarter.bus.ViewHandler;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Import({KirkeEventBus.class, ViewHandler.class, DomainHandler.class})
public class AbstractIntegrationTest {
}
