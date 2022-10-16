/** */
package com.striveh.callcenter.server.callcenter.service.iservice;

import com.striveh.callcenter.common.base.service.iservice.IBaseService;
import com.striveh.callcenter.pojo.callcenter.CallProjectPojo;

import java.util.Map;

/**
 * @功能:【callProject 呼叫项目表】IService
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-04-06 12:13:48
 * @说明：<pre></pre>
 */
public interface ICallProjectService extends IBaseService<CallProjectPojo> {

    void createProject(CallProjectPojo callProjectPojo);
    void endProject(CallProjectPojo callProjectPojo);

    void callBack(Map<String, String> params,String url,String log);

    void eavesdrop(String projectCode,String username, String listenerExt);

    void conference(String projectCode, String listenerExt);
}
