package com.striveh.callcenter.server.callcenter.callHandler;

import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.transport.message.EslMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CallThread implements Runnable {

    protected Logger log = LogManager.getLogger(this.getClass());

    private InboundClient inboundClient;

    private String callStr;

    public CallThread(InboundClient inboundClient,String callStr) {
        this.inboundClient = inboundClient;
        this.callStr = callStr;
    }

    public InboundClient getInboundClient() {
        return inboundClient;
    }

    public void setInboundClient(InboundClient inboundClient) {
        this.inboundClient = inboundClient;
    }

    public String getCallStr() {
        return callStr;
    }

    public void setCallStr(String callStr) {
        this.callStr = callStr;
    }

    @Override
    public void run() {
        log.info("执行了{},{}",callStr,inboundClient.sendAsyncApiCommand(inboundClient.option().serverOptions().get(0).addr(),"originate",callStr));
    }
}
