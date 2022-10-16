package com.striveh.callcenter.server.ESLEventHandler;

import com.striveh.callcenter.common.constant.param.ERedisCacheKey;
import com.striveh.callcenter.common.database.redis.BaseRedisDao;
import com.striveh.callcenter.common.storage.StorageService;
import com.striveh.callcenter.common.util.DateTool;
import com.striveh.callcenter.common.util.StringTool;
import com.striveh.callcenter.pojo.callcenter.CallLogPojo;
import com.striveh.callcenter.pojo.callcenter.CallProjectPojo;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallLogService;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallProjectService;
import com.striveh.callcenter.server.callcenter.service.iservice.ICallTaskService;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.constant.EventNames;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.spring.boot.starter.annotation.EslEventName;
import link.thingscloud.freeswitch.esl.spring.boot.starter.handler.EslEventHandler;
import link.thingscloud.freeswitch.esl.transport.event.EslEvent;
import link.thingscloud.freeswitch.esl.transport.message.EslMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@EslEventName(EventNames.CHANNEL_HANGUP_COMPLETE)
@Component
public class ChannelHangupCompleteEslEventHandler implements EslEventHandler {
    protected Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    private ICallLogService callLogService;
    @Autowired
    @Qualifier("baseRedisDaoDef")
    private BaseRedisDao baseRedisDao;
    @Autowired
    @Qualifier("baseRedisDaoDB0")
    private BaseRedisDao baseRedisDaoDB0;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ICallProjectService callProjectService;
    @Autowired
    @Lazy
    private ICallTaskService callTaskService;
    @Autowired
    private InboundClient inboundClient;
    @Override
    public void handle(String addr, EslEvent event) {

        try {
            log.info("ChannelHangupCompleteEslEventHandler handle addr[{}] EslEvent[{}].", addr, EslHelper.formatEslEvent(event));
            if (StringUtils.isEmpty(event.getEventHeaders().get("variable_callcenter_project_code"))
                    &&StringUtils.isEmpty(event.getEventHeaders().get("variable_sip_h_X-Call-Task-Code"))
                    &&StringUtils.isEmpty(event.getEventHeaders().get("variable_return_visit"))){
                return;
            }

            //setResultDetail:
            //1:空号5:未接通10:呼叫被拒绝20:客户忙25:客户未接听
            //28:等待坐席接通时挂断30:坐席忙32:坐席未接听35:坐席全忙40:排队超时挂断
            //50:呼叫成功坐席挂断
            //55:呼叫成功客户挂断
            //setResult:
            //10:成功20:失败30:漏接


            //回访逻辑:根据variable_return_visit判断是否是回访、根据呼叫号码是否大于等于11位判断是否是呼向手机用户的
            if (StringUtils.isNotBlank(event.getEventHeaders().get("variable_return_visit"))
                    &&event.getEventHeaders().get("Caller-Destination-Number").length()>=11){
                String telNo=event.getEventHeaders().get("Caller-Destination-Number");
                String phonePrefix = event.getEventHeaders().get("variable_phonePrefix");
                if (phonePrefix==null){
                    phonePrefix="";
                }
                if (telNo.length()>11){
//                    telNo=telNo.substring(telNo.length()-11);
                    telNo=telNo.replaceFirst(phonePrefix,"");
                }
                CallLogPojo callLogPojo = new CallLogPojo();
                callLogPojo.setCallUUID(event.getEventHeaders().get("variable_uuid"));
                callLogPojo=this.callLogService.selectUnique(callLogPojo);

                callLogPojo.setEndTimestamp(new Timestamp(System.currentTimeMillis()));
                callLogPojo.setDuration(Long.valueOf(event.getEventHeaders().get("variable_duration")));
                callLogPojo.setBillsec(Long.valueOf(event.getEventHeaders().get("variable_billsec")));

                if (StringTool.isEmpty(event.getEventHeaders().get("variable_proto_specific_hangup_cause"))){
                    if (event.getEventHeaders().get("Hangup-Cause").equals("NORMAL_CLEARING")){
                        callLogPojo.setResultDetail("50");
                        callLogPojo.setResult("10");
                        if (callLogPojo.getBillsec().equals(0L)){
                            callLogPojo.setResultDetail("25");
                            callLogPojo.setResult("20");
                        }
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("RECOVERY_ON_TIMER_EXPIRE")){
                        callLogPojo.setResultDetail("5");
                        callLogPojo.setResult("20");
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("NO_ANSWER")){
                        callLogPojo.setResultDetail("25");
                        callLogPojo.setResult("20");
                    }else {
                        callLogPojo.setResultDetail("5");
                        callLogPojo.setResult("20");
                        log.info("{}该挂断cause未处理，请补充",event.getEventHeaders().get("Hangup-Cause"));
                    }
                }else {
                    if (event.getEventHeaders().get("Hangup-Cause").equals("NORMAL_CLEARING")){
                            callLogPojo.setResultDetail("55");
                            callLogPojo.setResult("10");
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("RECOVERY_ON_TIMER_EXPIRE")||event.getEventHeaders().get("Hangup-Cause").equals("NO_ANSWER")){
                        callLogPojo.setResultDetail("25");
                        callLogPojo.setResult("20");
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("NO_USER_RESPONSE")){
                        callLogPojo.setResultDetail("20");
                        callLogPojo.setResult("20");
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("ORIGINATOR_CANCEL")){
                        callLogPojo.setResultDetail("25");
                        callLogPojo.setResult("20");
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("NORMAL_TEMPORARY_FAILURE")
                            ||event.getEventHeaders().get("Hangup-Cause").equals("INCOMPATIBLE_DESTINATION")
                            ||event.getEventHeaders().get("Hangup-Cause").equals("UNALLOCATED_NUMBER")){
                        callLogPojo.setResultDetail("10");
                        callLogPojo.setResult("20");
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("USER_BUSY")){
                        callLogPojo.setResultDetail("20");
                        callLogPojo.setResult("20");
                    }else {
                        callLogPojo.setResultDetail("5");
                        callLogPojo.setResult("20");
                        log.info("{}该挂断cause未处理，请补充",event.getEventHeaders().get("Hangup-Cause"));
                    }
                }

                try {
                    if (callLogPojo.getResult().equals("10")){
                        String recordPath=event.getEventHeaders().get("variable_execute_on_answer");
                        callLogPojo.setRecordFile(recordPath.substring(recordPath.indexOf(" ")+1));
                        File file=new File(callLogPojo.getRecordFile());
                        if (file.exists()){
                            String storeKey = DateTool.formatDate("yyyyMMdd")+"_"+StringTool.MD5Encode(event.getEventHeaders().get("variable_uuid"))+".mp3";
                            storageService.store(FileUtils.openInputStream(file),0L,"audio/mp3",storeKey);
                            callLogPojo.setRecordFileQiniu(storageService.getStorage().getBucketName()+"|"+storeKey);
                            file.delete();
                        }
                    }
                    if (StringUtils.isNotEmpty(callLogPojo.getPriceRule())){
                        String [] priceRule= callLogPojo.getPriceRule().split("\\|");
                        callLogPojo.setPriceCal(Math.ceil(((double)callLogPojo.getBillsec())/Double.valueOf(priceRule[0]))*Double.valueOf(priceRule[1]));
                    }
                }catch (Exception e){
                    log.error("ChannelHangupCompleteEslEventHandler处理异常",e);
                }

                callLogPojo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                callLogPojo.setHangupCase("return_visit");

                setEvaluation(callLogPojo);
                this.callLogService.update(callLogPojo);
                baseRedisDaoDB0.leftPush(ERedisCacheKey.CALLCENTER_CALLLOG_QUEUE.getCode(), callLogPojo);
                return;
            }else if (StringUtils.isNotBlank(event.getEventHeaders().get("variable_return_visit"))){
                return;
            }else if (StringUtils.isNotBlank(event.getEventHeaders().get("variable_preview_call"))
                        &&event.getEventHeaders().get("Caller-Destination-Number").length()>=11){
                //给坐席分配号码（预览式拨打）

                String telNo=event.getEventHeaders().get("Caller-Destination-Number");
                String phonePrefix = event.getEventHeaders().get("variable_phonePrefix");
                if (phonePrefix==null){
                    phonePrefix="";
                }
                if (telNo.length()>11){
//                    telNo=telNo.substring(telNo.length()-11);
                    telNo=telNo.replaceFirst(phonePrefix,"");
                }
                CallLogPojo callLogPojo = new CallLogPojo();
                callLogPojo.setDestinationNumber(telNo);
                callLogPojo=this.callLogService.selectUnique(callLogPojo);
                baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+callLogPojo.getCallTaskCode()+"_"+callLogPojo.getGwCode(),callLogPojo.getDestinationNumber());

                callLogPojo.setEndTimestamp(new Timestamp(System.currentTimeMillis()));
                callLogPojo.setDuration(Long.valueOf(event.getEventHeaders().get("variable_duration")));
                callLogPojo.setBillsec(Long.valueOf(event.getEventHeaders().get("variable_billsec")));

                if (StringTool.isEmpty(event.getEventHeaders().get("variable_proto_specific_hangup_cause"))){
                    if (event.getEventHeaders().get("Hangup-Cause").equals("NORMAL_CLEARING")){
                        callLogPojo.setResultDetail("50");
                        callLogPojo.setResult("10");
                        if (callLogPojo.getBillsec().equals(0L)){
                            callLogPojo.setResultDetail("25");
                            callLogPojo.setResult("20");
                        }
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("RECOVERY_ON_TIMER_EXPIRE")){
                        callLogPojo.setResultDetail("5");
                        callLogPojo.setResult("20");
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("NO_ANSWER")){
                        callLogPojo.setResultDetail("25");
                        callLogPojo.setResult("20");
                    }else {
                        callLogPojo.setResultDetail("5");
                        callLogPojo.setResult("20");
                        log.info("{}该挂断cause未处理，请补充",event.getEventHeaders().get("Hangup-Cause"));
                    }
                }else {
                    if (event.getEventHeaders().get("Hangup-Cause").equals("NORMAL_CLEARING")){
                        callLogPojo.setResultDetail("55");
                        callLogPojo.setResult("10");
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("RECOVERY_ON_TIMER_EXPIRE")||event.getEventHeaders().get("Hangup-Cause").equals("NO_ANSWER")){
                        callLogPojo.setResultDetail("25");
                        callLogPojo.setResult("20");
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("NO_USER_RESPONSE")){
                        callLogPojo.setResultDetail("20");
                        callLogPojo.setResult("20");
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("ORIGINATOR_CANCEL")){
                        callLogPojo.setResultDetail("25");
                        callLogPojo.setResult("20");
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("NORMAL_TEMPORARY_FAILURE")
                            ||event.getEventHeaders().get("Hangup-Cause").equals("INCOMPATIBLE_DESTINATION")
                            ||event.getEventHeaders().get("Hangup-Cause").equals("UNALLOCATED_NUMBER")){
                        callLogPojo.setResultDetail("10");
                        callLogPojo.setResult("20");
                        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_FAIL_REJECY_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber(),0);
                    }else if (event.getEventHeaders().get("Hangup-Cause").equals("USER_BUSY")){
                        callLogPojo.setResultDetail("20");
                        callLogPojo.setResult("20");
                    }else {
                        callLogPojo.setResultDetail("5");
                        callLogPojo.setResult("20");
                        log.info("{}该挂断cause未处理，请补充",event.getEventHeaders().get("Hangup-Cause"));
                    }
                }
                if (callLogPojo.getResult().equals("10")){
                    countSuccess(callLogPojo);
                    baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_SESSION_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber());
                }else {
                    countFail(callLogPojo);
                }
                try {
                    if (callLogPojo.getResult().equals("10")){
                        String recordPath=event.getEventHeaders().get("variable_execute_on_answer");
                        callLogPojo.setRecordFile(recordPath.substring(recordPath.indexOf(" ")+1));
                        File file=new File(callLogPojo.getRecordFile());
                        if (file.exists()){
                            String storeKey = DateTool.formatDate("yyyyMMdd")+"_"+StringTool.MD5Encode(event.getEventHeaders().get("variable_uuid"))+".mp3";
                            storageService.store(FileUtils.openInputStream(file),0L,"audio/mp3",storeKey);
                            callLogPojo.setRecordFileQiniu(storageService.getStorage().getBucketName()+"|"+storeKey);
                            file.delete();
                        }
                    }
                    if (StringUtils.isNotEmpty(callLogPojo.getPriceRule())){
                        String [] priceRule= callLogPojo.getPriceRule().split("\\|");
                        callLogPojo.setPriceCal(Math.ceil(((double)callLogPojo.getBillsec())/Double.valueOf(priceRule[0]))*Double.valueOf(priceRule[1]));
                    }
                }catch (Exception e){
                    log.error("ChannelHangupCompleteEslEventHandler处理异常",e);
                }

                callLogPojo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                callLogPojo.setHangupCase(event.getEventHeaders().get("Hangup-Cause"));

                setEvaluation(callLogPojo);
                this.callLogService.update(callLogPojo);
                return;
            }else if (StringUtils.isNotBlank(event.getEventHeaders().get("variable_preview_call"))){
                return;
            }

            //任务逻辑
            CallLogPojo callLogPojo = new CallLogPojo();
            callLogPojo.setProjectCode(event.getEventHeaders().get("variable_callcenter_project_code"));
            callLogPojo.setCallTaskCode(event.getEventHeaders().get("variable_callcenter_task_code"));
            String telNo=event.getEventHeaders().get("Caller-Destination-Number");
            if (StringUtils.isNotEmpty(telNo)&&telNo.length()>11){
                String phonePrefix = event.getEventHeaders().get("variable_phonePrefix");
                if (phonePrefix==null){
                    phonePrefix="";
                }
                if (telNo.length()>11){
//                    telNo=telNo.substring(telNo.length()-11);
                    telNo=telNo.replaceFirst(phonePrefix,"");
                }
                callLogPojo.setDestinationNumber(telNo);
            }else {
                callLogPojo.setDestinationNumber(telNo);
            }
            callLogPojo=this.callLogService.selectUnique(callLogPojo);
            if (callLogPojo!=null){
                baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+callLogPojo.getCallTaskCode()+"_"+callLogPojo.getGwCode(),callLogPojo.getDestinationNumber());
                baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber());
                resultProc(addr, event, callLogPojo);

                callLogPojo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                setEvaluation(callLogPojo);
                this.callLogService.update(callLogPojo);
                baseRedisDaoDB0.leftPush(ERedisCacheKey.CALLCENTER_CALLLOG_QUEUE.getCode(), callLogPojo);


            }else {//直接转坐席模式下坐席leg挂断后执行
                callLogPojo = new CallLogPojo();
                callLogPojo.setCallTaskCode(event.getEventHeaders().get("variable_sip_h_X-Call-Task-Code"));
                if (StringUtils.isNotEmpty(event.getEventHeaders().get("Other-Leg-Destination-Number"))
                        &&event.getEventHeaders().get("Other-Leg-Destination-Number").length()>11){
                    callLogPojo.setDestinationNumber(event.getEventHeaders().get("Other-Leg-Destination-Number").substring(event.getEventHeaders().get("Other-Leg-Destination-Number").length()-11));
                }else {
                    callLogPojo.setDestinationNumber(event.getEventHeaders().get("Other-Leg-Destination-Number"));
                }
                callLogPojo=this.callLogService.selectUnique(callLogPojo);

                if (callLogPojo!=null){
                    callLogPojo.setCallerIdNumber(event.getEventHeaders().get("Caller-Destination-Number"));
                    callLogPojo.setCallUUID(event.getEventHeaders().get("Other-Leg-Unique-ID"));
                    if (StringUtils.isNotEmpty(event.getEventHeaders().get("variable_fifo_record_template"))){
                        try {
                            callLogPojo.setRecordFile(event.getEventHeaders().get("variable_fifo_record_template"));

                            File file=new File(event.getEventHeaders().get("variable_fifo_record_template"));
//                        String storeKey = event.getEventHeaders().get("variable_fifo_record_template").substring(event.getEventHeaders().get("variable_fifo_record_template").lastIndexOf("/")+1);
                            String storeKey = DateTool.formatDate("yyyyMMdd")+"_"+StringTool.MD5Encode(event.getEventHeaders().get("variable_sip_call_id"))+".mp3";
                            storageService.store(FileUtils.openInputStream(file),0L,"audio/mp3",storeKey);
                            callLogPojo.setRecordFileQiniu(storageService.getStorage().getBucketName()+"|"+storeKey);
                        }catch (Exception e){
                            log.error(e);
                        }
                    }
                    resultProc(addr, event, callLogPojo);
                    callLogPojo.setUpdateTime(new Timestamp(System.currentTimeMillis()));

                    this.callLogService.update(callLogPojo);
                    baseRedisDaoDB0.leftPush(ERedisCacheKey.CALLCENTER_CALLLOG_QUEUE.getCode(), callLogPojo);

                }
            }
        }catch (Exception ex){
            log.error("ChannelHangupCompleteEslEventHandler处理异常{}:{}。。。",event.getEventHeaders().get("variable_callcenter_task_code"),event.getEventHeaders().get("Caller-Destination-Number"),ex);
            log.error("ChannelHangupCompleteEslEventHandler处理异常",ex);
        }
    }

    private void setEvaluation(CallLogPojo callLogPojo) {
        try {
            Integer evaluation = baseRedisDao.get(ERedisCacheKey.CALLCENTER_AGENT_SESSION_EVALUATION.getCode()+ callLogPojo.getDestinationNumber(),Integer.class);
            if (evaluation!=null){
                callLogPojo.setEvaluation(evaluation);
                baseRedisDao.delete(ERedisCacheKey.CALLCENTER_AGENT_SESSION_EVALUATION.getCode()+ callLogPojo.getDestinationNumber());
            }
        }catch (Exception e){
            log.error("ChannelHangupCompleteEslEventHandler处理异常",e);
        }
    }

    private void resultProc(String addr,EslEvent event, CallLogPojo callLogPojo) {
        if (StringTool.isEmpty(event.getEventHeaders().get("variable_proto_specific_hangup_cause"))){
            if (!StringUtils.isEmpty(event.getEventHeaders().get("variable_task_schedule_type"))&&
                    event.getEventHeaders().get("variable_task_schedule_type").equals("playback")){
                callLogPojo.setResultDetail("50");
                callLogPojo.setResult("10");
                baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_SESSION_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber());
                countSuccess(callLogPojo);
            }else {
                if (event.getEventHeaders().get("Hangup-Cause").equals("NORMAL_CLEARING")){
                    if (StringUtils.isEmpty(event.getEventHeaders().get("Other-Leg-Caller-ID-Number"))){
                        callLogPojo.setResult("30");
                        callLogPojo.setResultDetail("35");
                        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_MISS_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber(),0);
                    }else {
                        callLogPojo.setResultDetail("50");
                        callLogPojo.setResult("10");
                        countSuccess(callLogPojo);
                        baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_SESSION_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber());
                    }
                }else if (event.getEventHeaders().get("Hangup-Cause").equals("RECOVERY_ON_TIMER_EXPIRE")){
                    callLogPojo.setResultDetail("5");
                    callLogPojo.setResult("20");
                    countFail(callLogPojo);
                }else if (event.getEventHeaders().get("Hangup-Cause").equals("NO_ANSWER")){
                    callLogPojo.setResultDetail("25");
                    callLogPojo.setResult("20");
                    countFail(callLogPojo);
                }else {
                    callLogPojo.setResultDetail("5");
                    callLogPojo.setResult("20");
                    countFail(callLogPojo);
                    log.info("{}该挂断cause未处理，请补充",event.getEventHeaders().get("Hangup-Cause"));
                }
            }
        }else {
            if (event.getEventHeaders().get("Hangup-Cause").equals("NORMAL_CLEARING")){
                if (!StringUtils.isEmpty(event.getEventHeaders().get("variable_task_schedule_type"))&&
                        event.getEventHeaders().get("variable_task_schedule_type").equals("playback")){
                    callLogPojo.setResultDetail("55");
                    callLogPojo.setResult("10");
                    baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_SESSION_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber());
                    countSuccess(callLogPojo);
                }else {
                    if (StringUtils.isEmpty(event.getEventHeaders().get("variable_fifo_status"))){
                        if (StringTool.isEmpty(event.getEventHeaders().get("variable_fifo_originate_uuid"))){
                            callLogPojo.setResultDetail("35");
                        }else {
                            callLogPojo.setResultDetail("28");
                        }
                        callLogPojo.setResult("30");
                        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_MISS_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber(),0);
                    }else if (event.getEventHeaders().get("variable_fifo_status").equals("ABORTED")){
                        if (StringTool.isEmpty(event.getEventHeaders().get("variable_fifo_originate_uuid"))){
                            callLogPojo.setResultDetail("35");
                        }else {
                            callLogPojo.setResultDetail("28");
                        }
                        callLogPojo.setResult("30");
                        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_MISS_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber(),0);
                    }else if (event.getEventHeaders().get("variable_fifo_status").equals("TIMEOUT")){
                        if (StringTool.isEmpty(event.getEventHeaders().get("variable_fifo_originate_uuid"))){
                            callLogPojo.setResultDetail("40");
                        }else {
                            callLogPojo.setResultDetail("40");
                        }
                        callLogPojo.setResult("30");
                        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_MISS_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber(),0);
                    } else {
                        callLogPojo.setResultDetail("55");
                        callLogPojo.setResult("10");
                        baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_SESSION_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber());
                        countSuccess(callLogPojo);
                    }
                }
            }else if (event.getEventHeaders().get("Hangup-Cause").equals("RECOVERY_ON_TIMER_EXPIRE")||event.getEventHeaders().get("Hangup-Cause").equals("NO_ANSWER")){
                callLogPojo.setResultDetail("25");
                callLogPojo.setResult("20");
                countFail(callLogPojo);
            }else if (event.getEventHeaders().get("Hangup-Cause").equals("NO_USER_RESPONSE")){
                callLogPojo.setResultDetail("20");
                callLogPojo.setResult("20");
                countFail(callLogPojo);
            }else if (event.getEventHeaders().get("Hangup-Cause").equals("ORIGINATOR_CANCEL")){
                callLogPojo.setResultDetail("25");
                callLogPojo.setResult("20");
                countFail(callLogPojo);
            }else if (event.getEventHeaders().get("Hangup-Cause").equals("NORMAL_TEMPORARY_FAILURE")
                    ||event.getEventHeaders().get("Hangup-Cause").equals("INCOMPATIBLE_DESTINATION")
                    ||event.getEventHeaders().get("Hangup-Cause").equals("UNALLOCATED_NUMBER")){
                callLogPojo.setResultDetail("10");
                callLogPojo.setResult("20");
                countFail(callLogPojo);
                baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_FAIL_REJECY_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber(),0);
            }else if (event.getEventHeaders().get("Hangup-Cause").equals("USER_BUSY")){
                callLogPojo.setResultDetail("20");
                callLogPojo.setResult("20");
                countFail(callLogPojo);
            }else {
                callLogPojo.setResultDetail("5");
                callLogPojo.setResult("20");
                countFail(callLogPojo);
                log.info("{}该挂断cause未处理，请补充",event.getEventHeaders().get("Hangup-Cause"));
            }
        }
        callLogPojo.setEndTimestamp(new Timestamp(System.currentTimeMillis()));
        callLogPojo.setDuration(Long.valueOf(event.getEventHeaders().get("variable_duration")));
        callLogPojo.setBillsec(Long.valueOf(event.getEventHeaders().get("variable_billsec")));
        if (callLogPojo.getHangupCase()==null)callLogPojo.setHangupCase(event.getEventHeaders().get("Hangup-Cause"));
        try {

            if (callLogPojo.getResult().equals("10")){
                Long answeredTime = Long.valueOf(event.getEventHeaders().get("Caller-Channel-Answered-Time"));
                Long bridgedTime = Long.valueOf(event.getEventHeaders().get("Caller-Channel-Bridged-Time"));
                if (callLogPojo.getHangupCase()==null) callLogPojo.setHangupCase(new Timestamp(answeredTime/1000)+"|"+new Timestamp(bridgedTime/1000));

                String recordPath=event.getEventHeaders().get("variable_execute_on_answer");
                if (StringUtils.isNotBlank(recordPath)){
                    callLogPojo.setCallerIdNumber(event.getEventHeaders().get("Other-Leg-Caller-ID-Number"));
                    callLogPojo.setCallUUID(event.getEventHeaders().get("variable_uuid"));
                    callLogPojo.setRecordFile(recordPath.substring(recordPath.indexOf(" ")+1));
                    File file=new File(callLogPojo.getRecordFile());
                    if (file.exists()){
                        String storeKey = DateTool.formatDate("yyyyMMdd")+"_"+StringTool.MD5Encode(event.getEventHeaders().get("variable_uuid"))+".mp3";
                        storageService.store(FileUtils.openInputStream(file),0L,"audio/mp3",storeKey);
                        callLogPojo.setRecordFileQiniu(storageService.getStorage().getBucketName()+"|"+storeKey);
                        file.delete();
                    }
                }
            }else if (callLogPojo.getResult().equals("30")){
                CallProjectPojo callProjectPojo = new CallProjectPojo();
                callProjectPojo.setProjectCode(callLogPojo.getProjectCode());
                callProjectPojo = this.callProjectService.selectUnique(callProjectPojo);
                if (callProjectPojo!=null){
                    List<UserinfoPojo> userinfoList = this.callTaskService.getUserinfoPojos(callProjectPojo);
                    AtomicReference<Integer> sessionCount = new AtomicReference<>(0);
                    AtomicReference<Integer> busyCount = new AtomicReference<>(0);
                    AtomicReference<Integer> idleCount = new AtomicReference<>(0);
                    userinfoList.forEach(e->{
                        if (e.getSessionStatus().equals(10)){
                            sessionCount.getAndSet(sessionCount.get() + 1);
                        }else if (e.getWorkStatus().equals(1)){
                            idleCount.getAndSet(idleCount.get() + 1);
                        }else if (e.getWorkStatus().equals(0)){
                            busyCount.getAndSet(busyCount.get() + 1);
                        }
                    });
                    if (callLogPojo.getHangupCase()==null)callLogPojo.setHangupCase(userinfoList.size()+"|"+sessionCount.get()+"|"+idleCount.get()+"|"+busyCount.get());
                    try {
                        EslMessage fifoList = inboundClient.sendSyncApiCommand(addr,"fifo","list");
                        log.info("FreeSWITCH:{}fifoList:{}",addr, EslHelper.formatEslMessage(fifoList));
                    }catch (Exception e){
                        log.error("ChannelHangupCompleteEslEventHandler处理异常",e);
                    }
                }
            }

            if (StringUtils.isNotEmpty(callLogPojo.getPriceRule())){
                String [] priceRule= callLogPojo.getPriceRule().split("\\|");
                callLogPojo.setPriceCal(Math.ceil(((double)callLogPojo.getBillsec())/Double.valueOf(priceRule[0]))*Double.valueOf(priceRule[1]));
            }
        }catch (Exception e){
            log.error("ChannelHangupCompleteEslEventHandler处理异常",e);
        }
    }

    private void countSuccess(CallLogPojo callLogPojo) {
        baseRedisDao.unorderedzrem(ERedisCacheKey.CALLCENTER_TASK_FAIL_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber());
        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_SUCCESS_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber(),0);
    }

    private void countFail(CallLogPojo callLogPojo) {
        if (!baseRedisDao.sismember(ERedisCacheKey.CALLCENTER_TASK_SUCCESS_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber())) {
            baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_FAIL_COUNT.getCode()+callLogPojo.getCallTaskCode(),callLogPojo.getDestinationNumber(),0);
        }
    }
}
