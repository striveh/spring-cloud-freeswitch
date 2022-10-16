package com.striveh.callcenter.server.ESLEventHandler;

import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

@EslEventName(EventNames.RECORD_STOP)
@Component
public class RecordStopEslEventHandler implements EslEventHandler {

    protected Logger log = LogManager.getLogger(this.getClass());

    @Override
    public void handle(String addr, EslEvent event) {
        log.info("RecordStopEslEventHandler handle addr[{}] EslEvent[{}].", addr, EslHelper.formatEslEvent(event));
    }
}
