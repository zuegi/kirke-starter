package ch.wesr.starter.kirkespringbootstarter.bus;

public interface EventSubscriber {
    void handleEvent(Object event);
}
