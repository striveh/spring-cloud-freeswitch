/** */
package com.striveh.callcenter.calllist.calllist.service;

import com.striveh.callcenter.calllist.calllist.dao.CallListDao;
import com.striveh.callcenter.calllist.calllist.service.iservice.ICallListService;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.pojo.calllist.CallListPojo;

/**
 * @功能:【callList 】Service
 * @项目名:callcentercallList
 * @作者:xxx
 * @日期:2020-04-06 12:05:43
 * @说明：<pre></pre>
 */
@Service
public class CallListService extends BaseService<CallListPojo, CallListDao> implements ICallListService {

}
