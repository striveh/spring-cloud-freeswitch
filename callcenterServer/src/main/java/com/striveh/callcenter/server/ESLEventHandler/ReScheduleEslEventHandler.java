package com.striveh.callcenter.server.ESLEventHandler;

import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@EslEventName(EventNames.RE_SCHEDULE)
@Component
public class ReScheduleEslEventHandler implements EslEventHandler {

    private InboundClient inboundClient;
    @Autowired
    private ApplicationContext applicationContext;

    protected Logger log = LogManager.getLogger(this.getClass());

    @Override
    public void handle(String addr, EslEvent event) {
        if (inboundClient==null){
            inboundClient= applicationContext.getBean(InboundClient.class);
        }
        log.info("ReScheduleEslEventHandler handle addr[{}] EslEvent[{}].", addr, event);
//        EslMessage eslMessage = inboundClient.sendSyncApiCommand(addr, "version", null);
//        log.info("{}", EslHelper.formatEslMessage(eslMessage));
    }
}
