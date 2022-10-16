package com.striveh.callcenter.feignclient.callcenter;

import com.striveh.callcenter.pojo.callcenter.CallTaskPojo;
import com.striveh.callcenter.pojo.freeswitch.GatewayPojo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient("callcenter")
public interface ICallTaskServiceApi {


    @RequestMapping(value = "/callTask/inner/getListByStatus",method = RequestMethod.GET)
    List<CallTaskPojo> getListByStatus(@RequestParam Integer status);

    @RequestMapping(value = "/callTask/inner/addGateway",method = RequestMethod.POST)
    boolean addGateway(GatewayPojo gatewayPojo);

    @RequestMapping(value = "/callTask/inner/updateGateway",method = RequestMethod.POST)
    boolean updateGateway(GatewayPojo gatewayPojo);
}
