package com.striveh.callcenter.server.ESLEventHandler;

import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@EslEventName(EventNames.CHANNEL_CALLSTATE)
@Component
public class ChannelCallStateEslEventHandler implements EslEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("ChannelCallStateEslEventHandler handle addr[{}] EslEvent[{}].", addr, event);
    }
}
