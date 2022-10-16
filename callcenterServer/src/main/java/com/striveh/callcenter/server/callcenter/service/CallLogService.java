/** */
package com.striveh.callcenter.server.callcenter.service;

import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.server.callcenter.dao.CallLogDao;
import com.striveh.callcenter.pojo.callcenter.CallLogPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallLogService;

import java.util.List;

/**
 * @功能:【callLog 呼叫日志表】Service
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-04-06 12:13:47
 * @说明：<pre></pre>
 */
@Service
public class CallLogService extends BaseService<CallLogPojo, CallLogDao> implements ICallLogService {

    @Override
    public List<CallLogPojo> selectListByCalled(CallLogPojo callLogPojo) {
        return this.dao.selectListByCalled(callLogPojo);
    }
    @Override
    public List<CallLogPojo> selectListByCalling(CallLogPojo callLogPojo) {
        return this.dao.selectListByCalling(callLogPojo);
    }
}
