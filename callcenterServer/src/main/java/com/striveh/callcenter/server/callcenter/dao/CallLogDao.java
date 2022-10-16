/** */
package com.striveh.callcenter.server.callcenter.dao;

import org.springframework.stereotype.Repository;
import com.striveh.callcenter.common.base.dao.BaseDao;
import com.striveh.callcenter.pojo.callcenter.CallLogPojo;

import java.util.List;

/**
 * @功能:【callLog 呼叫日志表】Dao
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-04-06 12:13:47
 * @说明：<pre></pre>
 */
@Repository
public class CallLogDao extends BaseDao<CallLogPojo> {

    public List<CallLogPojo> selectListByCalled(CallLogPojo callLogPojo){
        return selectList("selectListByCalled",callLogPojo);
    }
    public List<CallLogPojo> selectListByCalling(CallLogPojo callLogPojo){
        return selectList("selectListByCalling",callLogPojo);
    }
}
