package com.striveh.callcenter.server.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

@Component
public class WebSocketDecoratorFactory implements WebSocketHandlerDecoratorFactory {

    protected Logger log = LogManager.getLogger(this.getClass());

    @Override
    public WebSocketHandler decorate(WebSocketHandler handler) {
        return new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                log.info("有人连接啦  sessionId = {}", session.getId());
                super.afterConnectionEstablished(session);
            }
            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                log.info("有人退出连接啦  sessionId = {}", session.getId());
                super.afterConnectionClosed(session, closeStatus);
            }
        };
    }
}