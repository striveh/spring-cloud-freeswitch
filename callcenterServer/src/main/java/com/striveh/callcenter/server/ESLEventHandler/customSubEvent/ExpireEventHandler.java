package com.striveh.callcenter.server.ESLEventHandler.customSubEvent;

import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("sofiaexpireEventHandler")
public class ExpireEventHandler implements ISubEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());
    @Override
    public void handle(String addr, EslEvent event) {
        log.info("sofiaexpireEventHandler:{},{}",addr, EslHelper.formatEslEvent(event));

    }
}
