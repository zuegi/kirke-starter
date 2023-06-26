package ch.wesr.starter.kirkespringbootstarter.bus.handler;

public interface EventSubscriber {
    void handleEvent(Object event);
}
