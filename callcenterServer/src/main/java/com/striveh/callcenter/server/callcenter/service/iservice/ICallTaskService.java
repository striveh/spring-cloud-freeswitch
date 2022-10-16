/** */
package com.striveh.callcenter.server.callcenter.service.iservice;

import com.striveh.callcenter.common.base.service.iservice.IBaseService;
import com.striveh.callcenter.pojo.callcenter.CallProjectPojo;
import com.striveh.callcenter.pojo.callcenter.CallTaskPojo;
import com.striveh.callcenter.pojo.calllist.CallListPojo;
import com.striveh.callcenter.pojo.freeswitch.GatewayPojo;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;

import java.util.List;
import java.util.Map;

/**
 * @功能:【callTask 呼叫任务表】IService
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-04-06 12:13:48
 * @说明：<pre></pre>
 */
public interface ICallTaskService extends IBaseService<CallTaskPojo> {

    void getCallList(CallTaskPojo callTask);
    void additionalCallList(CallTaskPojo callTask);
    void start(CallTaskPojo callTask);
    void pause(CallTaskPojo callTask);
    void end(CallTaskPojo callTask);
    void updateCacheAfterPorjectEnd(CallTaskPojo callTask);
    List<UserinfoPojo> getUserinfoPojos(CallProjectPojo callProjectPojo);

    void reloadGateway(GatewayPojo gatewayPojo);

    List<CallTaskPojo> getProcList(CallTaskPojo callTaskPojo);

    CallTaskPojo getProc(CallTaskPojo callTaskPojo);

    void notifyCount(CallTaskPojo e,String uncallListSyncStatus);
    void notifyAgentStatus(UserinfoPojo userinfoPojo);

    void callTimeout(CallTaskPojo e);

    void setCallParams(Integer rateLimit,Long sleep,Integer originateTimeout);

    void previewCall(String params);

    List<CallListPojo> getCallList(CallTaskPojo callTaskPojo, Integer count);

    void agentCall(Map<String, String> map, String username, String telNo);
}
