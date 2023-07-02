package ch.wesr.starter.kirkespringbootstarter.bus;

import ch.wesr.starter.kirkespringbootstarter.bus.handler.ViewHandler;
import ch.wesr.starter.kirkespringbootstarter.bus.impl.KirkeSolaceEventBusImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {KirkeSolaceEventBusImpl.class, ViewHandler.class/*, DomainHandler.class*/})
class KirkeInlineEventBusImplIntegrationTest {


    @Autowired
    KirkeSolaceEventBusImpl eventBus;

    @Test
    void testeMich() {
//        eventBus.publish(new Object());
    }

}
