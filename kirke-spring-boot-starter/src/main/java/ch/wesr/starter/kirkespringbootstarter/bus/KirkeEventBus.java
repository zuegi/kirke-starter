package ch.wesr.starter.kirkespringbootstarter.bus;

import ch.wesr.starter.kirkespringbootstarter.bus.impl.KirkeMessage;

public interface KirkeEventBus {
    void publish(KirkeMessage kirkeMessage);
}
