package com.striveh.callcenter.server.ESLEventHandler;

import com.striveh.callcenter.common.util.JsonTool;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@EslEventName(EventNames.CHANNEL_ORIGINATE)
@Component
public class ChannelOriginateEslEventHandler implements EslEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    private SimpMessageSendingOperations msgOperations;

    @Override
    public void handle(String addr, EslEvent event) {
        log.info("ChannelOriginateEslEventHandler handle addr[{}] EslEvent[{}].", addr, EslHelper.formatEslEvent(event));
        if (!StringUtils.isEmpty(event.getEventHeaders().get("variable_sip_h_X-Call-Task-Code"))){
            Map<String,String> body = new HashMap<>();

            body.put("telNo",event.getEventHeaders().get("variable_origination_caller_id_number"));
            body.put("callId", event.getEventHeaders().get("variable_fifo_bridge_uuid"));
            body.put("callTaskCode", event.getEventHeaders().get("variable_sip_h_X-Call-Task-Code"));
            msgOperations.convertAndSend("/topic/call/" + event.getEventHeaders().get("Caller-Destination-Number"),  JsonTool.getJsonString(body));
        }
    }
}
