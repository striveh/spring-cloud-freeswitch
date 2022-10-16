package com.striveh.callcenter.freeswitch.base;

import com.striveh.callcenter.common.base.controller.AbsBaseController;
import com.striveh.callcenter.common.base.pojo.BasePojo;
import com.striveh.callcenter.common.database.redis.BaseRedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BaseController<T extends BasePojo> extends AbsBaseController<T> {
    /** redis操作 */
    @Qualifier("baseRedisDaoDef")
    @Autowired
    protected BaseRedisDao baseRedisDao;

}
