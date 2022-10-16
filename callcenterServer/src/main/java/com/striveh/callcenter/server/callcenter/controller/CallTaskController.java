/** */
package com.striveh.callcenter.server.callcenter.controller;


import com.striveh.callcenter.common.base.pojo.OptResult;
import com.striveh.callcenter.common.base.pojo.ResponseData;
import com.striveh.callcenter.common.constant.param.ERedisCacheKey;
import com.striveh.callcenter.common.constant.response.EResCodeCommon;
import com.striveh.callcenter.common.constant.response.EResCodeServer;
import com.striveh.callcenter.common.database.redis.BaseRedisDao;
import com.striveh.callcenter.common.util.DateTool;
import com.striveh.callcenter.common.util.JsonTool;
import com.striveh.callcenter.common.util.StringTool;
import com.striveh.callcenter.feignclient.freeswitch.IGatewayServiceApi;
import com.striveh.callcenter.feignclient.freeswitch.IUserInfoServiceApi;
import com.striveh.callcenter.pojo.callcenter.CallLogPojo;
import com.striveh.callcenter.pojo.callcenter.CallProjectPojo;
import com.striveh.callcenter.pojo.freeswitch.GatewayPojo;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallLogService;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallProjectService;
import com.striveh.callcenter.server.callcenter.service.iservice.IFreeswitchService;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.transport.message.EslMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import com.striveh.callcenter.pojo.callcenter.CallTaskPojo;
import com.striveh.callcenter.server.base.BaseController;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallTaskService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @功能:【callTask 呼叫任务表】controller
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-04-06 12:13:48
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/callTask")
public class CallTaskController extends BaseController<CallTaskPojo> {
	/** callTask 呼叫任务表service*/
	@Autowired
	private ICallProjectService callProjectService;
    @Autowired
    private ICallTaskService callTaskService;
    @Autowired
	private ICallLogService callLogService;
	@Autowired
	@Qualifier("baseRedisDaoDef")
	private BaseRedisDao baseRedisDao;
	@Autowired
	InboundClient inboundClient;
	@Autowired
	private IGatewayServiceApi gatewayServiceApi;

	@Value("${common.fsGateway.filepath}")
	private String gatewayConfigFilePath;

	@Value("${common.recordings.path}")
	private String recordingsPath;
	@Autowired
	private IUserInfoServiceApi userInfoServiceApi;
	@Autowired
	private SimpMessageSendingOperations msgOperations;
	@Value("${common.playback.path}")
	private String playbackPath;

	@Autowired
	private IFreeswitchService freeswitchService;
  	/**
	 * 提交呼叫任务
	 *
	 */
	@RequestMapping("/api/submitCallTask/v1")
	public ResponseData submitCallTask(CallTaskPojo callTask) {
		logger.info("任务{}添加",callTask.toString());
		OptResult optResult=null;
		try {
			if (StringUtils.isEmpty(callTask.getProjectCode())) {
				optResult = EResCodeServer.svceErrNoProjectCode.getOptResult(logger);
			} else if (StringUtils.isEmpty(callTask.getCallTaskCode())) {
				optResult = EResCodeServer.svceErrNoCallTaskCode.getOptResult(logger);
			} else if (StringUtils.isEmpty(callTask.getCallGWs())) {
				optResult = EResCodeServer.svceErrNoCallGWs.getOptResult(logger);
			} else if (callTask.getCallListId()==null) {
				optResult = EResCodeServer.svceErrNoCallListId.getOptResult(logger);
			} else if (callTask.getRate()==null) {
				optResult = EResCodeServer.svceErrNoRate.getOptResult(logger);
			} else {
				CallTaskPojo localTask=this.callTaskService.selectUnique(callTask);
				if (localTask!=null){
					callTask.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					callTask.setStatus(1);
					this.callTaskService.update(callTask);
					this.callTaskService.getCallList(callTask);
					optResult = EResCodeCommon.svceRigSubmitSuccess.getOptResult(logger);
				}else {
					callTask.setAddTime(new Timestamp(System.currentTimeMillis()));
					callTask.setStatus(1);
					this.callTaskService.insert(callTask);
					this.callTaskService.getCallList(callTask);
					optResult = EResCodeCommon.svceRigSubmitSuccess.getOptResult(logger);
				}
			}
		} catch (Exception ex) {
			optResult = EResCodeServer.exptSubmitCallTask.getOptResult(logger);
			logger.error(optResult.getMsg(), ex);
		}

		return new ResponseData<Object>(optResult);
	}

	/**
	 * 呼叫任务启动
	 * 1 callTaskCode String √ 任务代码
	 */
	@RequestMapping("/api/callTaskStart/v1")
	public ResponseData callTaskStart(CallTaskPojo callTask) {
		OptResult optResult=null;
		try {
			if (StringUtils.isEmpty(callTask.getCallTaskCode())) {
				optResult = EResCodeServer.svceErrNoCallTaskCode.getOptResult(logger);
			} else {
				callTask=this.callTaskService.selectUnique(callTask);
				if (callTask==null){
					optResult = EResCodeServer.svceErrTaskCode.getOptResult(logger);
				}else{
					if (callTask.getExpiredTime().getTime()<System.currentTimeMillis()){
						optResult = EResCodeServer.svceErrTaskExpired.getOptResult(logger);
					}else if (callTask.getStatus()>3){
						optResult = EResCodeServer.svceErrTaskEnd.getOptResult(logger);
					}else {
						//拨打类型，1预测式拨打2预览式拨打3客户拨入
						if (!callTask.getCallType().equals(3)){
							List<Map> callGWs= JsonTool.getList(callTask.getCallGWs(),Map.class);
							List<GatewayPojo> gatewayPojos = new ArrayList<>();
							for (Map callGW : callGWs) {
								GatewayPojo gatewayPojo= gatewayServiceApi.getByCode((String)callGW.get("code"));
								ResponseData optResult1 = checkGwStatus(gatewayPojo,callTask);
								if (optResult1 != null) return optResult1;

								gatewayPojo.setConcurrency(Integer.valueOf((String) callGW.get("concurrency")));
								gatewayPojos.add(gatewayPojo);
							}
							baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TASK_GWS.getCode()+callTask.getCallTaskCode(),gatewayPojos);
							baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TASK_RATE.getCode()+callTask.getCallTaskCode(),callTask.getRate());

						}
						//修改任务状态为启动状态
						callTask.setUpdateTime(new Timestamp(System.currentTimeMillis()));
						callTask.setStatus(2);
						this.callTaskService.update(callTask);
						this.callTaskService.start(callTask);
						msgOperations.convertAndSend("/topic/task/" + callTask.getProjectCode(), callTask);
						optResult = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
					}
				}
			}
		} catch (Exception ex) {
			optResult = EResCodeServer.exptCallTaskStart.getOptResult(logger);
			logger.error(optResult.getMsg(), ex);
		}

		return new ResponseData<Object>(optResult);
	}

	private ResponseData checkGwStatus(GatewayPojo gatewayPojo,CallTaskPojo callTaskPojo) {
		
		CallProjectPojo callProjectPojo = new CallProjectPojo();
		callProjectPojo.setProjectCode(callTaskPojo.getProjectCode());
		callProjectPojo = this.callProjectService.selectUnique(callProjectPojo);
		
		OptResult optResult;
		if (gatewayPojo.getRegisterType()==1){
			EslMessage eslMessage=inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()), "sofia profile external gwlist ","up");
			logger.info("网关状态查询 {},结果{}",gatewayPojo.getGwName(), EslHelper.formatEslMessage(eslMessage));
			String gwurl=eslMessage.getBodyLines().get(0);
			if (!gwurl.contains(gatewayPojo.getGwCode())){
				optResult = EResCodeServer.exptCallTaskStart.getOptResult(logger);
				optResult.setMsg(gatewayPojo.getGwName()+"未注册，请先解决或者更换线路");
				return new ResponseData<Object>(optResult);
			}
		}else {
			EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
					"sofia_contact",gatewayPojo.getUsername());
			logger.info("网关状态查询 {},结果{}",gatewayPojo.getGwName(),EslHelper.formatEslMessage(eslMessage));
			String gwurl=eslMessage.getBodyLines().get(0);
			if (gwurl.contains("user_not_registered")){
				optResult = EResCodeServer.exptCallTaskStart.getOptResult(logger);
				optResult.setMsg(gatewayPojo.getGwName()+"未注册，请先解决或者更换线路");
				return new ResponseData<Object>(optResult);
			}
		}
		return null;
	}

	/**
	 * 设置任务并发数
	 * 1 callTaskCode String √ 任务代码
	 * 2 callGWs JSONObject √ 线路列表[{"code":"1001","concurrent":100},{"code":"1002","concurrent":200}]
	 * 3 rate Integer √ 拨打倍率
	 */
	@RequestMapping("/api/setTaskConcurrent/v1")
	public ResponseData setTaskConcurrent(String callTaskCode,String callGWs,Double rate) {
		OptResult optResult=null;
		try {
			if (StringUtils.isEmpty(callTaskCode)) {
				optResult = EResCodeServer.svceErrNoCallTaskCode.getOptResult(logger);
			} else if (StringUtils.isEmpty(callGWs)) {
				optResult = EResCodeServer.svceErrNoCallGWs.getOptResult(logger);
			} else if (rate==null) {
				optResult = EResCodeServer.svceErrNoRate.getOptResult(logger);
			} else {
				CallTaskPojo callTask=new CallTaskPojo();
				callTask.setCallTaskCode(callTaskCode);
				callTask=this.callTaskService.selectUnique(callTask);
				if (callTask==null){
					optResult = EResCodeServer.svceErrTaskCode.getOptResult(logger);
				}else if (callTask.getStatus()>3){
					optResult = EResCodeServer.svceErrTaskEnd.getOptResult(logger);
				}else{
					logger.info("任务{}修改线路及并发数{},{}",callTask.getCallTaskCode(),callGWs,rate);
					callTask.setCallGWs(callGWs);
					callTask.setRate(rate);
					this.callTaskService.update(callTask);
					if (callTask.getStatus().equals(2)||callTask.getStatus().equals(3)){
						List<Map> callGWList= JsonTool.getList(callTask.getCallGWs(),Map.class);
						List<GatewayPojo> gatewayPojos = new ArrayList<>();
						for (Map callGW : callGWList) {
							GatewayPojo gatewayPojo= gatewayServiceApi.getByCode((String)callGW.get("code"));
							ResponseData optResult1 = checkGwStatus(gatewayPojo,callTask);
							if (optResult1 != null) return optResult1;

							gatewayPojo.setConcurrency(Integer.valueOf((String) callGW.get("concurrency")));
							gatewayPojos.add(gatewayPojo);
						}
						baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TASK_GWS.getCode()+callTask.getCallTaskCode(),gatewayPojos);
						baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TASK_RATE.getCode()+callTask.getCallTaskCode(),callTask.getRate());
					}
					optResult = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
				}
			}
		} catch (Exception ex) {
			optResult = EResCodeServer.exptSetTaskConcurrent.getOptResult(logger);
			logger.error(optResult.getMsg(), ex);
		}

		return new ResponseData<Object>(optResult);
	}

	/**
	 * 给坐席分配号码（预览式拨打）
	 * 1 callTaskCode String √ 任务代码
	 * 2 count Integer √ 分配数量
	 */
	@RequestMapping("/api/getCallList/v1")
	public ResponseData getCallList(String callTaskCode,Integer count) {
		OptResult optResult=null;
		Map<String,Object> resultData = new HashMap<>();
		try {
			if (StringUtils.isEmpty(callTaskCode)) {
				optResult = EResCodeServer.svceErrNoCallTaskCode.getOptResult(logger);
			} else {
				CallTaskPojo callTask=new CallTaskPojo();
				callTask.setCallTaskCode(callTaskCode);
				callTask=this.callTaskService.selectUnique(callTask);
				if (callTask==null){
					optResult = EResCodeServer.svceErrTaskCode.getOptResult(logger);
				}else if (callTask.getStatus()>3){
					optResult = EResCodeServer.svceErrTaskEnd.getOptResult(logger);
				}else{
					resultData.put("items",this.callTaskService.getCallList(callTask,count));
					optResult = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
				}
			}
		} catch (Exception ex) {
			optResult = EResCodeServer.exptGetCallList.getOptResult(logger);
			logger.error(optResult.getMsg(), ex);
		}

		return new ResponseData<Object>(optResult,resultData);
	}

	/**
	 * 呼叫任务暂停
	 * 1 callTaskCode String √ 任务代码
	 */
	@RequestMapping("/api/callTaskPause/v1")
	public ResponseData callTaskPause(CallTaskPojo callTask) {
		OptResult optResult=null;
		try {
			if (StringUtils.isEmpty(callTask.getCallTaskCode())) {
				optResult = EResCodeServer.svceErrNoCallTaskCode.getOptResult(logger);
			} else {
				callTask=this.callTaskService.selectUnique(callTask);
				if (callTask==null){
					optResult = EResCodeServer.svceErrTaskCode.getOptResult(logger);
				}else if (callTask.getStatus()>3){
					optResult = EResCodeServer.svceErrTaskEnd.getOptResult(logger);
				}else{
					//修改任务状态为暂停状态
					callTask.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					callTask.setStatus(3);
					this.callTaskService.update(callTask);
					this.callTaskService.pause(callTask);
					optResult = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
				}
			}
		} catch (Exception ex) {
			optResult = EResCodeServer.exptCallTaskPause.getOptResult(logger);
			logger.error(optResult.getMsg(), ex);
		}

		return new ResponseData<Object>(optResult);
	}

	/**
	 * 呼叫任务暂停所有
	 */
	@RequestMapping("/api/callTaskPauseAll/v1")
	public ResponseData callTaskPauseAll(CallTaskPojo callTask) {
		OptResult optResult=null;
		try {
			callTask = new CallTaskPojo();
			callTask.setStatus(2);
			List<CallTaskPojo> callTaskPojos= this.callTaskService.selectList(callTask);
			callTaskPojos.forEach(e->{
				e.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				e.setStatus(3);
				this.callTaskService.update(e);
				this.callTaskService.pause(e);
			});
			optResult = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
		} catch (Exception ex) {
			optResult = EResCodeServer.exptCallTaskPause.getOptResult(logger);
			logger.error(optResult.getMsg(), ex);
		}

		return new ResponseData<Object>(optResult);
	}

	/**
	 * 呼叫任务结束
	 * 1 callTaskCode String √ 任务代码
	 */
	@RequestMapping("/api/callTaskEnd/v1")
	public ResponseData callTaskEnd(CallTaskPojo callTask) {
		OptResult optResult=null;
		try {
			if (StringUtils.isEmpty(callTask.getCallTaskCode())) {
				optResult = EResCodeServer.svceErrNoCallTaskCode.getOptResult(logger);
			} else {
				callTask=this.callTaskService.selectUnique(callTask);
				if (callTask==null){
					optResult = EResCodeServer.svceErrTaskCode.getOptResult(logger);
				}else if (callTask.getStatus()>3){
					optResult = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
				}else{
					baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TASK_STATUS.getCode()+callTask.getCallTaskCode(),3);
					logger.info("任务{}手动结束",callTask.getCallTaskCode());
					if (callTask.getStatus()>1){
						this.callTaskService.end(callTask);
					} else {
						callTaskService.updateCacheAfterPorjectEnd(callTask);
					}
					optResult = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
				}
			}
		} catch (Exception ex) {
			optResult = EResCodeServer.exptCallTaskEnd.getOptResult(logger);
			logger.error(optResult.getMsg(), ex);
		}

		return new ResponseData<Object>(optResult);
	}

	/**
	 * 呼叫任务追加
	 * 1 callTaskCode String √ 任务代码
	 */
	@RequestMapping("/api/callTaskAdditional/v1")
	public ResponseData callTaskAdditional(CallTaskPojo callTask) {
		logger.info("任务{}追加{}",callTask.getCallTaskCode(),callTask.getCallListLastId());
		OptResult optResult=null;
		try {
			if (StringUtils.isEmpty(callTask.getCallTaskCode())) {
				optResult = EResCodeServer.svceErrNoCallTaskCode.getOptResult(logger);
			} else {
				CallTaskPojo callTaskPojo=this.callTaskService.selectUnique(callTask);
				if (callTaskPojo==null){
					optResult = EResCodeServer.svceErrTaskCode.getOptResult(logger);
				}else if (callTaskPojo.getStatus()>3){
					optResult = EResCodeServer.svceErrTaskEnd.getOptResult(logger);
				}else{
					callTaskPojo.setUpdateTime(DateTool.getTimestamp());
					this.callTaskService.update(callTaskPojo);
					this.callTaskService.additionalCallList(callTask);
					optResult = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
				}
			}
		} catch (Exception ex) {
			optResult = EResCodeServer.exptCallTaskAdditional.getOptResult(logger);
			logger.error(optResult.getMsg(), ex);
		}

		return new ResponseData<Object>(optResult);
	}

	/**
	 * 获取任务进度
	 * 1 callTaskCode String √ 任务代码
	 */
	@RequestMapping("/api/callTaskStatus/v1")
	public ResponseData callTaskStatus(CallTaskPojo callTask) {
		OptResult optResult=null;
		try {
			if (StringUtils.isEmpty(callTask.getCallTaskCode())) {
				optResult = EResCodeServer.svceErrNoCallTaskCode.getOptResult(logger);
			} else {
				callTask=this.callTaskService.selectUnique(callTask);
				if (callTask==null){
					optResult = EResCodeServer.svceErrTaskCode.getOptResult(logger);
				}else{
					CallLogPojo callLog = new CallLogPojo();
					callLog.setCallTaskCode(callTask.getCallTaskCode());
					callLog.setResult("10");
					callTask.setSuccessCount(this.callLogService.selectList(callLog).size());
					callLog.setResult("20");
					callTask.setFailCount(this.callLogService.selectList(callLog).size());
					callLog.setResult("30");
					callTask.setMissCount(this.callLogService.selectList(callLog).size());
					optResult = EResCodeCommon.svceRigGetDataSuccess.getOptResult(logger);
				}
			}
		} catch (Exception ex) {
			optResult = EResCodeServer.exptCallTaskStatus.getOptResult(logger);
			logger.error(optResult.getMsg(), ex);
		}

		return new ResponseData<Object>(optResult,callTask);
	}

	/**
	 * 坐席拨打电话
	 */
	@MessageMapping("/call")
	public void call(String jsonStr) {
		logger.info("坐席拨打电话: {}", jsonStr);
		Map<String,String> map=JsonTool.getObj(jsonStr,Map.class);
		String username=map.get("username");
		String telNo = map.get("telNo").trim();

		this.callTaskService.agentCall(map, username, telNo);
	}

	/**
	 * 项目经理给坐席打电话
	 */
	@MessageMapping("/pmCallAgent")
	public void pmCallAgent(String jsonStr) {
		logger.info("项目经理给坐席打电话: {}", jsonStr);
		Map<String,String> map=JsonTool.getObj(jsonStr,Map.class);
		String username=map.get("username");
		String telNo = map.get("telNo").trim();
		UserinfoPojo userinfoPojo = this.userInfoServiceApi.getByUsername(username);
		String projectCode = map.get("projectCode");
		String serverAddr = null;
		if (projectCode == null){
			serverAddr = inboundClient.option().serverOptions().get(0).addr();
		}else {
			CallProjectPojo callProjectPojo = new CallProjectPojo();
			callProjectPojo.setProjectCode(projectCode);
			callProjectPojo = this.callProjectService.selectUnique(callProjectPojo);
			serverAddr = freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId());
		}
		try {
			GatewayPojo gatewayPojo = this.gatewayServiceApi.getReturnVisitGw();

			String callUrl=null;
			String callTelNo=telNo;
			String phonePrefix="";
			if (StringTool.isPhoneNumber(telNo)&&StringUtils.isNotEmpty(gatewayPojo.getMobilePhonePrefix())){
				callTelNo=gatewayPojo.getMobilePhonePrefix()+telNo;
				phonePrefix=gatewayPojo.getMobilePhonePrefix();
			}else if (!StringTool.isPhoneNumber(telNo)&&StringUtils.isNotEmpty(gatewayPojo.getFixedPhonePrefix())){
				callTelNo=gatewayPojo.getFixedPhonePrefix()+telNo;
				phonePrefix=gatewayPojo.getFixedPhonePrefix();
			}
			if (gatewayPojo.getRegisterType()==1){
				callUrl="sofia/gateway/"+gatewayPojo.getGwCode()+"/"+callTelNo;
			}else {
				EslMessage eslMessage = inboundClient.sendSyncApiCommand(serverAddr,
						"sofia_contact",gatewayPojo.getUsername());
				logger.info("网关用户 sofia_contact {},结果{}",gatewayPojo.getUsername(),EslHelper.formatEslMessage(eslMessage));
				String gwurl=eslMessage.getBodyLines().get(0);
				callUrl="sofia/internal/"+callTelNo+gwurl.substring(gwurl.lastIndexOf("@"));
			}
			EslMessage eslMessage = inboundClient.sendSyncApiCommand(serverAddr,
					"create_uuid",null);
			logger.info("项目经理给坐席打电话{}create_uuid结果{}",username,EslHelper.formatEslMessage(eslMessage));

			if (gatewayPojo.getCodec()!=null&&gatewayPojo.getCodec().equals("G711")){
				gatewayPojo.setCodec(null);//使用默认编码
			}
			String callStr="{return_visit=true"+",phonePrefix="+phonePrefix+"}user/"+username+
					" &bridge({sip_sticky_contact=true,return_visit=true,origination_uuid="+eslMessage.getBodyLines().get(0)+
					(StringUtils.isNotEmpty(gatewayPojo.getOriginationCallId())? ",origination_caller_id_number="+gatewayPojo.getOriginationCallId():"")+
					(StringUtils.isNotEmpty(gatewayPojo.getCodec())? ",absolute_codec_string="+gatewayPojo.getCodec():"")+
					",execute_on_answer='record_session "+recordingsPath+ DateTool.formatDate("yyyyMMddHHmmss") +telNo+".mp3'"+",phonePrefix="+phonePrefix+
					"}"+callUrl+")";

			Map<String,String> body=new HashMap<>();
			body.put("telNo",telNo);
			body.put("callId", eslMessage.getBodyLines().get(0));
			//记录calllog
			CallLogPojo newCallLogPojo=new CallLogPojo();
			newCallLogPojo.setAddTime(new Timestamp(System.currentTimeMillis()));

			newCallLogPojo.setDestinationNumber(telNo);
			newCallLogPojo.setGwCode(gatewayPojo.getGwCode());
			if (gatewayPojo.getBillType()!=null&&gatewayPojo.getBillType()==2){
				newCallLogPojo.setPriceRule(gatewayPojo.getBillsec()+"|"+gatewayPojo.getPrice());
			}
			newCallLogPojo.setHangupCase("return_visit");
			newCallLogPojo.setCallerIdNumber(username);
			newCallLogPojo.setCallUUID(eslMessage.getBodyLines().get(0));
			newCallLogPojo.setStartTimestamp(new Timestamp(System.currentTimeMillis()));
			this.callLogService.insert(newCallLogPojo);

			logger.info("项目经理给坐席打电话{}执行了{},{}",username,callStr,inboundClient.sendAsyncApiCommand(serverAddr,"originate",callStr));

			msgOperations.convertAndSend("/topic/call/" + username,  JsonTool.getJsonString(body));
		}catch (Exception e){
			logger.error("项目经理给坐席打电话异常", e);
		}
	}

	/**
	 * 项目经理给坐席打电话挂断
	 */
	@MessageMapping("/pmByeAgent")
	public void pmByeAgent(String jsonStr) {
		logger.info("项目经理给坐席打电话挂断 {}", jsonStr);
		Map<String,String> map=JsonTool.getObj(jsonStr,Map.class);
		UserinfoPojo userinfoPojo = this.userInfoServiceApi.getByUsername(map.get("username"));
		String projectCode = map.get("projectCode");
		if (projectCode == null){
			EslMessage eslMessage = inboundClient.sendSyncApiCommand(inboundClient.option().serverOptions().get(0).addr(),
					"uuid_kill",map.get("callId"));
			logger.info("项目经理给坐席打电话挂断{}：{},结果{}",map.get("username"),map.get("callId"), EslHelper.formatEslMessage(eslMessage));
		}else {
			CallProjectPojo callProjectPojo = new CallProjectPojo();
			callProjectPojo.setProjectCode(projectCode);
			callProjectPojo = this.callProjectService.selectUnique(callProjectPojo);
			EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
					"uuid_kill",map.get("callId"));
			logger.info("项目经理给坐席打电话挂断{}：{},结果{}",map.get("username"),map.get("callId"), EslHelper.formatEslMessage(eslMessage));
		}
	}


	/**
	 * 坐席点拨（预览式拨打）
	 */
	@MessageMapping("/previewCall")
	public void previewCall(String jsonStr) {
		logger.info("坐席点拨（预览式拨打）: {}", jsonStr);
		try {
			this.callTaskService.previewCall(jsonStr);
		}catch (Exception e){
			logger.error("坐席点拨（预览式拨打）异常", e);
		}
	}



	/**
	 * 坐席挂断
	 */
	@MessageMapping("/bye")
	public void bye(String jsonStr) {
		logger.info("坐席挂断 {}", jsonStr);
		Map<String,String> map=JsonTool.getObj(jsonStr,Map.class);
		UserinfoPojo userinfoPojo  = this.userInfoServiceApi.getByUsername(map.get("username"));
		//11660呼入项目拒接处理
		if (userinfoPojo.getProjectId().equals(7145L)){
			EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
					"uuid_kill",map.get("callId"));
			logger.info("坐席{}挂断 {},结果{}",map.get("username"),map.get("callId"), EslHelper.formatEslMessage(eslMessage));
			if (userinfoPojo.getSessionStatus().equals(0)){
				Map<String,String> body=new HashMap<>();
				body.put("telNo",userinfoPojo.getDestinationNumber());
				body.put("callId", map.get("callId"));
				msgOperations.convertAndSend("/topic/bye/" + userinfoPojo.getUsername(), JsonTool.getJsonString(body));
				msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);
			}
		}else {
			if (userinfoPojo.getSessionStatus().equals(10)){
				EslMessage eslMessagePark = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
						"uuid_park",map.get("callId"));
				logger.info("坐席{}挂断 {},uuid_park结果{}",map.get("username"),map.get("callId"), EslHelper.formatEslMessage(eslMessagePark));
				if (eslMessagePark.getBodyLines().get(0).contains("ERR")){
					userinfoPojo.setSessionStatus(0);
					this.userInfoServiceApi.update(userinfoPojo);
					EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
							"uuid_kill",userinfoPojo.getCalluuid());
					logger.info("坐席{}挂断的电话已经挂断,置忙{},结果{}",map.get("username"),userinfoPojo.getCalluuid(), EslHelper.formatEslMessage(eslMessage));
				}else {
					String eslMessage = inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
							"uuid_broadcast",map.get("callId")+" "+playbackPath+"evaluation.mp3 aleg");
					logger.info("坐席{}挂断 {},uuid_broadcast结果{}",map.get("username"),map.get("callId"), eslMessage);

					baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_AGENT_SESSION_EVALUATION_PLAYBACK.getCode()+ map.get("callId")+"_"+freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),1,30);
				}
			}else {
				EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
						"uuid_kill",map.get("callId"));
				logger.info("坐席{}挂断 {},结果{}",map.get("username"),map.get("callId"), EslHelper.formatEslMessage(eslMessage));
			}
		}
	}

	/**
	 * 坐席获取任务信息
	 */
	@MessageMapping("/task")
	public void task(String jsonStr) {
		Map<String,String> map=JsonTool.getObj(jsonStr,Map.class);
		CallTaskPojo callTaskPojo = new CallTaskPojo();
		callTaskPojo.setProjectCode(map.get("projectCode"));
		callTaskPojo=this.callTaskService.getProc(callTaskPojo);
		if (callTaskPojo!=null){
			msgOperations.convertAndSend("/topic/task/" + map.get("projectCode")+"/"+map.get("username"), callTaskPojo);
		}
	}


	/**
	 * 通知任务执行进度
	 */
	@Scheduled(fixedRate = 10000)
	void notifyCount(){

		List<CallTaskPojo> callTaskPojos= this.callTaskService.getProcList(new CallTaskPojo());
		callTaskPojos.forEach(e->{
			if (e.getCallType().equals(3)){
				return;
			}
			this.callTaskService.notifyCount(e,"0");
		});
	}


	/**
	 * 处理执行中的超时呼叫
	 */
	@Scheduled(fixedRate = 30000)
	void callTimeout(){

		List<CallTaskPojo> callTaskPojos= this.callTaskService.getProcList(new CallTaskPojo());
		callTaskPojos.forEach(e->{
			if (e.getStatus().equals(1)) return;
			this.callTaskService.callTimeout(e);
		});
	}


	@RequestMapping(value = "/inner/getListByStatus",method = RequestMethod.GET)
	public List<CallTaskPojo> getListByStatus (@RequestParam Integer status){
		CallTaskPojo callTaskPojo=new CallTaskPojo();
		callTaskPojo.setStatus(status);
		return this.callTaskService.getProcList(callTaskPojo);
	}

	@RequestMapping(value = "/inner/addGateway",method = RequestMethod.POST)
	public boolean addGateway(@RequestBody GatewayPojo gatewayPojo){

		File gatewayFile = new File(gatewayConfigFilePath+gatewayPojo.getGwCode()+".xml");
		configGateway(gatewayPojo, gatewayFile);

		inboundClient.option().serverOptions().forEach(e->{
			String eslMessage=inboundClient.sendAsyncApiCommand(e.addr(), "sofia profile external rescan reloadxml",null);
			logger.info("添加网关{}到{}结果{}",gatewayPojo.getGwName(),e.addr() , eslMessage);
		});
		return true;
	}

	@RequestMapping(value = "/inner/updateGateway",method = RequestMethod.POST)
	public boolean updateGateway(@RequestBody GatewayPojo gatewayPojo){
		File gatewayFile = new File(gatewayConfigFilePath+gatewayPojo.getGwCode()+".xml");
		configGateway(gatewayPojo, gatewayFile);

		this.callTaskService.reloadGateway(gatewayPojo);
		return true;
	}

	private void configGateway(GatewayPojo gatewayPojo, File gatewayFile) {
		FileWriter gatewayFileWriter = null;
		try {
			gatewayFileWriter = new FileWriter(gatewayFile,false);
			gatewayFileWriter.append("<include>"+"\n");
			gatewayFileWriter.append("    <gateway name=\""+gatewayPojo.getGwCode()+"\">"+"\n");
			if (StringUtils.isNotEmpty(gatewayPojo.getUsername())){
				gatewayFileWriter.append("        <param name=\"username\" value=\""+gatewayPojo.getUsername()+"\"/>"+"\n");
				gatewayFileWriter.append("        <param name=\"password\" value=\""+gatewayPojo.getPassword()+"\"/>"+"\n");
			}
			gatewayFileWriter.append("        <param name=\"realm\" value=\""+gatewayPojo.getRealm()+"\"/>"+"\n");
			gatewayFileWriter.append("        <param name=\"register\" value=\""+gatewayPojo.getRegister()+"\"/>"+"\n");
			gatewayFileWriter.append("    </gateway>"+"\n");
			gatewayFileWriter.append("</include>");
			gatewayFileWriter.flush();
		} catch (IOException e) {
			logger.info("配置线路异常",e);
		} finally {
			if (null!=gatewayFileWriter){
				try {
					gatewayFileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
