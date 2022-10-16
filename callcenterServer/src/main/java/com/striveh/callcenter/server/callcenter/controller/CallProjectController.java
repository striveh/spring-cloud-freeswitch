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
import com.striveh.callcenter.feignclient.freeswitch.IUserInfoServiceApi;
import com.striveh.callcenter.pojo.callcenter.CallProjectPojo;
import com.striveh.callcenter.pojo.callcenter.CallTaskPojo;
import com.striveh.callcenter.pojo.callcenter.FreeswitchPojo;
import com.striveh.callcenter.pojo.callcenter.VoicePojo;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallTaskService;
import com.striveh.callcenter.server.callcenter.service.iservice.IFreeswitchService;
import com.striveh.callcenter.server.callcenter.service.iservice.IVoiceService;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.transport.message.EslMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.server.base.BaseController;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallProjectService;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @功能:【callProject 呼叫项目表】controller
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-04-06 12:13:48
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/callProject")
public class CallProjectController extends BaseController<CallProjectPojo> {
	/** callProject 呼叫项目表service*/
    @Autowired
    private ICallProjectService callProjectService;
    @Autowired
	private IUserInfoServiceApi userInfoServiceApi;
    @Autowired
	InboundClient inboundClient;
    @Autowired
	private ICallTaskService callTaskService;
    @Autowired
	private IFreeswitchService freeswitchService;

	@Autowired
	private SimpMessageSendingOperations msgOperations;
	@Autowired
	@Qualifier("baseRedisDaoDef")
	private BaseRedisDao baseRedisDao;

	@Autowired
	private IVoiceService voiceService;

	@Value("${common.voice.path}")
	private String voicePath;

	private AtomicInteger serverIndex = new AtomicInteger(0);

	/**
	 * 创建项目获得分机
	 * 1: projectCode String √ 项目代码
	 * 2: seats Integer X 坐席数
	 */
	@RequestMapping("/api/createProject/v1")
	public ResponseData createProject(String projectCode,Integer seats) {
		OptResult info = null;
		CallProjectPojo callProjectPojo=new CallProjectPojo();
		CallProjectPojo finalCallProjectPojo = null;
		try {
			if (StringUtils.isEmpty(projectCode)) {
				info = EResCodeServer.svceErrNoProjectCode.getOptResult(logger);
			} else if (seats==null){
				info = EResCodeServer.svceErrNoSeats.getOptResult(logger);
			} else {
				callProjectPojo.setProjectCode(projectCode);
				callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
				if (callProjectPojo==null){
					callProjectPojo=new CallProjectPojo();
					callProjectPojo.setProjectCode(projectCode);
					callProjectPojo.setAddTime(new Timestamp(System.currentTimeMillis()));
					callProjectPojo.setSeats(seats);
					callProjectPojo.setStatus(1);
					callProjectPojo.setCertificate(StringTool.MD5Encode(UUID.randomUUID().toString()));

					//计算该分配给哪个FreeSWITCH实例运行该项目
					//获取可用的FreeSWITCH实例列表
					FreeswitchPojo freeswitchPojo = new FreeswitchPojo();
					freeswitchPojo.setStatus(1);
					List<FreeswitchPojo> freeswitchPojos = this.freeswitchService.selectList(freeswitchPojo);
					finalCallProjectPojo = callProjectPojo;
					for (FreeswitchPojo e : freeswitchPojos) {
						//获取还在运行中的项目列表
						CallProjectPojo callProjectPojo1 = new CallProjectPojo();
						callProjectPojo1.setFreeswitchId(e.getId());
						callProjectPojo1.setStatus(1);
						List<CallProjectPojo> callProjectPojos = this.callProjectService.selectList(callProjectPojo1);
						int seatsCount = callProjectPojos.stream().mapToInt(CallProjectPojo::getSeats).sum();
						if (e.getCallConcurrent() >= (seats + seatsCount)) {
							finalCallProjectPojo.setFreeswitchId(e.getId());
							finalCallProjectPojo.setNativeServers(e.getNativeSipPhone());
							finalCallProjectPojo.setServers(e.getWebSipPhone());
							break;
						}
					}
					if (finalCallProjectPojo.getFreeswitchId()==null){
						info = EResCodeServer.exptCreateProjectNoServer.getOptResult(logger);
						return new ResponseData<Object>(info);
					}
					this.callProjectService.createProject(finalCallProjectPojo);
				}
				info = EResCodeCommon.svceRigSubmitSuccess.getOptResult(logger);
			}
		} catch (Exception ex) {
			info = EResCodeServer.exptCreateProject.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info, finalCallProjectPojo);
	}

	/**
	 * 坐席认领项目分机
	 * 1:certificate String √ 坐席领取分机凭证
	 * 2:extension String X 分配给坐席的分机号
	 * 3:type Integer √ 2坐席顾问3项目经理
	 */
	@RequestMapping("/api/getByCertificate/v1")
	public ResponseData getByCertificate(String certificate,String extension,Integer type) {
		OptResult info = null;
		UserinfoPojo userinfoPojo= null;
		try {
			if (type == null){
				type = 2;
			}
			CallProjectPojo callProjectPojo= new CallProjectPojo();

			if (StringUtils.isEmpty(extension)){
				if (StringUtils.isEmpty(certificate)){
					//创建分机
					userinfoPojo=new UserinfoPojo();
					userinfoPojo.setUsername(StringTool.getRandom(8));
					userinfoPojo.setPassword(StringTool.getRandom(7));
					userinfoPojo.setWorkStatus(0);
					userinfoPojo.setType(type);
					this.userInfoServiceApi.add(userinfoPojo);
					logger.info("分机{}认领成功,type:{}",userinfoPojo.getUsername(),type);
					info = EResCodeCommon.svceRigGetDataSuccess.getOptResult(logger);
				}else {
					callProjectPojo.setCertificate(certificate);
					callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
					if (callProjectPojo==null){
						info = EResCodeServer.svceErrCertificate.getOptResult(logger);
					}else {
						//创建分机
						userinfoPojo=new UserinfoPojo();
						userinfoPojo.setProjectId(callProjectPojo.getId());
						userinfoPojo.setFreeswitchId(callProjectPojo.getFreeswitchId());
						userinfoPojo.setUsername(StringTool.getRandom(8));
						userinfoPojo.setPassword(StringTool.getRandom(7));
						userinfoPojo.setWorkStatus(0);
						userinfoPojo.setType(type);
						this.userInfoServiceApi.add(userinfoPojo);
						logger.info("分机{}认领成功,type:{}",userinfoPojo.getUsername(),type);
						info = EResCodeCommon.svceRigGetDataSuccess.getOptResult(logger);
					}
				}
			}else{
				userinfoPojo=this.userInfoServiceApi.getByUsername(extension);
				info = EResCodeCommon.svceRigGetDataSuccess.getOptResult(logger);
				callProjectPojo.setId(userinfoPojo.getProjectId());
				callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
			}
//			if (userinfoPojo!=null){
//				userinfoPojo.setServers(sipServer);
//				userinfoPojo.setUri("sip:"+userinfoPojo.getUsername()+"@"+sipUri);
//				userinfoPojo.setNativeServers(sipUri+":5161");
//
//				if (userinfoPojo.getProjectId()!=null){
//					FreeswitchPojo freeswitchPojo = this.freeswitchService.getCacheDataByKey(new FreeswitchPojo(callProjectPojo.getFreeswitchId()));
//					userinfoPojo.setServers(freeswitchPojo.getWebSipPhone());
//					userinfoPojo.setNativeServers(freeswitchPojo.getNativeSipPhone());
//					userinfoPojo.setUri("sip:"+userinfoPojo.getUsername()+"@"+freeswitchPojo.getNativeSipPhone());
//				}
//			}
		} catch (Exception ex) {
			info = EResCodeServer.exptGetByCertificate.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info, userinfoPojo);
	}

	/**
	 * 给项目分配坐席
	 * 1:projectCode String √ 项目代码
	 * 2:username String √ 分配给坐席的分机号
	 */
	@RequestMapping("/api/distributionSeat/v1")
	public ResponseData distributionSeat(String projectCode,String username) {
		OptResult info = null;
		UserinfoPojo userinfoPojo= null;
		try {
			if (StringUtils.isEmpty(projectCode)) {
				info = EResCodeServer.svceErrNoProjectCode.getOptResult(logger);
			}else if (StringUtils.isEmpty(username)) {
				info = EResCodeServer.svceErrNoUsername.getOptResult(logger);
			} else {
				CallProjectPojo callProjectPojo= new CallProjectPojo();
				callProjectPojo.setProjectCode(projectCode);
				callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
				if (callProjectPojo==null){
					info = EResCodeServer.svceErrProjectCode.getOptResult(logger);
				}else {
					userinfoPojo=this.userInfoServiceApi.getByUsername(username);
					if (userinfoPojo.getProjectId()!=null){
						ResponseData responseData = setWorkStatus(username,0);
						if (!responseData.getReqResult().getCode().equals(1)){
							return responseData;
						}
					}
					userinfoPojo.setWorkStatus(null);
					userinfoPojo.setProjectId(callProjectPojo.getId());
					userinfoPojo.setFreeswitchId(callProjectPojo.getFreeswitchId());
					this.userInfoServiceApi.update(userinfoPojo);
					logger.info("项目{}分配{}坐席成功",projectCode,username);
					info = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);

					FreeswitchPojo freeswitchPojo = this.freeswitchService.getCacheDataByKey(new FreeswitchPojo(callProjectPojo.getFreeswitchId()));
					userinfoPojo.setServers(freeswitchPojo.getWebSipPhone());
					userinfoPojo.setNativeServers(freeswitchPojo.getNativeSipPhone());
					userinfoPojo.setUri("sip:"+userinfoPojo.getUsername()+"@"+freeswitchPojo.getNativeSipPhone());
				}
			}
		} catch (Exception ex) {
			info = EResCodeServer.exptDistributionSeat.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info, userinfoPojo);
	}


	/**
	 * 获取项目分机状态
	 * 1 projectCode String √ 项目代码
	 */
	@RequestMapping("/api/getExtentionByProject/v1")
	public ResponseData getExtentionByProject(String projectCode) {
		OptResult info = null;
		List<UserinfoPojo> userinfoList= null;
		try {
			if (StringUtils.isEmpty(projectCode)) {
				info = EResCodeServer.svceErrNoProjectCode.getOptResult(logger);
			} else {
				CallProjectPojo callProjectPojo= new CallProjectPojo();
				callProjectPojo.setProjectCode(projectCode);
				callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
				if (callProjectPojo==null){
					info = EResCodeServer.svceErrProjectCode.getOptResult(logger);
				}else {
					userinfoList = this.callTaskService.getUserinfoPojos(callProjectPojo);
					info = EResCodeCommon.svceRigGetDataSuccess.getOptResult(logger);
				}
			}
		} catch (Exception ex) {
			info = EResCodeServer.exptGetExtentionByProject.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info, userinfoList);
	}

	/**
	 * 坐席查询
	 */
	@RequestMapping("/api/getExtentions/v1")
	public ResponseData getExtentions(String projectCode,Integer status) {
		OptResult info = null;
		List<UserinfoPojo> userinfoList= null;
		try {
			if (StringUtils.isEmpty(projectCode)) {
				info = EResCodeServer.svceErrNoProjectCode.getOptResult(logger);
			} else {
				CallProjectPojo callProjectPojo= new CallProjectPojo();
				callProjectPojo.setProjectCode(projectCode);
				callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
				if (callProjectPojo==null){
					info = EResCodeServer.svceErrProjectCode.getOptResult(logger);
				}else {
					userinfoList = this.callTaskService.getUserinfoPojos(callProjectPojo);
					info = EResCodeCommon.svceRigGetDataSuccess.getOptResult(logger);
				}
			}
		} catch (Exception ex) {
			info = EResCodeServer.exptGetExtentionByProject.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info, userinfoList);
	}


	/**
	 * 置忙置闲
	 * 1 workStatus String √ 0不可通话、1可通话
	 * 2 username String √ 分配给坐席的分机号
	 */
	@RequestMapping("/api/setWorkStatus/v1")
	public ResponseData setWorkStatus(String username,Integer workStatus) {
		OptResult info = null;
		UserinfoPojo userinfoPojo = null;
		logger.info("坐席{}置忙置闲{}",username,workStatus);
		try {
			if (StringUtils.isEmpty(username)) {
				info = EResCodeServer.svceErrNoUsername.getOptResult(logger);
			} else {
				userinfoPojo  = this.userInfoServiceApi.getByUsername(username);
				if (workStatus!=null){
					if (userinfoPojo!=null&&userinfoPojo.getWorkStatus()>-1){

						CallProjectPojo callProjectPojo= new CallProjectPojo();
						callProjectPojo.setId(userinfoPojo.getProjectId());
						callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);

						CallTaskPojo callTaskPojo = new CallTaskPojo();
						callTaskPojo.setProjectCode(callProjectPojo.getProjectCode());
						callTaskPojo = this.callTaskService.getProc(callTaskPojo);

						if (callTaskPojo!=null &&!callTaskPojo.getScheduleType().equals(4)&& (callTaskPojo.getStatus().equals(2)||callTaskPojo.getStatus().equals(3))){
							EslMessage eslMessage=null;
							if (workStatus==1){

								String params = " 1 5 0";
								if (callTaskPojo.getScheduleType().equals(11)){
									params = " 1 50 0";
								}
								eslMessage=inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
										"fifo_member add",callTaskPojo.getProjectCode()+" {fifo_record_template=$${recordings_dir}/${strftime(%Y-%m-%d-%H-%M-%S)}."+userinfoPojo.getUsername()+".mp3," +
												"sip_h_X-Call-Task-Code="+callTaskPojo.getCallTaskCode()+"}user/"+userinfoPojo.getUsername()+params);
							}else if (workStatus==0){
								eslMessage=inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
										"fifo_member del",callTaskPojo.getProjectCode()+" {fifo_record_template=$${recordings_dir}/${strftime(%Y-%m-%d-%H-%M-%S)}."+userinfoPojo.getUsername()+".mp3," +
												"sip_h_X-Call-Task-Code="+callTaskPojo.getCallTaskCode()+"}user/"+userinfoPojo.getUsername());
							}
							logger.info("任务{}坐席{}置忙置闲{}结果{}", callTaskPojo.getCallTaskCode(),userinfoPojo.getUsername(),workStatus,EslHelper.formatEslMessage(eslMessage));
						}else if (StringUtils.isNotBlank(userinfoPojo.getCalluuid())&&workStatus==0&&userinfoPojo.getSessionStatus().equals(0)){
							EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
									"uuid_kill",userinfoPojo.getCalluuid());
							logger.info("摘机坐席{}置忙结果{}",userinfoPojo.getUsername(), EslHelper.formatEslMessage(eslMessage));
						}else if (StringUtils.isNotBlank(userinfoPojo.getCalluuid())&&workStatus==0&&userinfoPojo.getSessionStatus().equals(10)){
							baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_SESSION_AFTER.getCode()+userinfoPojo.getUsername(),-1,600);
						}
						userinfoPojo.setWorkStatus(workStatus);
						msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);
						msgOperations.convertAndSend("/topic/callExtStatus/" + callProjectPojo.getProjectCode(), userinfoPojo);
						UserinfoPojo notify = userinfoPojo;
						this.callTaskService.notifyAgentStatus(notify);
						userinfoPojo.setStatus(null);
						this.userInfoServiceApi.update(userinfoPojo);
					}
				}
				info = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
			}
		} catch (Exception ex) {
			info = EResCodeServer.exptSetWorkStatus.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info,userinfoPojo);
	}

	/**
	 * 置忙置闲
	 * 1 workStatus String √ 0不可通话、1可通话
	 * 2 projectCode String √ 项目代码
	 */
	@RequestMapping("/api/setWorkStatusByProjectCode/v1")
	public ResponseData setWorkStatusByProjectCode(String projectCode,Integer workStatus) {
		OptResult info = null;
		try {
			if (StringUtils.isEmpty(projectCode)) {
				info = EResCodeServer.svceErrNoProjectCode.getOptResult(logger);
			} else {
				CallProjectPojo callProjectPojo= new CallProjectPojo();
				callProjectPojo.setProjectCode(projectCode);
				callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
				if (callProjectPojo==null){
					info = EResCodeServer.svceErrProjectCode.getOptResult(logger);
				}else {
					CallTaskPojo callTaskPojo = new CallTaskPojo();
					callTaskPojo.setProjectCode(callProjectPojo.getProjectCode());
					callTaskPojo = this.callTaskService.getProc(callTaskPojo);

					List<UserinfoPojo> userinfoList = this.userInfoServiceApi.getListByProjectId(callProjectPojo.getId());

					for (UserinfoPojo userinfoPojo : userinfoList) {
						if (userinfoPojo.getWorkStatus().equals(-1)){
							continue;
						}
						if (callTaskPojo!=null &&!callTaskPojo.getScheduleType().equals(4)&& (callTaskPojo.getStatus().equals(2)||callTaskPojo.getStatus().equals(3))){
							EslMessage eslMessage=null;
							if (workStatus==1){

								String params = " 1 5 0";
								if (callTaskPojo.getScheduleType().equals(11)){
									params = " 1 50 0";
								}

								eslMessage=inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
										"fifo_member add",callTaskPojo.getProjectCode()+" {fifo_record_template=$${recordings_dir}/${strftime(%Y-%m-%d-%H-%M-%S)}."+userinfoPojo.getUsername()+".mp3," +
												"sip_h_X-Call-Task-Code="+callTaskPojo.getCallTaskCode()+"}user/"+userinfoPojo.getUsername()+params);
							}else if (workStatus==0){
								eslMessage=inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
										"fifo_member del",callTaskPojo.getProjectCode()+" {fifo_record_template=$${recordings_dir}/${strftime(%Y-%m-%d-%H-%M-%S)}."+userinfoPojo.getUsername()+".mp3," +
												"sip_h_X-Call-Task-Code="+callTaskPojo.getCallTaskCode()+"}user/"+userinfoPojo.getUsername());
							}
							logger.info("任务{}坐席{}置忙置闲{}结果{}", callTaskPojo.getCallTaskCode(),userinfoPojo.getUsername(),workStatus,EslHelper.formatEslMessage(eslMessage));
						}else if (StringUtils.isNotBlank(userinfoPojo.getCalluuid())&&workStatus==0&&userinfoPojo.getSessionStatus().equals(0)){
							EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
									"uuid_kill",userinfoPojo.getCalluuid());
							logger.info("摘机坐席{}置忙结果{}",userinfoPojo.getUsername(), EslHelper.formatEslMessage(eslMessage));
						}else if (StringUtils.isNotBlank(userinfoPojo.getCalluuid())&&workStatus==0&&userinfoPojo.getSessionStatus().equals(10)){
							baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_SESSION_AFTER.getCode()+userinfoPojo.getUsername(),-1,600);
						}
						userinfoPojo.setWorkStatus(workStatus);
						msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);
						msgOperations.convertAndSend("/topic/callExtStatus/" + callProjectPojo.getProjectCode(), userinfoPojo);
						UserinfoPojo notify = userinfoPojo;
						this.callTaskService.notifyAgentStatus(notify);
						userinfoPojo.setStatus(null);
						this.userInfoServiceApi.update(userinfoPojo);
					}
					info = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
				}
			}
		} catch (Exception ex) {
			info = EResCodeServer.exptSetWorkStatusByProject.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info);
	}

	/**
	 * 分机禁用启用
	 * 1 workStatus String √ -1禁用0启用
	 * 2 username String √ 分配给坐席的分机号
	 */
	@RequestMapping("/api/setExtensionStatus/v1")
	public ResponseData setExtensionStatus(String username,Integer workStatus) {
		OptResult info = null;
		UserinfoPojo userinfoPojo = null;
		try {
			if (StringUtils.isEmpty(username)) {
				info = EResCodeServer.svceErrNoUsername.getOptResult(logger);
			} else {
				userinfoPojo  = this.userInfoServiceApi.getByUsername(username);

				CallProjectPojo callProjectPojo= new CallProjectPojo();
				callProjectPojo.setId(userinfoPojo.getProjectId());
				callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
				if (workStatus!=null){
					if (workStatus==-1){
						CallTaskPojo callTaskPojo = new CallTaskPojo();
						callTaskPojo.setProjectCode(callProjectPojo.getProjectCode());
						callTaskPojo = this.callTaskService.getProc(callTaskPojo);

						if (callTaskPojo!=null&&!callTaskPojo.getScheduleType().equals(4) && (callTaskPojo.getStatus().equals(2)||callTaskPojo.getStatus().equals(3))){
							EslMessage eslMessage=null;
							eslMessage=inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
									"fifo_member del",callTaskPojo.getProjectCode()+" {fifo_record_template=$${recordings_dir}/${strftime(%Y-%m-%d-%H-%M-%S)}."+userinfoPojo.getUsername()+".mp3," +
											"sip_h_X-Call-Task-Code="+callTaskPojo.getCallTaskCode()+"}user/"+userinfoPojo.getUsername());
							logger.info("任务{}坐席{}禁用{}结果{}", callTaskPojo.getCallTaskCode(),userinfoPojo.getUsername(),workStatus,EslHelper.formatEslMessage(eslMessage));
						}else if (StringUtils.isNotBlank(userinfoPojo.getCalluuid()) &&userinfoPojo.getSessionStatus().equals(0)){
							EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
									"uuid_kill",userinfoPojo.getCalluuid());
							logger.info("摘机坐席{}禁用,挂断连接结果{}",userinfoPojo.getUsername(), EslHelper.formatEslMessage(eslMessage));
						}else if (StringUtils.isNotBlank(userinfoPojo.getCalluuid())&&userinfoPojo.getSessionStatus().equals(10)){
							baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_SESSION_AFTER.getCode()+username,-1,600);
							logger.info("摘机坐席{}禁用",userinfoPojo.getUsername());
						}
					}
					userinfoPojo.setWorkStatus(workStatus);
					msgOperations.convertAndSend("/topic/status/" + username, userinfoPojo);
					msgOperations.convertAndSend("/topic/callExtStatus/" + callProjectPojo.getProjectCode(), userinfoPojo);
					UserinfoPojo notify = userinfoPojo;
					this.callTaskService.notifyAgentStatus(notify);
					userinfoPojo.setStatus(null);
					this.userInfoServiceApi.update(userinfoPojo);
				}
				info = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
			}
		} catch (Exception ex) {
			info = EResCodeServer.exptSetExtensionStatus.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info,userinfoPojo);
	}

	/**
	 * 完结项目
	 * 1 projectCode String √ 项目代码
	 */
	@RequestMapping("/api/finishProject/v1")
	public ResponseData finishProject(String projectCode) {
		logger.info("完结项目{}", projectCode);
		OptResult info = null;
		try {
			if (StringUtils.isEmpty(projectCode)) {
				info = EResCodeServer.svceErrNoProjectCode.getOptResult(logger);
			} else {
				CallProjectPojo callProjectPojo= new CallProjectPojo();
				callProjectPojo.setProjectCode(projectCode);
				callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
				if (callProjectPojo==null){
					info = EResCodeServer.svceErrProjectCode.getOptResult(logger);
				}else {
					CallTaskPojo callTaskPojo = new CallTaskPojo();
					callTaskPojo.setProjectCode(projectCode);
					List<CallTaskPojo> callTaskPojos= this.callTaskService.getProcList(callTaskPojo);
					callTaskPojos.forEach(e->{
						logger.info("任务{}手动结束",e.getCallTaskCode());
						if (e.getStatus()>1){
							this.callTaskService.end(e);
						} else {
							callTaskService.updateCacheAfterPorjectEnd(e);
						}
					});
					callProjectPojo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					callProjectPojo.setStatus(2);
					this.callProjectService.endProject(callProjectPojo);
					info = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
				}
			}
		} catch (Exception ex) {
			info = EResCodeServer.exptFinishProject.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info);
	}

	/**
	 * 上传语音文件
	 * 1 qiniuStoreKey String √ 语音文件在七牛文件存储服务器里的存储key
	 * 2 voiceCode String √ 语音代码
	 */
	@RequestMapping("/api/uploadVoiceFile/v1")
	public ResponseData uploadVoiceFile(String voiceCode,String qiniuStoreKey) {
		OptResult info = null;
		try {
			VoicePojo voicePojo = new VoicePojo();
			voicePojo.setVoiceCode(voiceCode);
			VoicePojo oldVoice = this.voiceService.selectUnique(voicePojo);
			if (oldVoice==null){
				voicePojo.setPatch(voicePath+qiniuStoreKey);
				voicePojo.setCreateTime(DateTool.getTimestamp());
				this.voiceService.insert(voicePojo);
			}else {
				oldVoice.setPatch(voicePath+qiniuStoreKey);
				oldVoice.setCreateTime(DateTool.getTimestamp());
				this.voiceService.update(oldVoice);
			}
			info = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
		} catch (Exception ex) {
			info = EResCodeServer.exptGetUploadVoiceFile.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info);
	}




	/**
	 * 通过websocket给坐席发消息
	 */
	@RequestMapping("/api/sendToUser/v1")
	public ResponseData sendToUser(String projectCode,String username,String content) {
		OptResult optResult=null;
		try {
			msgOperations.convertAndSend("/topic/notify" +(StringUtils.isNotEmpty(projectCode)?"/"+projectCode:"") +(StringUtils.isNotEmpty(username)?"/"+username:""), content);
			optResult = EResCodeCommon.svceRigOptSuccess.getOptResult(logger);
		} catch (Exception ex) {
			optResult = EResCodeServer.exptSendToUser.getOptResult(logger);
			logger.error(optResult.getMsg(), ex);
		}
		return new ResponseData<Object>(optResult);
	}


	/**
	 * 坐席置闲
	 */
	@MessageMapping("/idle")
	public void idle(String jsonStr) {
		logger.info("坐席置闲: {}", jsonStr);
		Map<String,String> map= JsonTool.getObj(jsonStr,Map.class);
		Map<String,String> notify = new HashMap<>();
		notify.put("type","message");
		notify.put("duration","5000");

		UserinfoPojo userinfoPojo  = this.userInfoServiceApi.getByUsername(map.get("username"));
		if (userinfoPojo==null||userinfoPojo.getSessionStatus().equals(10)||!userinfoPojo.getWorkStatus().equals(0)||userinfoPojo.getStatus().equals(0)){
			logger.info("坐席{}置闲,不行",map.get("username"));
			return;
		}else if (StringUtils.isNotEmpty(baseRedisDao.get(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_IDLE.getCode()+userinfoPojo.getUsername(),String.class))){
			notify.put("data","置闲中，请等待结果");
			notify.put("msgType","success");
			msgOperations.convertAndSend("/topic/notify/" + map.get("projectCode")+"/"+map.get("username"), JsonTool.getFormatJsonString(notify));
			return;
		}

		if (map.get("workType").equals("20")){
			String eslMessage = inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
					"originate","user/"+map.get("username")+" answer,fifo:'"+map.get("projectCode")+" out wait' inline");
			logger.info("坐席{}置闲,originate结果{}",map.get("username"), eslMessage);
			if (StringUtils.isEmpty(eslMessage)){
				eslMessage = userinfoPojo.getUsername();
			}
			baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_IDLE.getCode()+userinfoPojo.getUsername(),eslMessage,60);
			notify.put("data","开始置闲，请等待结果");
			notify.put("msgType","success");

		}else if (map.get("workType").equals("30")){

			if (userinfoPojo.getWorkStatus()>-1){
				try {
					CallProjectPojo callProjectPojo= new CallProjectPojo();
					callProjectPojo.setId(userinfoPojo.getProjectId());
					callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);

					CallTaskPojo callTaskPojo = new CallTaskPojo();
					callTaskPojo.setProjectCode(callProjectPojo.getProjectCode());
					callTaskPojo = this.callTaskService.getProc(callTaskPojo);

					EslMessage eslMe=inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
							"fifo_member add",callTaskPojo.getProjectCode()+" {fifo_record_template=$${recordings_dir}/${strftime(%Y-%m-%d-%H-%M-%S)}."+userinfoPojo.getUsername()+".mp3," +
									"sip_h_X-Call-Task-Code="+callTaskPojo.getCallTaskCode()+"}user/"+userinfoPojo.getUsername()+" 1 50 0");
					logger.info("任务{}坐席{}置闲结果{}", callTaskPojo.getCallTaskCode(),userinfoPojo.getUsername(),EslHelper.formatEslMessage(eslMe));
					userinfoPojo.setWorkStatus(1);
					msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);
					msgOperations.convertAndSend("/topic/callExtStatus/" + callProjectPojo.getProjectCode(), userinfoPojo);
					UserinfoPojo notify1 = userinfoPojo;
					this.callTaskService.notifyAgentStatus(notify1);
					userinfoPojo.setStatus(null);
					this.userInfoServiceApi.update(userinfoPojo);

					notify.put("data","置闲成功");
					notify.put("msgType","success");
				}catch (Exception e){
					logger.info(e.getMessage(),e);
					notify.put("data","置闲失败");
					notify.put("msgType","error");
				}
			}
		}else {
			ResponseData responseData = setWorkStatus(map.get("username"),1);
			if (responseData.getReqResult().getCode().equals(1)){
				notify.put("data","置闲成功");
				notify.put("msgType","success");
			}else {
				notify.put("data","置闲失败");
				notify.put("msgType","error");
			}
		}
		msgOperations.convertAndSend("/topic/notify/" + map.get("projectCode")+"/"+map.get("username"), JsonTool.getFormatJsonString(notify));
	}

	/**
	 * 坐席置忙
	 */
	@MessageMapping("/busy")
	public void busy(String jsonStr) {
		logger.info("坐席置忙: {}", jsonStr);
		Map<String,String> map= JsonTool.getObj(jsonStr,Map.class);
		Map<String,String> notify = new HashMap<>();
		notify.put("type","message");
		notify.put("duration","3000");
		if (map.get("workType").equals("20")){
			UserinfoPojo userinfoPojo  = this.userInfoServiceApi.getByUsername(map.get("username"));
			if (userinfoPojo!=null&&userinfoPojo.getSessionStatus().equals(10)){
				userinfoPojo.setWorkStatus(0);
				msgOperations.convertAndSend("/topic/status/" + map.get("username"), userinfoPojo);
				baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_SESSION_AFTER.getCode()+map.get("username"),0,600);
				notify.put("data","置忙成功");
				notify.put("msgType","success");
			}else {
				EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
						"uuid_kill",userinfoPojo.getCalluuid());
				logger.info("坐席{}置忙{},结果{}",map.get("username"),userinfoPojo.getCalluuid(), EslHelper.formatEslMessage(eslMessage));
				if (!eslMessage.getBodyLines().get(0).contains("ERR")){
					notify.put("data","置忙成功");
					notify.put("msgType","success");
				}else {
					notify.put("data","置忙失败");
					notify.put("msgType","error");
				}
			}

		}else {
			ResponseData responseData = setWorkStatus(map.get("username"),0);
			if (responseData.getReqResult().getCode().equals(1)){
				notify.put("data","置忙成功");
				notify.put("msgType","success");
			}else {
				notify.put("data","置忙失败");
				notify.put("msgType","error");
			}
		}

		msgOperations.convertAndSend("/topic/notify/" + map.get("projectCode")+"/"+map.get("username"), JsonTool.getFormatJsonString(notify));
	}



	/**
	 * 监听坐席通话
	 */
	@MessageMapping("/eavesdrop")
	public void eavesdrop(String jsonStr) {
		logger.info("监听坐席通话: {}", jsonStr);
		Map<String,String> map= JsonTool.getObj(jsonStr,Map.class);
		String username = map.get("username");
		String listenerExt = map.get("listenerExt");
		String projectCode = map.get("projectCode");

		this.callProjectService.eavesdrop(projectCode, username, listenerExt);
	}

	/**
	 * 监听坐席结束
	 */
	@MessageMapping("/stopEavesdrop")
	public void stopEavesdrop(String jsonStr) {
		logger.info("监听坐席结束: {}", jsonStr);
		Map<String,String> map= JsonTool.getObj(jsonStr,Map.class);
		String listenerExt = map.get("listenerExt");
		String projectCode = map.get("projectCode");
		UserinfoPojo listener = this.userInfoServiceApi.getByUsername(listenerExt);

		if (projectCode == null){
			EslMessage eslMessage = inboundClient.sendSyncApiCommand(inboundClient.option().serverOptions().get(0).addr(),
					"uuid_kill",listener.getCalluuid());
		}else {
			CallProjectPojo callProjectPojo = new CallProjectPojo();
			callProjectPojo.setProjectCode(projectCode);
			callProjectPojo = this.callProjectService.selectUnique(callProjectPojo);
			EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
					"uuid_kill",listener.getCalluuid());
		}
	}

	/**
	 * 项目经理发起会议
	 */
	@MessageMapping("/conference")
	public void conference(String jsonStr) {
		logger.info("项目经理发起会议: {}", jsonStr);
		Map<String,String> map= JsonTool.getObj(jsonStr,Map.class);
		String projectCode = map.get("projectCode");
		String listenerExt = map.get("listenerExt");

		this.callProjectService.conference(projectCode, listenerExt);
	}

	/**
	 * 项目经理邀请坐席参加会议
	 */
	@MessageMapping("/conferenceByUsername")
	public void conferenceByUsername(String jsonStr) {
		logger.info("项目经理邀请坐席参加会议: {}", jsonStr);
		Map<String,String> map= JsonTool.getObj(jsonStr,Map.class);
		String projectCode = map.get("projectCode");
		String username = map.get("username");
		String listenerExt = map.get("listenerExt");
		UserinfoPojo userinfoPojo = this.userInfoServiceApi.getByUsername(username);
		if (!userinfoPojo.getSessionStatus().equals(0)){
			return;
		}
		CallProjectPojo callProjectPojo = new CallProjectPojo();
		callProjectPojo.setProjectCode(projectCode);
		callProjectPojo = this.callProjectService.selectUnique(callProjectPojo);
		EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
				"conference",projectCode+" list");
		if (eslMessage.getBodyLines().get(0).contains("not found")){
			inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),"conference",projectCode+" bgdial user/"+listenerExt);
			logger.info("项目经理{}发起会议{}",listenerExt,projectCode);
		}
		inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),"conference",projectCode+" bgdial user/"+username);
		logger.info("邀请坐席{}加入会议{}",username,projectCode);
	}

	/**
	 * 项目经理结束会议
	 */
	@MessageMapping("/stopConference")
	public void stopConference(String jsonStr) {
		logger.info("项目经理结束会议: {}", jsonStr);
		Map<String,String> map= JsonTool.getObj(jsonStr,Map.class);
		String projectCode = map.get("projectCode");
		String listenerExt = map.get("listenerExt");
		UserinfoPojo userinfoPojo = this.userInfoServiceApi.getByUsername(listenerExt);
		CallProjectPojo callProjectPojo = new CallProjectPojo();
		callProjectPojo.setProjectCode(projectCode);
		callProjectPojo = this.callProjectService.selectUnique(callProjectPojo);
		inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),"conference",projectCode+" hup all");
		logger.info("项目经理{}结束会议{}",listenerExt,projectCode);
	}


	/**
	 * 坐席播放语音
	 */
	@MessageMapping("/playVoice")
	public void playVoice(String jsonStr) {
		logger.info("坐席播放语音 {}", jsonStr);
		Map<String,String> map=JsonTool.getObj(jsonStr,Map.class);
		String username = map.get("username");
		String callId = map.get("callId");

		VoicePojo voicePojo = new VoicePojo();
		voicePojo.setVoiceCode(map.get("voiceCode"));
		voicePojo = this.voiceService.selectUnique(voicePojo);
		UserinfoPojo userinfoPojo  = this.userInfoServiceApi.getByUsername(username);

		// 给坐席播放
		EslMessage uuid_displaceForAgent = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
				"uuid_displace",userinfoPojo.getCalluuid()+" start "+voicePojo.getPatch()+" 0 mux");
		logger.info("坐席{}uuid_displaceForAgent:{}结果{}",username,userinfoPojo.getCalluuid(), EslHelper.formatEslMessage(uuid_displaceForAgent));
		if (!uuid_displaceForAgent.getBodyLines().get(0).contains("ERR")){
			// 给接听者播放
			EslMessage uuid_displaceForCall = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
					"uuid_displace",callId+" start "+voicePojo.getPatch()+" 0 mux");
			logger.info("坐席{}uuid_displaceForCall:{}结果{}",username,callId, EslHelper.formatEslMessage(uuid_displaceForCall));
			// 把坐席静音
			EslMessage uuid_audioForAgent = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
					"uuid_audio",userinfoPojo.getCalluuid()+" start read mute -4");
			logger.info("坐席{}uuid_audioForAgent:{}结果{}",username,userinfoPojo.getCalluuid(), EslHelper.formatEslMessage(uuid_audioForAgent));
		}
	}

	/**
	 * 坐席停止播放语音
	 */
	@MessageMapping("/stopPlayVoice")
	public void stopPlayVoice(String jsonStr) {
		logger.info("坐席停止播放语音 {}", jsonStr);
		Map<String,String> map=JsonTool.getObj(jsonStr,Map.class);
		String username = map.get("username");
		String callId = map.get("callId");

		VoicePojo voicePojo = new VoicePojo();
		voicePojo.setVoiceCode(map.get("voiceCode"));
		voicePojo = this.voiceService.selectUnique(voicePojo);
		UserinfoPojo userinfoPojo  = this.userInfoServiceApi.getByUsername(username);
//
//		// 把坐席静音恢复
//		EslMessage uuid_audioForAgent = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
//				"uuid_audio",userinfoPojo.getCalluuid()+" stop");
//		logger.info("坐席{}uuid_audioForAgent:{}结果{}",username,userinfoPojo.getCalluuid(), EslHelper.formatEslMessage(uuid_audioForAgent));

		// 停止给坐席播放
		EslMessage uuid_displaceForAgent = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
				"uuid_displace",userinfoPojo.getCalluuid()+" stop "+voicePojo.getPatch()+" 0 mux");
		logger.info("坐席{}uuid_displaceForAgent:{}结果{}",username,userinfoPojo.getCalluuid(), EslHelper.formatEslMessage(uuid_displaceForAgent));

		// 停止给接听者播放
		EslMessage uuid_displaceForCall = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
				"uuid_displace",callId+" stop "+voicePojo.getPatch()+" 0 mux");
		logger.info("坐席{}uuid_displaceForCall:{}结果{}",username,callId, EslHelper.formatEslMessage(uuid_displaceForCall));
	}

	/**
	 * 坐席通话转移
	 */
	@MessageMapping("/transfer")
	public void transfer(String jsonStr) {
		logger.info("坐席通话转移 {}", jsonStr);
		Map<String,String> map=JsonTool.getObj(jsonStr,Map.class);
		String username = map.get("username");
		String otherUsername = map.get("otherUsername");
		String callId = map.get("callId");
		UserinfoPojo userinfoPojo  = this.userInfoServiceApi.getByUsername(username);
		UserinfoPojo otherUserinfo = this.userInfoServiceApi.getByUsername(otherUsername);
		Map<String,String> notify = new HashMap<>();
		notify.put("type","message");
		notify.put("duration","3000");
		if (otherUserinfo.getStatus().equals(0)||otherUserinfo.getSessionStatus().equals(10)||otherUserinfo.getWorkStatus().equals(1)){
			notify.put("data",otherUsername+"状态不行，无法转移");
			notify.put("msgType","error");
		}else{
			EslMessage uuid_transfer = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
					"uuid_transfer",userinfoPojo.getCalluuid()+" -bleg 'bridge:{call_transfer=true}user/"+otherUsername+"' inline");
			logger.info("坐席{}uuid_transfer:{}结果{}",username,callId, EslHelper.formatEslMessage(uuid_transfer));
			notify.put("data","转移成功");
			notify.put("msgType","success");
		}
		msgOperations.convertAndSend("/topic/notify/" + username, JsonTool.getFormatJsonString(notify));
	}
}
