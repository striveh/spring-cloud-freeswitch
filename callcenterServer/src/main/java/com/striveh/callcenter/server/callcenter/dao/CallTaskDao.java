/** */
package com.striveh.callcenter.server.callcenter.dao;

import org.springframework.stereotype.Repository;
import com.striveh.callcenter.common.base.dao.BaseDao;
import com.striveh.callcenter.pojo.callcenter.CallTaskPojo;

import java.util.List;

/**
 * @功能:【callTask 呼叫任务表】Dao
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-04-06 12:13:48
 * @说明：<pre></pre>
 */
@Repository
public class CallTaskDao extends BaseDao<CallTaskPojo> {
    public List<CallTaskPojo> selectProcList(CallTaskPojo callTaskPojo){
        return selectList("selectProcList",callTaskPojo);
    }

    public CallTaskPojo selectProc(CallTaskPojo callTaskPojo) {
        return selectUnique("selectProc",callTaskPojo);
    }
}
