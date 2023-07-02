package ch.wesr.starter.kirkespringbootstarter.bus.handler;

import ch.wesr.starter.kirkespringbootstarter.bus.KirkePayLoad;

public interface EventSubscriber {
    void handleEvent(KirkePayLoad kirkePayLoad);
}
