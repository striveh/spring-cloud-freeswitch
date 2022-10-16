/** */
package com.striveh.callcenter.server.callcenter.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.common.base.pojo.OptResult;
import com.striveh.callcenter.common.base.pojo.ResponseData;
import com.striveh.callcenter.common.constant.param.ERedisCacheKey;
import com.striveh.callcenter.common.constant.response.EResCodeCommon;
import com.striveh.callcenter.common.constant.response.EResCodeServer;

import com.striveh.callcenter.common.database.redis.BaseRedisDao;
import com.striveh.callcenter.common.util.JsonTool;
import com.striveh.callcenter.common.util.StringTool;
import com.striveh.callcenter.pojo.callcenter.CallTaskPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.callcenter.CallLogPojo;
import com.striveh.callcenter.server.base.BaseController;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallLogService;

import java.util.*;

/**
 * @功能:【callLog 呼叫日志表】controller
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-04-06 12:13:47
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/callLog")
public class CallLogController extends BaseController<CallLogPojo> {
	/** callLog 呼叫日志表service*/
    @Autowired
    private ICallLogService callLogService;
	@Autowired
	@Qualifier("baseRedisDaoDBCache")
	private BaseRedisDao baseRedisDaoDBCache;
	@Autowired
	private ICallTaskService callTaskService;
  	/**
	 * get
	 * @param callLog
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(CallLogPojo callLog, HttpServletRequest request, HttpServletResponse response) {

	}

	/**
	 * 获取项目呼叫结果
	 * 1 pageNum Integer √ 第几页
	 * 2 pageSize Integer √ 每页条数
	 * 3 projectCode String X 项目代码
	 * 4 callTask Code String X 任务代码
	 */
	@RequestMapping("/api/getList/v1")
	public ResponseData getList(CallLogPojo callLog) {
		OptResult info = null;
		Map<String,Object> data=new HashMap<>();
		try {
			if (callLog.getQueryTimeType()==null){
				callLog.setQueryTimeType(2);
			}
			if (!StringTool.isEmpty(callLog.getResultDetail())&&callLog.getResultDetail().contains(",")){
				callLog.setResultDetails(callLog.getResultDetail().split(","));
				callLog.setResultDetail(null);
			}
			if (!StringTool.isEmpty(callLog.getResult())&&callLog.getResult().contains(",")){
				callLog.setResults(callLog.getResult().split(","));
				callLog.setResult(null);
			}
			data.put("items",this.callLogService.selectListByCalled(callLog));
			data.put("total",callLog.getTotalRowCount());
			info = EResCodeCommon.svceRigGetDataSuccess.getOptResult(logger);
		} catch (Exception ex) {
			info = EResCodeServer.exptGetCallResult.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info, data);
	}
	/**
	 * 获取重呼呼叫列表
	 */
	@RequestMapping("/api/getListByResult/v1")
	public ResponseData getCallListForRepeat(CallLogPojo callLog) {
		logger.info("获取重呼呼叫列表{}",callLog.toString());
		OptResult info = null;
		Map<String,Object> data=new HashMap<>();
		try {

			if (baseRedisDaoDBCache.exists(ERedisCacheKey.CALLCENTER_TASK_CALLLIST_REPEAT.getCode()+callLog.getCallTaskCode())){
				extracted(callLog, data);
			}else {
				CallTaskPojo callTaskPojo = new CallTaskPojo();
				callTaskPojo.setCallTaskCode(callLog.getCallTaskCode());
				callTaskPojo = this.callTaskService.selectUnique(callTaskPojo);
				if (callTaskPojo.getCallType().equals(2)){
					Long startTime = System.currentTimeMillis();

					// 获取去重的重呼呼叫数据
					CallLogPojo params = new CallLogPojo();
					params.setCallTaskCode(callLog.getCallTaskCode());
					params.setPageSize(500);
					params.setPageNum(0);
					params.setResultDetails(callLog.getResultDetail().split(","));
					Set<String> callListSet = new HashSet<>();
					List<CallLogPojo> callLogPojos = null;
					do {
						params.setPageNum(params.getPageNum()+1);
						callLogPojos = this.callLogService.selectList(params);
						callLogPojos.forEach(e->{
							callListSet.add(e.getDestinationNumber());
						});
					} while (callLogPojos.size()==500);

					// 获取重呼原任务的成功数据
					Set<String> telNoSuccessSet = new HashSet<>();
					if (callLog.getResultDetail().contains("50")&&callLog.getResultDetail().contains("55")){

					}else {
						CallLogPojo callLogSuccess = new CallLogPojo();
						callLogSuccess.setCallTaskCode(callLog.getCallTaskCode());
						callLogSuccess.setPageSize(500);
						callLogSuccess.setPageNum(0);
						String resultDetails = null;
						if (!callLog.getResultDetail().contains("50")){
							resultDetails = "50";
						}else if (!callLog.getResultDetail().contains("55")){
							resultDetails=resultDetails==null?"55":resultDetails+",55";
						}
						callLogSuccess.setResultDetails(resultDetails.split(","));
						List<CallLogPojo> callLogSuccessList = null;
						do {
							callLogSuccess.setPageNum(callLogSuccess.getPageNum()+1);
							callLogSuccessList = this.callLogService.selectList(callLogSuccess);
							callLogSuccessList.forEach(e->telNoSuccessSet.add(e.getDestinationNumber()));
						} while (callLogSuccessList.size()==500);
					}

					callListSet.forEach(e->{
						if (!telNoSuccessSet.contains(e)){
							CallLogPojo callLogPojo = new CallLogPojo();
							callLogPojo.setDestinationNumber(e);
							baseRedisDaoDBCache.leftPush(ERedisCacheKey.CALLCENTER_TASK_CALLLIST_REPEAT.getCode()+callLog.getCallTaskCode(),callLogPojo);
						}
					});
					Long endTime = System.currentTimeMillis();
					logger.info("任务{}重呼查询耗时{}毫秒",callLog.getCallTaskCode(),endTime-startTime);

					extracted(callLog, data);
				}else {
					if (!StringTool.isEmpty(callLog.getResultDetail())&&callLog.getResultDetail().contains(",")){
						callLog.setResultDetails(callLog.getResultDetail().split(","));
						callLog.setResultDetail(null);
					}
					if (!StringTool.isEmpty(callLog.getResult())&&callLog.getResult().contains(",")){
						callLog.setResults(callLog.getResult().split(","));
						callLog.setResult(null);
					}
					data.put("items",this.callLogService.selectList(callLog));
					data.put("total",callLog.getTotalRowCount());
				}
			}
			info = EResCodeCommon.svceRigGetDataSuccess.getOptResult(logger);
		} catch (Exception ex) {
			info = EResCodeServer.exptGetCallResult.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info, data);
	}

	private void extracted(CallLogPojo callLog, Map<String, Object> data) {
		List<String> items = baseRedisDaoDBCache.lrange(ERedisCacheKey.CALLCENTER_TASK_CALLLIST_REPEAT.getCode()+ callLog.getCallTaskCode(),
				(callLog.getPageNum()-1)* callLog.getPageSize(), callLog.getPageNum()* callLog.getPageSize()-1);
		List<CallLogPojo> callLogPojoList = new ArrayList<>();
		items.forEach(e->callLogPojoList.add(JsonTool.getObj(e,CallLogPojo.class)));
		data.put("items",callLogPojoList);
		data.put("total",baseRedisDaoDBCache.llen(ERedisCacheKey.CALLCENTER_TASK_CALLLIST_REPEAT.getCode()+ callLog.getCallTaskCode()));

		if (items.size()< callLog.getPageSize()){
			baseRedisDaoDBCache.delete(ERedisCacheKey.CALLCENTER_TASK_CALLLIST_REPEAT.getCode()+ callLog.getCallTaskCode());
		}
	}
}
