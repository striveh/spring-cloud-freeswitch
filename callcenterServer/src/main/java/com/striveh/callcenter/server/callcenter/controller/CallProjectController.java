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
 * @??????:???callProject ??????????????????controller
 * @?????????:callcenterServer
 * @??????:xxx
 * @??????:2020-04-06 12:13:48
 * @?????????<pre></pre>
 */
@RestController
@RequestMapping("/callProject")
public class CallProjectController extends BaseController<CallProjectPojo> {
	/** callProject ???????????????service*/
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
	 * ????????????????????????
	 * 1: projectCode String ??? ????????????
	 * 2: seats Integer X ?????????
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

					//????????????????????????FreeSWITCH?????????????????????
					//???????????????FreeSWITCH????????????
					FreeswitchPojo freeswitchPojo = new FreeswitchPojo();
					freeswitchPojo.setStatus(1);
					List<FreeswitchPojo> freeswitchPojos = this.freeswitchService.selectList(freeswitchPojo);
					finalCallProjectPojo = callProjectPojo;
					for (FreeswitchPojo e : freeswitchPojos) {
						//????????????????????????????????????
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
	 * ????????????????????????
	 * 1:certificate String ??? ????????????????????????
	 * 2:extension String X ???????????????????????????
	 * 3:type Integer ??? 2????????????3????????????
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
					//????????????
					userinfoPojo=new UserinfoPojo();
					userinfoPojo.setUsername(StringTool.getRandom(8));
					userinfoPojo.setPassword(StringTool.getRandom(7));
					userinfoPojo.setWorkStatus(0);
					userinfoPojo.setType(type);
					this.userInfoServiceApi.add(userinfoPojo);
					logger.info("??????{}????????????,type:{}",userinfoPojo.getUsername(),type);
					info = EResCodeCommon.svceRigGetDataSuccess.getOptResult(logger);
				}else {
					callProjectPojo.setCertificate(certificate);
					callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
					if (callProjectPojo==null){
						info = EResCodeServer.svceErrCertificate.getOptResult(logger);
					}else {
						//????????????
						userinfoPojo=new UserinfoPojo();
						userinfoPojo.setProjectId(callProjectPojo.getId());
						userinfoPojo.setFreeswitchId(callProjectPojo.getFreeswitchId());
						userinfoPojo.setUsername(StringTool.getRandom(8));
						userinfoPojo.setPassword(StringTool.getRandom(7));
						userinfoPojo.setWorkStatus(0);
						userinfoPojo.setType(type);
						this.userInfoServiceApi.add(userinfoPojo);
						logger.info("??????{}????????????,type:{}",userinfoPojo.getUsername(),type);
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
	 * ?????????????????????
	 * 1:projectCode String ??? ????????????
	 * 2:username String ??? ???????????????????????????
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
					logger.info("??????{}??????{}????????????",projectCode,username);
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
	 * ????????????????????????
	 * 1 projectCode String ??? ????????????
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
	 * ????????????
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
	 * ????????????
	 * 1 workStatus String ??? 0???????????????1?????????
	 * 2 username String ??? ???????????????????????????
	 */
	@RequestMapping("/api/setWorkStatus/v1")
	public ResponseData setWorkStatus(String username,Integer workStatus) {
		OptResult info = null;
		UserinfoPojo userinfoPojo = null;
		logger.info("??????{}????????????{}",username,workStatus);
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
							logger.info("??????{}??????{}????????????{}??????{}", callTaskPojo.getCallTaskCode(),userinfoPojo.getUsername(),workStatus,EslHelper.formatEslMessage(eslMessage));
						}else if (StringUtils.isNotBlank(userinfoPojo.getCalluuid())&&workStatus==0&&userinfoPojo.getSessionStatus().equals(0)){
							EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
									"uuid_kill",userinfoPojo.getCalluuid());
							logger.info("????????????{}????????????{}",userinfoPojo.getUsername(), EslHelper.formatEslMessage(eslMessage));
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
	 * ????????????
	 * 1 workStatus String ??? 0???????????????1?????????
	 * 2 projectCode String ??? ????????????
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
							logger.info("??????{}??????{}????????????{}??????{}", callTaskPojo.getCallTaskCode(),userinfoPojo.getUsername(),workStatus,EslHelper.formatEslMessage(eslMessage));
						}else if (StringUtils.isNotBlank(userinfoPojo.getCalluuid())&&workStatus==0&&userinfoPojo.getSessionStatus().equals(0)){
							EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
									"uuid_kill",userinfoPojo.getCalluuid());
							logger.info("????????????{}????????????{}",userinfoPojo.getUsername(), EslHelper.formatEslMessage(eslMessage));
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
	 * ??????????????????
	 * 1 workStatus String ??? -1??????0??????
	 * 2 username String ??? ???????????????????????????
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
							logger.info("??????{}??????{}??????{}??????{}", callTaskPojo.getCallTaskCode(),userinfoPojo.getUsername(),workStatus,EslHelper.formatEslMessage(eslMessage));
						}else if (StringUtils.isNotBlank(userinfoPojo.getCalluuid()) &&userinfoPojo.getSessionStatus().equals(0)){
							EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
									"uuid_kill",userinfoPojo.getCalluuid());
							logger.info("????????????{}??????,??????????????????{}",userinfoPojo.getUsername(), EslHelper.formatEslMessage(eslMessage));
						}else if (StringUtils.isNotBlank(userinfoPojo.getCalluuid())&&userinfoPojo.getSessionStatus().equals(10)){
							baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_SESSION_AFTER.getCode()+username,-1,600);
							logger.info("????????????{}??????",userinfoPojo.getUsername());
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
	 * ????????????
	 * 1 projectCode String ??? ????????????
	 */
	@RequestMapping("/api/finishProject/v1")
	public ResponseData finishProject(String projectCode) {
		logger.info("????????????{}", projectCode);
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
						logger.info("??????{}????????????",e.getCallTaskCode());
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
	 * ??????????????????
	 * 1 qiniuStoreKey String ??? ??????????????????????????????????????????????????????key
	 * 2 voiceCode String ??? ????????????
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
	 * ??????websocket??????????????????
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
	 * ????????????
	 */
	@MessageMapping("/idle")
	public void idle(String jsonStr) {
		logger.info("????????????: {}", jsonStr);
		Map<String,String> map= JsonTool.getObj(jsonStr,Map.class);
		Map<String,String> notify = new HashMap<>();
		notify.put("type","message");
		notify.put("duration","5000");

		UserinfoPojo userinfoPojo  = this.userInfoServiceApi.getByUsername(map.get("username"));
		if (userinfoPojo==null||userinfoPojo.getSessionStatus().equals(10)||!userinfoPojo.getWorkStatus().equals(0)||userinfoPojo.getStatus().equals(0)){
			logger.info("??????{}??????,??????",map.get("username"));
			return;
		}else if (StringUtils.isNotEmpty(baseRedisDao.get(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_IDLE.getCode()+userinfoPojo.getUsername(),String.class))){
			notify.put("data","???????????????????????????");
			notify.put("msgType","success");
			msgOperations.convertAndSend("/topic/notify/" + map.get("projectCode")+"/"+map.get("username"), JsonTool.getFormatJsonString(notify));
			return;
		}

		if (map.get("workType").equals("20")){
			String eslMessage = inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
					"originate","user/"+map.get("username")+" answer,fifo:'"+map.get("projectCode")+" out wait' inline");
			logger.info("??????{}??????,originate??????{}",map.get("username"), eslMessage);
			if (StringUtils.isEmpty(eslMessage)){
				eslMessage = userinfoPojo.getUsername();
			}
			baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_AGENT_WORK_STATUS_IDLE.getCode()+userinfoPojo.getUsername(),eslMessage,60);
			notify.put("data","??????????????????????????????");
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
					logger.info("??????{}??????{}????????????{}", callTaskPojo.getCallTaskCode(),userinfoPojo.getUsername(),EslHelper.formatEslMessage(eslMe));
					userinfoPojo.setWorkStatus(1);
					msgOperations.convertAndSend("/topic/status/" + userinfoPojo.getUsername(), userinfoPojo);
					msgOperations.convertAndSend("/topic/callExtStatus/" + callProjectPojo.getProjectCode(), userinfoPojo);
					UserinfoPojo notify1 = userinfoPojo;
					this.callTaskService.notifyAgentStatus(notify1);
					userinfoPojo.setStatus(null);
					this.userInfoServiceApi.update(userinfoPojo);

					notify.put("data","????????????");
					notify.put("msgType","success");
				}catch (Exception e){
					logger.info(e.getMessage(),e);
					notify.put("data","????????????");
					notify.put("msgType","error");
				}
			}
		}else {
			ResponseData responseData = setWorkStatus(map.get("username"),1);
			if (responseData.getReqResult().getCode().equals(1)){
				notify.put("data","????????????");
				notify.put("msgType","success");
			}else {
				notify.put("data","????????????");
				notify.put("msgType","error");
			}
		}
		msgOperations.convertAndSend("/topic/notify/" + map.get("projectCode")+"/"+map.get("username"), JsonTool.getFormatJsonString(notify));
	}

	/**
	 * ????????????
	 */
	@MessageMapping("/busy")
	public void busy(String jsonStr) {
		logger.info("????????????: {}", jsonStr);
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
				notify.put("data","????????????");
				notify.put("msgType","success");
			}else {
				EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
						"uuid_kill",userinfoPojo.getCalluuid());
				logger.info("??????{}??????{},??????{}",map.get("username"),userinfoPojo.getCalluuid(), EslHelper.formatEslMessage(eslMessage));
				if (!eslMessage.getBodyLines().get(0).contains("ERR")){
					notify.put("data","????????????");
					notify.put("msgType","success");
				}else {
					notify.put("data","????????????");
					notify.put("msgType","error");
				}
			}

		}else {
			ResponseData responseData = setWorkStatus(map.get("username"),0);
			if (responseData.getReqResult().getCode().equals(1)){
				notify.put("data","????????????");
				notify.put("msgType","success");
			}else {
				notify.put("data","????????????");
				notify.put("msgType","error");
			}
		}

		msgOperations.convertAndSend("/topic/notify/" + map.get("projectCode")+"/"+map.get("username"), JsonTool.getFormatJsonString(notify));
	}



	/**
	 * ??????????????????
	 */
	@MessageMapping("/eavesdrop")
	public void eavesdrop(String jsonStr) {
		logger.info("??????????????????: {}", jsonStr);
		Map<String,String> map= JsonTool.getObj(jsonStr,Map.class);
		String username = map.get("username");
		String listenerExt = map.get("listenerExt");
		String projectCode = map.get("projectCode");

		this.callProjectService.eavesdrop(projectCode, username, listenerExt);
	}

	/**
	 * ??????????????????
	 */
	@MessageMapping("/stopEavesdrop")
	public void stopEavesdrop(String jsonStr) {
		logger.info("??????????????????: {}", jsonStr);
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
	 * ????????????????????????
	 */
	@MessageMapping("/conference")
	public void conference(String jsonStr) {
		logger.info("????????????????????????: {}", jsonStr);
		Map<String,String> map= JsonTool.getObj(jsonStr,Map.class);
		String projectCode = map.get("projectCode");
		String listenerExt = map.get("listenerExt");

		this.callProjectService.conference(projectCode, listenerExt);
	}

	/**
	 * ????????????????????????????????????
	 */
	@MessageMapping("/conferenceByUsername")
	public void conferenceByUsername(String jsonStr) {
		logger.info("????????????????????????????????????: {}", jsonStr);
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
			logger.info("????????????{}????????????{}",listenerExt,projectCode);
		}
		inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),"conference",projectCode+" bgdial user/"+username);
		logger.info("????????????{}????????????{}",username,projectCode);
	}

	/**
	 * ????????????????????????
	 */
	@MessageMapping("/stopConference")
	public void stopConference(String jsonStr) {
		logger.info("????????????????????????: {}", jsonStr);
		Map<String,String> map= JsonTool.getObj(jsonStr,Map.class);
		String projectCode = map.get("projectCode");
		String listenerExt = map.get("listenerExt");
		UserinfoPojo userinfoPojo = this.userInfoServiceApi.getByUsername(listenerExt);
		CallProjectPojo callProjectPojo = new CallProjectPojo();
		callProjectPojo.setProjectCode(projectCode);
		callProjectPojo = this.callProjectService.selectUnique(callProjectPojo);
		inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),"conference",projectCode+" hup all");
		logger.info("????????????{}????????????{}",listenerExt,projectCode);
	}


	/**
	 * ??????????????????
	 */
	@MessageMapping("/playVoice")
	public void playVoice(String jsonStr) {
		logger.info("?????????????????? {}", jsonStr);
		Map<String,String> map=JsonTool.getObj(jsonStr,Map.class);
		String username = map.get("username");
		String callId = map.get("callId");

		VoicePojo voicePojo = new VoicePojo();
		voicePojo.setVoiceCode(map.get("voiceCode"));
		voicePojo = this.voiceService.selectUnique(voicePojo);
		UserinfoPojo userinfoPojo  = this.userInfoServiceApi.getByUsername(username);

		// ???????????????
		EslMessage uuid_displaceForAgent = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
				"uuid_displace",userinfoPojo.getCalluuid()+" start "+voicePojo.getPatch()+" 0 mux");
		logger.info("??????{}uuid_displaceForAgent:{}??????{}",username,userinfoPojo.getCalluuid(), EslHelper.formatEslMessage(uuid_displaceForAgent));
		if (!uuid_displaceForAgent.getBodyLines().get(0).contains("ERR")){
			// ??????????????????
			EslMessage uuid_displaceForCall = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
					"uuid_displace",callId+" start "+voicePojo.getPatch()+" 0 mux");
			logger.info("??????{}uuid_displaceForCall:{}??????{}",username,callId, EslHelper.formatEslMessage(uuid_displaceForCall));
			// ???????????????
			EslMessage uuid_audioForAgent = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
					"uuid_audio",userinfoPojo.getCalluuid()+" start read mute -4");
			logger.info("??????{}uuid_audioForAgent:{}??????{}",username,userinfoPojo.getCalluuid(), EslHelper.formatEslMessage(uuid_audioForAgent));
		}
	}

	/**
	 * ????????????????????????
	 */
	@MessageMapping("/stopPlayVoice")
	public void stopPlayVoice(String jsonStr) {
		logger.info("???????????????????????? {}", jsonStr);
		Map<String,String> map=JsonTool.getObj(jsonStr,Map.class);
		String username = map.get("username");
		String callId = map.get("callId");

		VoicePojo voicePojo = new VoicePojo();
		voicePojo.setVoiceCode(map.get("voiceCode"));
		voicePojo = this.voiceService.selectUnique(voicePojo);
		UserinfoPojo userinfoPojo  = this.userInfoServiceApi.getByUsername(username);
//
//		// ?????????????????????
//		EslMessage uuid_audioForAgent = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
//				"uuid_audio",userinfoPojo.getCalluuid()+" stop");
//		logger.info("??????{}uuid_audioForAgent:{}??????{}",username,userinfoPojo.getCalluuid(), EslHelper.formatEslMessage(uuid_audioForAgent));

		// ?????????????????????
		EslMessage uuid_displaceForAgent = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
				"uuid_displace",userinfoPojo.getCalluuid()+" stop "+voicePojo.getPatch()+" 0 mux");
		logger.info("??????{}uuid_displaceForAgent:{}??????{}",username,userinfoPojo.getCalluuid(), EslHelper.formatEslMessage(uuid_displaceForAgent));

		// ????????????????????????
		EslMessage uuid_displaceForCall = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
				"uuid_displace",callId+" stop "+voicePojo.getPatch()+" 0 mux");
		logger.info("??????{}uuid_displaceForCall:{}??????{}",username,callId, EslHelper.formatEslMessage(uuid_displaceForCall));
	}

	/**
	 * ??????????????????
	 */
	@MessageMapping("/transfer")
	public void transfer(String jsonStr) {
		logger.info("?????????????????? {}", jsonStr);
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
			notify.put("data",otherUsername+"???????????????????????????");
			notify.put("msgType","error");
		}else{
			EslMessage uuid_transfer = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
					"uuid_transfer",userinfoPojo.getCalluuid()+" -bleg 'bridge:{call_transfer=true}user/"+otherUsername+"' inline");
			logger.info("??????{}uuid_transfer:{}??????{}",username,callId, EslHelper.formatEslMessage(uuid_transfer));
			notify.put("data","????????????");
			notify.put("msgType","success");
		}
		msgOperations.convertAndSend("/topic/notify/" + username, JsonTool.getFormatJsonString(notify));
	}
}
