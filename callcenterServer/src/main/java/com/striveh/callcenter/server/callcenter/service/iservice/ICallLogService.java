/** */
package com.striveh.callcenter.server.callcenter.service.iservice;

import com.striveh.callcenter.common.base.service.iservice.IBaseService;
import com.striveh.callcenter.pojo.callcenter.CallLogPojo;

import java.util.List;

/**
 * @功能:【callLog 呼叫日志表】IService
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-04-06 12:13:47
 * @说明：<pre></pre>
 */
public interface ICallLogService extends IBaseService<CallLogPojo> {
    public List<CallLogPojo> selectListByCalled(CallLogPojo callLogPojo);
    public List<CallLogPojo> selectListByCalling(CallLogPojo callLogPojo);
}
