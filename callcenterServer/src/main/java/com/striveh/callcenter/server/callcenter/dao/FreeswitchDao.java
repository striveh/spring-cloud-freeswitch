/** */
package com.striveh.callcenter.server.callcenter.dao;

import org.springframework.stereotype.Repository;
import com.striveh.callcenter.common.base.dao.BaseDao;
import com.striveh.callcenter.pojo.callcenter.FreeswitchPojo;

/**
 * @功能:【freeswitch freeswitch服务实例表】Dao
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-05-17 16:54:05
 * @说明：<pre></pre>
 */
@Repository
public class FreeswitchDao extends BaseDao<FreeswitchPojo> {

    public FreeswitchPojo selectAvailableServer(FreeswitchPojo freeswitchPojo){
        return selectUnique("selectAvailableServer",freeswitchPojo);
    }
}
