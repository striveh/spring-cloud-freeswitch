package com.striveh.callcenter.feignclient.calllist;

import com.striveh.callcenter.pojo.calllist.CallListPojo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("callList")
public interface ICallListServiceApi {

    @RequestMapping(value = "/callList/inner/getListByCallListId",method = RequestMethod.GET)
    List<CallListPojo> getListByCallListId(@RequestParam Long callListId,@RequestParam Integer size,@RequestParam(required = false) Long lastId);
}
