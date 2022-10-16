package com.striveh.callcenter.server.ESLEventHandler.customSubEvent;

import link.thingscloud.freeswitch.esl.transport.event.EslEvent;

public interface ISubEventHandler {
    void handle(String addr, EslEvent event);
}
