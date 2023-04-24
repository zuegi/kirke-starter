package ch.wesr.starter.kirkespringbootstarter.bus;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {KirkeEventBus.class, ViewHandler.class, DomainHandler.class})
class KirkeEventBusIntegrationTest {


    @Autowired
    KirkeEventBus eventBus;

    @Test
    void testeMich() {
//        eventBus.publish(new Object());
    }

}
