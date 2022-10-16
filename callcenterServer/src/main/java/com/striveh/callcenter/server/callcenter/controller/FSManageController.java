package com.striveh.callcenter.server.callcenter.controller;

import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.inbound.option.ServerOption;
import link.thingscloud.freeswitch.esl.transport.message.EslMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fs")
public class FSManageController {

    @GetMapping("/demo")
    public String demo() {
        return "demo";
    }

    @GetMapping("/addServer1")
    public void addServer1(String host, int port) {
        InboundClient.getInstance().option().addServerOption(new ServerOption(host, port));
    }

    @Autowired
    private InboundClient inboundClient;

    @GetMapping("/addServer2")
    public void addServer2(String host, int port) {
        inboundClient.option().addServerOption(new ServerOption(host, port));
    }

    @GetMapping("/removeServer1")
    public void removeServer1() {
        ServerOption serverOption = inboundClient.option().serverOptions().get(0);
        inboundClient.option().removeServerOption(serverOption);
    }

    @GetMapping("/serverOptions")
    public String serverOptions() {
        return inboundClient.option().serverOptions().toString();
    }

    @GetMapping("/api")
    public String api(String cmd,String params, Integer index) {
        EslMessage message = inboundClient.sendSyncApiCommand(inboundClient.option().serverOptions().get(index).addr(),cmd,params);
        return EslHelper.formatEslMessage(message);
    }
}
