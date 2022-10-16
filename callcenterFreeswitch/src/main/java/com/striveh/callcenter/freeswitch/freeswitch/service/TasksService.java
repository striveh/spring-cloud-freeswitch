/** */
package com.striveh.callcenter.freeswitch.freeswitch.service;

import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ITasksService;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.freeswitch.freeswitch.dao.TasksDao;
import com.striveh.callcenter.pojo.freeswitch.TasksPojo;

/**
 * @功能:【tasks 】Service
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:31
 * @说明：<pre></pre>
 */
@Service
public class TasksService extends BaseService<TasksPojo, TasksDao> implements ITasksService {

}
