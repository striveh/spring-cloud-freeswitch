package com.striveh.callcenter.feignclient.freeswitch;

import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient("freeswitch")
public interface IUserInfoServiceApi {

    @RequestMapping(value = "/userinfo/inner/addList",method = RequestMethod.POST)
    void addList(List<UserinfoPojo> userinfoPojos);

    @RequestMapping(value = "/userinfo/inner/add",method = RequestMethod.POST)
    void add(UserinfoPojo userinfoPojo);

    @RequestMapping(value = "/userinfo/inner/update",method = RequestMethod.POST)
    void update(UserinfoPojo userinfoPojo);

    @RequestMapping(value = "/userinfo/inner/getByUsername",method = RequestMethod.GET)
    UserinfoPojo getByUsername(@RequestParam String username);

    @RequestMapping(value = "/userinfo/inner/getListByProjectId",method = RequestMethod.GET)
    List<UserinfoPojo> getListByProjectId(@RequestParam Long projectId);

    @RequestMapping(value = "/userinfo/inner/getListByProjectIdAndStatus",method = RequestMethod.GET)
    List<UserinfoPojo> getListByProjectIdAndStatus(@RequestParam Long projectId,@RequestParam Integer status);
}
