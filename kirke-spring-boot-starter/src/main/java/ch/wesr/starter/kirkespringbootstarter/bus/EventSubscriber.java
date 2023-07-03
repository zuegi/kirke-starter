package ch.wesr.starter.kirkespringbootstarter.bus;

import ch.wesr.starter.kirkespringbootstarter.bus.impl.KirkeMessage;

public interface EventSubscriber {
    void handleEvent(KirkeMessage kirkeMessage);
}
