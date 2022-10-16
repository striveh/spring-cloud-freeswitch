package com.striveh.callcenter.server.callcenter.controller;

import com.striveh.callcenter.server.callcenter.service.CallTaskService;
import com.striveh.callcenter.common.util.JsonTool;
import com.striveh.callcenter.common.util.StringTool;
import com.striveh.callcenter.feignclient.freeswitch.IUserInfoServiceApi;
import com.striveh.callcenter.pojo.callcenter.CallTaskPojo;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import link.thingscloud.freeswitch.esl.InboundClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/websocket")
public class WebSocketTestController {
    protected Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private SimpMessageSendingOperations msgOperations;
    @Autowired
    private CallTaskService callTaskService;
    @Autowired
    private IUserInfoServiceApi userInfoServiceApi;
    @Autowired
    private InboundClient inboundClient;

    @GetMapping("/invite")
    public void invite(String telNo,String username) {
        Map<String,String> body=new HashMap<>();
        body.put("telNo",telNo);
        body.put("callId", StringTool.getSerno());
        body.put("callTaskCode", StringTool.getRandom(4));
        msgOperations.convertAndSend("/topic/invite/" + username, JsonTool.getJsonString(body));
    }

    @GetMapping("/bye")
    public void bye(String telNo,String username) {
        Map<String,String> body=new HashMap<>();
        body.put("telNo",telNo);
        body.put("callId", StringTool.getSerno());
        body.put("callTaskCode", StringTool.getRandom(4));
        msgOperations.convertAndSend("/topic/bye/" + username, JsonTool.getJsonString(body));
    }

    @GetMapping("/status")
    public void status(String status,String username) {
        UserinfoPojo userinfoPojo= this.userInfoServiceApi.getByUsername(username);
        userinfoPojo.setWorkStatus(Integer.valueOf(status));
        msgOperations.convertAndSend("/topic/status/" + username, userinfoPojo);
    }

    @GetMapping("/task")
    public void task(String projectCode) {
        CallTaskPojo callTaskPojo = new CallTaskPojo();
        callTaskPojo.setProjectCode(projectCode);
        msgOperations.convertAndSend("/topic/task/" + projectCode, this.callTaskService.selectUnique(callTaskPojo));
    }

    @GetMapping("/notify")
    public void notify(String projectCode,String content) {
        msgOperations.convertAndSend("/topic/notify/" + projectCode, content);
    }

}
