package com.striveh.callcenter.server.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is an implementation for <code>StompSessionHandlerAdapter</code>.
 * Once a connection is established, We subscribe to /topic/messages and 
 * send a sample message to server.
 * 
 * @author Kalyan
 *
 */
public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private Logger logger = LogManager.getLogger(MyStompSessionHandler.class);

    private Integer count=0;
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("New session established : " + session.getSessionId());
        count=count+1;
//        session.subscribe("/topic/call/0000000391", this);
//        session.subscribe("/topic/invite/0000000391", this);
//        session.subscribe("/topic/bye/0000000391", this);
//        session.subscribe("/topic/status/0000000391", this);
//        session.subscribe("/topic/register/0000000391", this);
//        session.subscribe("/topic/task/1", this);
//        session.subscribe("/topic/task/1/0000000391", this);
//        session.subscribe("/topic/notify/1", this);
//        session.subscribe("/topic/notify/1/0000000391", this);
        session.subscribe("/topic/callExtStatus/1", this);
//        session.subscribe("/topic/callTaskStatus/1", this);
        logger.info("Subscribed to /topic/messages");
        session.send("/app/task", getSampleMessage());
        logger.info("Message sent to websocket server");
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Got an exception", exception);
    }
    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        logger.error("Got an error", exception);
    }
    @Override
    public Type getPayloadType(StompHeaders headers) {
        return JSONObject.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        logger.info("Received : " + payload);
    }

    /**
     * A sample message instance.
     * @return instance of <code>Message</code>
     */
    private Map getSampleMessage() {
        Map<String,String> notify = new HashMap<>();
        notify.put("username","0000000391");
        notify.put("projectCode","1");
        return notify;
    }

    public Integer getCounter() {
        return count;
    }
}