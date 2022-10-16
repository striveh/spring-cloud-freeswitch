package com.striveh.callcenter.feignclient.freeswitch;

import com.striveh.callcenter.pojo.freeswitch.GatewayPojo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



@FeignClient("freeswitch")
public interface IGatewayServiceApi {


    @RequestMapping(value = "/gateway/inner/update",method = RequestMethod.POST)
    void update(GatewayPojo gatewayPojo);

    @RequestMapping(value = "/gateway/inner/getByCode",method = RequestMethod.GET)
    GatewayPojo getByCode(@RequestParam String gwCode);

    @RequestMapping(value = "/gateway/inner/getReturnVisitGw",method = RequestMethod.GET)
    GatewayPojo getReturnVisitGw();

}
