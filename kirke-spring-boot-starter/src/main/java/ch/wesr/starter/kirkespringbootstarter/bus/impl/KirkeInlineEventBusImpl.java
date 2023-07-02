package ch.wesr.starter.kirkespringbootstarter.bus.impl;

import ch.wesr.starter.kirkespringbootstarter.bus.KirkeEventBus;
import ch.wesr.starter.kirkespringbootstarter.bus.KirkePayLoad;
import ch.wesr.starter.kirkespringbootstarter.bus.handler.DomainHandler;
import ch.wesr.starter.kirkespringbootstarter.bus.handler.ViewHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KirkeInlineEventBusImpl implements KirkeEventBus {

    public final static String BEAN_NAME = "domainHandler";

    private final DomainHandler domainHandler;
    private final ViewHandler viewHandler;

    public KirkeInlineEventBusImpl(DomainHandler domainHandler, ViewHandler viewHandler) {
        this.domainHandler = domainHandler;
        this.viewHandler = viewHandler;
    }

    @Override
    public void publish(KirkePayLoad kirkePayLoad) {
        log.debug("Sending payload: {}", kirkePayLoad);
        domainHandler.handleEvent(kirkePayLoad);
        viewHandler.handleEvent(kirkePayLoad);
    }
}
