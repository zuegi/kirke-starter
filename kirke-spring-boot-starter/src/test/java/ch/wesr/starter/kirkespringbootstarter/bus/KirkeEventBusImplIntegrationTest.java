package ch.wesr.starter.kirkespringbootstarter.bus;

import ch.wesr.starter.kirkespringbootstarter.bus.handler.DomainHandler;
import ch.wesr.starter.kirkespringbootstarter.bus.handler.ViewHandler;
import ch.wesr.starter.kirkespringbootstarter.bus.impl.KirkeEventBusImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {KirkeEventBusImpl.class, ViewHandler.class, DomainHandler.class})
class KirkeEventBusImplIntegrationTest {


    @Autowired
    KirkeEventBusImpl eventBus;

    @Test
    void testeMich() {
//        eventBus.publish(new Object());
    }

}
