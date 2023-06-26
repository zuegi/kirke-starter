package ch.wesr.starter.kirkespringbootstarter.bus.handler;

import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PublishEventHandler implements JCSMPStreamingPublishEventHandler {
    @Override
    public void handleError(String messageID, JCSMPException e, long timestamp) {
        log.info("Producer received error for msg: {}@{} - {}%n", messageID, timestamp, e);
    }

    @Override
    public void responseReceived(String messageID) {
        log.info("Producer received response for msg: {}", messageID);
    }
}
