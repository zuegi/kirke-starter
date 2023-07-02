package ch.wesr.starter.kirkespringbootstarter.bus;

public interface KirkeEventBus {
    void publish(KirkePayLoad kirkePayLoad);
}
