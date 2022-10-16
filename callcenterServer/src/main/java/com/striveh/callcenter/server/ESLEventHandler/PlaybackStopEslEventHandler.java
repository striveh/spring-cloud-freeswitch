package com.striveh.callcenter.server.ESLEventHandler;

import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import link.thingscloud.freeswitch.esl.transport.message.EslMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@EslEventName(EventNames.PLAYBACK_STOP)
@Component
public class PlaybackStopEslEventHandler implements EslEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());
    @Autowired
    InboundClient inboundClient;
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("PlaybackStopEslEventHandler handle addr[{}] EslEvent[{}].", addr, EslHelper.formatEslEvent(event));
        if (event.getEventHeaders().get("Playback-File-Path").contains("bye")){
            EslMessage eslMessage = inboundClient.sendSyncApiCommand(addr, "uuid_kill",event.getEventHeaders().get("Caller-Unique-ID"));
            log.info("坐席{}挂断{}结果{}",event.getEventHeaders().get("Other-Leg-Destination-Number"),event.getEventHeaders().get("Caller-Unique-ID"), EslHelper.formatEslMessage(eslMessage));
        }else if (event.getEventHeaders().get("Playback-File-Path").contains("evaluation")){

        }else {

        }
    }
}
