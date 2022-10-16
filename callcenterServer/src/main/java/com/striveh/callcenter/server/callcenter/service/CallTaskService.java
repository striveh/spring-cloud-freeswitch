/** */
package com.striveh.callcenter.server.callcenter.service;

import com.google.common.util.concurrent.RateLimiter;
import com.striveh.callcenter.server.callcenter.dao.CallTaskDao;
import com.striveh.callcenter.server.callcenter.service.iservice.*;
import com.striveh.callcenter.common.constant.param.ERedisCacheKey;
import com.striveh.callcenter.common.database.redis.BaseRedisDao;
import com.striveh.callcenter.common.util.DateTool;
import com.striveh.callcenter.common.util.HttpTool;
import com.striveh.callcenter.common.util.JsonTool;
import com.striveh.callcenter.common.util.StringTool;
import com.striveh.callcenter.feignclient.calllist.ICallListServiceApi;
import com.striveh.callcenter.feignclient.freeswitch.IGatewayServiceApi;
import com.striveh.callcenter.feignclient.freeswitch.IUserInfoServiceApi;
import com.striveh.callcenter.pojo.callcenter.CallLogPojo;
import com.striveh.callcenter.pojo.callcenter.CallProjectPojo;
import com.striveh.callcenter.pojo.callcenter.VoicePojo;
import com.striveh.callcenter.pojo.calllist.CallListPojo;
import com.striveh.callcenter.pojo.freeswitch.GatewayPojo;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import com.striveh.callcenter.server.callcenter.callHandler.CallThreadPoolManager;
import com.striveh.callcenter.server.callcenter.service.iservice.*;
import link.thingscloud.freeswitch.esl.InboundClient;
import link.thingscloud.freeswitch.esl.helper.EslHelper;
import link.thingscloud.freeswitch.esl.transport.message.EslMessage;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.striveh.callcenter.common.base.service.BaseService;
import com.striveh.callcenter.pojo.callcenter.CallTaskPojo;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @功能:【callTask 呼叫任务表】Service
 * @项目名:callcenterServer
 * @作者:xxx
 * @日期:2020-04-06 12:13:48
 * @说明：<pre></pre>
 */
@Service
public class CallTaskService extends BaseService<CallTaskPojo, CallTaskDao> implements ICallTaskService {

    @Autowired
    private ICallListServiceApi callListServiceApi;
    @Autowired
    private InboundClient inboundClient;
    @Autowired
    private ICallLogService callLogService;
    @Autowired
    @Qualifier("baseRedisDaoDef")
    private BaseRedisDao baseRedisDao;
    @Autowired
    @Qualifier("baseRedisDaoDBCache")
    private BaseRedisDao baseRedisDaoDBCache;

    @Autowired
    private IUserInfoServiceApi userInfoServiceApi;
    @Autowired
    private ICallProjectService callProjectService;
    @Value("${common.callTaskNotify.url}")
    private String callTaskNotifyUrl;
    @Value("${common.callExtNumberUpdateState.url}")
    private String callExtNumberUpdateState;
    @Value("${common.callListPullNotify.url}")
    private String callListPullNotify;

    @Value("${common.recordings.path}")
    private String recordingsPath;

    @Autowired
    private SimpMessageSendingOperations msgOperations;

    @Autowired
    private IFreeswitchService freeswitchService;
    @Autowired
    private IVoiceService voiceService;

    @Autowired
    private IGatewayServiceApi gatewayServiceApi;

    List<CallTaskPojo> callTaskPojos = new CopyOnWriteArrayList<>();

    private Map<String,Long> successCountMap = new HashMap<>();
    private Map<String,Long> failCountMap = new HashMap<>();
    private Map<String,Long> missCountMap = new HashMap<>();
    private Map<String,Long> runningCountMap = new HashMap<>();

    /** 限制整体呼叫流量，单位个/秒。默认初始值每秒30，可以通过setCallParams（技术人员验证调试使用）修改大于等于0小于等于50之间 */
    RateLimiter rateLimiter = RateLimiter.create(50);
    /** 当任务空闲坐席数*倍率<=在执行中的数时，让线程sleep时间，单位毫秒。默认初始值2000，可以通过setCallParams（技术人员验证调试使用）修改大于等于0小于等于10000之间 */
    Long sleep = 2000L;
    /** 呼叫等待时间，超过这个时间这结束呼叫，单位秒。默认初始值60，可以通过setCallParams（技术人员验证调试使用）修改大于等于30小于等于60之间 */
    Integer originateTimeout = 60;

    private Map<String,Integer> previewCallGwIndexMap = new ConcurrentHashMap<>();

    @Override
    @Async
    public void getCallList(CallTaskPojo callTask) {
        Integer callListTotal = 0;
        if (StringTool.isEmpty(callTask.getOriginalCallTaskCode())){
            List<CallListPojo> callList=null;
            Integer size=1000;
            Long lastId=0L;
            do {
                callList=this.callListServiceApi.getListByCallListId(Long.valueOf(callTask.getCallTaskCode()),size,lastId);
                if (callList==null||callList.size()==0){
                    break;
                }
                lastId=callList.get(callList.size()-1).getId();
                baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TASK_CALLLIST_LASTID.getCode()+callTask.getCallTaskCode(),lastId);
                callList.forEach(e->baseRedisDaoDBCache.leftPush(ERedisCacheKey.CALLCENTER_TASK_CALLLIST.getCode()+callTask.getCallTaskCode(),e));

                callListTotal = callListTotal+callList.size();
                baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TASK_TOTAL_COUNT.getCode()+callTask.getCallTaskCode(),callListTotal);
            }while (callList!=null&callList.size()>0);
        }else {
            // 获取去重的重呼呼叫数据
            CallLogPojo callLog = new CallLogPojo();
            callLog.setCallTaskCode(callTask.getOriginalCallTaskCode());
            callLog.setPageSize(500);
            callLog.setPageNum(0);
            callLog.setResultDetails(callTask.getOriginalResultDetail().split(","));
            AtomicReference<Long> recallLastId = new AtomicReference<>(0L);
            Set<String> callListSet = new HashSet<>();
            List<CallLogPojo> callLogPojos = null;
            do {
                callLog.setPageNum(callLog.getPageNum()+1);
                callLogPojos = this.callLogService.selectList(callLog);
                callLogPojos.forEach(e->{
                    callListSet.add(e.getDestinationNumber());
                    if (recallLastId.get()<e.getId()){
                        recallLastId.set(e.getId());
                    }
                });
            } while (callLogPojos.size()==500);

            // 获取重呼原任务的成功数据
            Set<String> telNoSuccessSet = new HashSet<>();
            if (callTask.getOriginalResultDetail().contains("50")&&callTask.getOriginalResultDetail().contains("55")){

            }else {
                CallLogPojo callLogSuccess = new CallLogPojo();
                callLogSuccess.setCallTaskCode(callTask.getOriginalCallTaskCode());
                callLogSuccess.setPageSize(500);
                callLogSuccess.setPageNum(0);
                String resultDetails = null;
                if (!callTask.getOriginalResultDetail().contains("50")){
                    resultDetails = "50";
                }else if (!callTask.getOriginalResultDetail().contains("55")){
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
                    CallListPojo callListPojo=new CallListPojo();
                    callListPojo.setPhone(e);
                    baseRedisDaoDBCache.leftPush(ERedisCacheKey.CALLCENTER_TASK_CALLLIST.getCode()+callTask.getCallTaskCode(),callListPojo);
                    CallLogPojo callLogPojo = new CallLogPojo();
                    callLogPojo.setDestinationNumber(e);
                    baseRedisDaoDBCache.leftPush(ERedisCacheKey.CALLCENTER_TASK_CALLLIST_REPEAT.getCode()+callTask.getOriginalCallTaskCode(),callLogPojo);
                }
            });
            baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TASK_TOTAL_COUNT.getCode()+callTask.getCallTaskCode(),baseRedisDaoDBCache.llen(ERedisCacheKey.CALLCENTER_TASK_CALLLIST.getCode()+callTask.getCallTaskCode()));
            try {
                Map<String, String> params = new HashMap<>();
                params.put("callTaskCode", callTask.getCallTaskCode());
                params.put("resultDetail", callTask.getOriginalResultDetail());
                params.put("recallLastId", recallLastId.get().toString());
                Long timestamp=System.currentTimeMillis();
                params.put("sign", StringTool.MD5Encode(timestamp+"TheCALLV1"));
                params.put("timestamp", String.valueOf(timestamp));
                logger.info("通知任务重呼呼叫列表拉取完成请求参数{}", JsonTool.getJsonString(params));
                String result = HttpTool.requestPost(null,callListPullNotify,params);
                logger.info("通知任务重呼呼叫列表拉取完成请求结果{}",result);
            } catch (Exception ex) {
                logger.error("通知任务重呼呼叫列表拉取完成异常",ex);
            }
        }
    }

    @Override
    @Async
    public void additionalCallList(CallTaskPojo callTask) {
        List<CallListPojo> callList=null;
        Integer size=1000;
        Long lastId=baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_CALLLIST_LASTID.getCode()+callTask.getCallTaskCode(),Long.class);
        if (lastId==null){
            lastId=callTask.getCallListLastId();
            if (lastId==null){
                return;
            }
        }
        Integer callListTotal = baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_TOTAL_COUNT.getCode()+callTask.getCallTaskCode(),Integer.class);
        do {
            callList=this.callListServiceApi.getListByCallListId(Long.valueOf(callTask.getCallTaskCode()),size,lastId);
            if (callList==null||callList.size()==0){
                break;
            }
            lastId=callList.get(callList.size()-1).getId();
            baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TASK_CALLLIST_LASTID.getCode()+callTask.getCallTaskCode(),lastId);
            callList.forEach(e->baseRedisDaoDBCache.leftPush(ERedisCacheKey.CALLCENTER_TASK_CALLLIST.getCode()+callTask.getCallTaskCode(),e));

            callListTotal = callListTotal+callList.size();
            baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TASK_TOTAL_COUNT.getCode()+callTask.getCallTaskCode(),callListTotal);
        }while (callList!=null&&callList.size()>0);
    }

    @Override
    @Async
    public void start(CallTaskPojo callTask) {
        baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TASK_STATUS.getCode()+callTask.getCallTaskCode(),2);
        //拨打类型，1预测式拨打2预览式拨打
        if (callTask.getCallType().equals(1)){
            callCustomer(callTask);
        }else {
            //坐席点拨，系统不做自动拨打处理
        }
    }

    private void callCustomer(CallTaskPojo callTask) {
        logger.info("任务{}启动",callTask.getCallTaskCode());
        callTaskPojos.add(callTask);
        CallProjectPojo callProjectPojo = new CallProjectPojo();
        callProjectPojo.setProjectCode(callTask.getProjectCode());
        callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);
        //执行任务
        CallThreadPoolManager callThreadPoolManager = new CallThreadPoolManager(inboundClient);

        String callApp=null;
        //系统调拨类型:1直接转坐席2播放录音后转坐席3先接坐席再拨客户4摘机坐席5播放语音
        if (callTask.getScheduleType().equals(1)||callTask.getScheduleType().equals(4)){
            callApp=" fifo:'"+callTask.getProjectCode()+" in' inline";
        }else if (callTask.getScheduleType().equals(2)){
            VoicePojo voicePojo = new VoicePojo();
            voicePojo.setVoiceCode(callTask.getVoiceCode());
            voicePojo = this.voiceService.selectUnique(voicePojo);
            callApp=" playback:"+voicePojo.getPatch()+",fifo:'"+callTask.getProjectCode()+" in' inline";
        }else if (callTask.getScheduleType().equals(5)){
            VoicePojo voicePojo = new VoicePojo();
            voicePojo.setVoiceCode(callTask.getVoiceCode());
            voicePojo = this.voiceService.selectUnique(voicePojo);
            callApp=" playback:"+voicePojo.getPatch()+" inline";
        }else {
            logger.info("任务{}运行类型未定义：{}",callTask.getCallTaskCode(),callTask.toString());
            return;
        }
        if (callTask.getScheduleType().equals(1)){
            //签入坐席
            List<UserinfoPojo> userinfoPojos = getUserinfoPojos(callProjectPojo);
            CallProjectPojo finalCallProjectPojo = callProjectPojo;
            userinfoPojos.forEach(e->{
                if (e.getStatus()==1&&e.getWorkStatus()==1){
                    //加入到呼叫队列坐席列表
                    EslMessage eslMessage =inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(e.getFreeswitchId()),
                            "fifo_member add", callTask.getProjectCode()+" {fifo_record_template=$${recordings_dir}/${strftime(%Y-%m-%d-%H-%M-%S)}."+e.getUsername()+".mp3," +
                                    "sip_h_X-Call-Task-Code="+callTask.getCallTaskCode()+"}user/"+e.getUsername()+" 1 5 0");

                    logger.info("{}加入到呼叫队列{}坐席列表结果{}",e.getUsername(),finalCallProjectPojo.getProjectCode(), EslHelper.formatEslMessage(eslMessage));
                }
            });
        }
        Integer gwIndex=0;
        do {
            List<GatewayPojo> callGW= JsonTool.getList(baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_GWS.getCode()+callTask.getCallTaskCode(),String.class), GatewayPojo.class);
            //执行中的呼叫
            Integer runningCount = Math.toIntExact(baseRedisDao.unorderedZcard(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode() + callTask.getCallTaskCode()));
            //可以通话的坐席
            Integer agentCount = getAgentIdleCnt(callProjectPojo,callTask);
            //倍率
            Double rate = baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_RATE.getCode()+callTask.getCallTaskCode(),Double.class);
            Long callListNum=baseRedisDaoDBCache.llen(ERedisCacheKey.CALLCENTER_TASK_CALLLIST.getCode()+callTask.getCallTaskCode());
            while ((Math.floor(agentCount*rate)<=runningCount)&&callListNum>0){
                if (2!=baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_STATUS.getCode()+callTask.getCallTaskCode(),Integer.class)){
                    return;
                }
                try {
                    logger.info("任务{}已达最大并发执行数{}等待{}毫秒,agentCount:{},rate:{}",callTask.getCallTaskCode(),runningCount,sleep,agentCount,rate);
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    logger.info(e);
                }
                runningCount = Math.toIntExact(baseRedisDao.unorderedZcard(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode() + callTask.getCallTaskCode()));
                agentCount =  getAgentIdleCnt(callProjectPojo,callTask);
                rate = baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_RATE.getCode()+callTask.getCallTaskCode(),Double.class);
            }
            logger.info("任务{}本次执行{}个agentCount:{},rate:{},running:{}",callTask.getCallTaskCode(),(Math.floor(agentCount*rate)-runningCount),agentCount,rate,runningCount);
            if (callListNum > 0) {
                Long startTime = System.currentTimeMillis();
                for (int i=0;i<(Math.floor(agentCount*rate)-runningCount);i++){

                    if (2!=baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_STATUS.getCode()+callTask.getCallTaskCode(),Integer.class)){
                        return;
                    }
                    try {
                        Integer pause= baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_PAUSE.getCode()+callTask.getCallTaskCode(),Integer.class);
                        if (pause !=null ){
                            logger.info("任务{}暂停一会",callTask.getCallTaskCode());
                            Thread.sleep(pause*1000);
                            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_PAUSE.getCode()+callTask.getCallTaskCode());
                        }
                    } catch (InterruptedException e) {
                        logger.info(e);
                    }

                    CallListPojo callListPojo=baseRedisDaoDBCache.rightPop(ERedisCacheKey.CALLCENTER_TASK_CALLLIST.getCode()+callTask.getCallTaskCode(),CallListPojo.class);

                    if (callListPojo==null){
                        logger.info("任务{}callListPojo=null",callTask.getCallTaskCode());
                        clearCallTask(callTask, callProjectPojo);
                        return;
                    }
                    String callUrl=null;
                    String telNo=callListPojo.getPhone();
                    try{
                        if (gwIndex>(callGW.size()-1)){
                            gwIndex=0;
                        }
                        GatewayPojo gatewayPojo=callGW.get(gwIndex);

                        if (telNo.length()>20){
                            throw new IllegalArgumentException("号码格式错误");
                        }
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
                            EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
                                    "sofia_contact",gatewayPojo.getUsername());
                            logger.info("网关用户 sofia_contact {},结果{}",gatewayPojo.getUsername(),EslHelper.formatEslMessage(eslMessage));
                            String gwurl=eslMessage.getBodyLines().get(0);
                            callUrl="sofia/internal/"+callTelNo+gwurl.substring(gwurl.lastIndexOf("@"));
                        }
                        if (gatewayPojo.getCodec()!=null&&gatewayPojo.getCodec().equals("G711")){
                            gatewayPojo.setCodec(null);//使用默认编码
                        }
                        String callStr="{originate_timeout="+originateTimeout+",ignore_early_media=true,sip_sticky_contact=true,fifo_music=local_stream://moh"+
                                (StringUtils.isNotEmpty(gatewayPojo.getOriginationCallId())? ",origination_caller_id_number="+gatewayPojo.getOriginationCallId():"")+
                                (StringUtils.isNotEmpty(gatewayPojo.getCodec())? ",absolute_codec_string="+gatewayPojo.getCodec():"")+
                                (callTask.getScheduleType().equals(5)?",task_schedule_type=playback":"")+
                                ",execute_on_answer='record_session "+recordingsPath+ DateTool.formatDate("yyyyMMddHHmmss") +telNo+".mp3'"+
                                ",callcenter_project_code="+callTask.getProjectCode()+",callcenter_task_code="+callTask.getCallTaskCode()
                                +",phonePrefix="+phonePrefix+"}"+callUrl+callApp;

                        //记录执行中的呼叫
                        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+callTask.getCallTaskCode(),telNo,0);
                        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+callTask.getCallTaskCode()+"_"+gatewayPojo.getGwCode(),telNo,0);

                        //切换到下一个网关
                        if (gwIndex==(callGW.size()-1)){
                            gwIndex=0;
                        }else {
                            gwIndex++;
                        }
                        //记录calllog
                        CallLogPojo callLogPojo=new CallLogPojo();
                        callLogPojo.setAddTime(new Timestamp(System.currentTimeMillis()));
                        callLogPojo.setCallTaskCode(callTask.getCallTaskCode());
                        callLogPojo.setDestinationNumber(telNo);
                        callLogPojo.setProjectCode(callTask.getProjectCode());
                        callLogPojo.setGwCode(gatewayPojo.getGwCode());
                        if (gatewayPojo.getBillType()!=null&&gatewayPojo.getBillType()==2){
                            callLogPojo.setPriceRule(gatewayPojo.getBillsec()+"|"+gatewayPojo.getPrice());
                        }
                        this.callLogService.insert(callLogPojo);

                        rateLimiter.acquire();
                        logger.info("执行了{},{}",callStr,inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),"originate",callStr));

                    }catch (Exception ex){
                        logger.info("telNo外呼发生异常",ex);
                        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_FAIL_COUNT.getCode()+callTask.getCallTaskCode(),telNo,0);
                        //记录calllog
                        CallLogPojo callLogPojo=new CallLogPojo();
                        callLogPojo.setAddTime(new Timestamp(System.currentTimeMillis()));
                        callLogPojo.setCallTaskCode(callTask.getCallTaskCode());
                        callLogPojo.setDestinationNumber(telNo);
                        callLogPojo.setProjectCode(callTask.getProjectCode());
                        callLogPojo.setResultDetail("10");
                        callLogPojo.setResult("20");
                        callLogPojo.setHangupCase("CALLCENTER_ERROR");
                        callLogPojo.setEndTimestamp(new Timestamp(System.currentTimeMillis()));
                        callLogPojo.setStartTimestamp(new Timestamp(System.currentTimeMillis()));
                        callLogPojo.setCallUUID(StringTool.getSerno());

                        this.callLogService.insert(callLogPojo);
                    }
                }
                Long endTime = System.currentTimeMillis();
                logger.info("任务{}本次执行{}个耗时{}毫秒",callTask.getCallTaskCode(),(Math.floor(agentCount*rate)-runningCount),endTime-startTime);
            }else {
                logger.info("任务{}callListNum=0",callTask.getCallTaskCode());
                clearCallTask(callTask, callProjectPojo);
                return;
            }
        } while (2==baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_STATUS.getCode()+callTask.getCallTaskCode(),Integer.class));

    }

    /**
     * 针对语音播放结束挂断的任务，按设置的线路并发来执行
     * @param callProjectPojo
     * @param callTask
     * @param callApp
     */
    private void callTaskNoAgentByGatewayConcurrency(CallProjectPojo callProjectPojo,CallTaskPojo callTask,String callApp){
        Integer gwIndex=0;
        do {
            List<GatewayPojo> callGW= JsonTool.getList(baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_GWS.getCode()+callTask.getCallTaskCode(),String.class), GatewayPojo.class);
            if (gwIndex>(callGW.size()-1)){
                gwIndex=0;
            }
            gwIndex = getGwIndex(callTask, gwIndex, callGW);
            GatewayPojo gatewayPojo = callGW.get(gwIndex);
            Long callListNum=baseRedisDaoDBCache.llen(ERedisCacheKey.CALLCENTER_TASK_CALLLIST.getCode()+callTask.getCallTaskCode());

            Integer useCount = baseRedisDao.unorderedZcard(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+ callTask.getCallTaskCode()+"_"+ gatewayPojo.getGwCode()).intValue();
            Integer running = gatewayPojo.getConcurrency()-useCount;
            logger.info("任务{}本次执行{}个,gwIndex:{}",callTask.getCallTaskCode(),running,gwIndex);
            if (callListNum > 0) {
                Long startTime = System.currentTimeMillis();
                for (int i=0;i<running;i++){

                    if (2!=baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_STATUS.getCode()+callTask.getCallTaskCode(),Integer.class)){
                        return;
                    }
                    try {
                        Integer pause= baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_PAUSE.getCode()+callTask.getCallTaskCode(),Integer.class);
                        if (pause !=null ){
                            logger.info("任务{}暂停一会",callTask.getCallTaskCode());
                            Thread.sleep(pause*1000);
                            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_PAUSE.getCode()+callTask.getCallTaskCode());
                        }
                    } catch (InterruptedException e) {
                        logger.info(e);
                    }

                    CallListPojo callListPojo=baseRedisDaoDBCache.rightPop(ERedisCacheKey.CALLCENTER_TASK_CALLLIST.getCode()+callTask.getCallTaskCode(),CallListPojo.class);

                    if (callListPojo==null){
                        logger.info("任务{}callListPojo=null",callTask.getCallTaskCode());
                        clearCallTask(callTask, callProjectPojo);
                        return;
                    }
                    String callUrl=null;
                    String telNo=callListPojo.getPhone();
                    try{
                        if (telNo.length()>11){
                            throw new IllegalArgumentException("手机号格式错误");
                        }
                        String callTelNo=telNo;
                        if (StringTool.isPhoneNumber(telNo)&&StringUtils.isNotEmpty(gatewayPojo.getMobilePhonePrefix())){
                            callTelNo=gatewayPojo.getMobilePhonePrefix()+telNo;
                        }else if (!StringTool.isPhoneNumber(telNo)&&StringUtils.isNotEmpty(gatewayPojo.getFixedPhonePrefix())){
                            callTelNo=gatewayPojo.getFixedPhonePrefix()+telNo;
                        }
                        if (gatewayPojo.getRegisterType()==1){
                            callUrl="sofia/gateway/"+gatewayPojo.getGwCode()+"/"+callTelNo;
                        }else {
                            EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),
                                    "sofia_contact",gatewayPojo.getUsername());
                            logger.info("网关用户 sofia_contact {},结果{}",gatewayPojo.getUsername(),EslHelper.formatEslMessage(eslMessage));
                            String gwurl=eslMessage.getBodyLines().get(0);
                            callUrl="sofia/internal/"+callTelNo+gwurl.substring(gwurl.lastIndexOf("@"));
                        }
                        if (gatewayPojo.getCodec()!=null&&gatewayPojo.getCodec().equals("G711")){
                            gatewayPojo.setCodec(null);//使用默认编码
                        }
                        String callStr="{originate_timeout="+originateTimeout+",ignore_early_media=true,sip_sticky_contact=true,fifo_music=local_stream://moh"+
                                (StringUtils.isNotEmpty(gatewayPojo.getOriginationCallId())? ",origination_caller_id_number="+gatewayPojo.getOriginationCallId():"")+
                                (StringUtils.isNotEmpty(gatewayPojo.getCodec())? ",absolute_codec_string="+gatewayPojo.getCodec():"")+
                                (callTask.getScheduleType().equals(4)?",execute_on_answer='record_session "+recordingsPath+ DateTool.formatDate("yyyyMMddHHmmss") +telNo+".mp3'":"")+
                                ",callcenter_project_code="+callTask.getProjectCode()+",callcenter_task_code="+callTask.getCallTaskCode()+"}"+callUrl+callApp;

                        //记录执行中的呼叫
                        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+callTask.getCallTaskCode(),telNo,0);
                        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+callTask.getCallTaskCode()+"_"+gatewayPojo.getGwCode(),telNo,0);

                        //记录calllog
                        CallLogPojo callLogPojo=new CallLogPojo();
                        callLogPojo.setAddTime(new Timestamp(System.currentTimeMillis()));
                        callLogPojo.setCallTaskCode(callTask.getCallTaskCode());
                        callLogPojo.setDestinationNumber(telNo);
                        callLogPojo.setProjectCode(callTask.getProjectCode());
                        callLogPojo.setGwCode(gatewayPojo.getGwCode());
                        if (gatewayPojo.getBillType()!=null&&gatewayPojo.getBillType()==2){
                            callLogPojo.setPriceRule(gatewayPojo.getBillsec()+"|"+gatewayPojo.getPrice());
                        }
                        this.callLogService.insert(callLogPojo);

                        rateLimiter.acquire();
                        logger.info("执行了{},{}",callStr,inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(callProjectPojo.getFreeswitchId()),"originate",callStr));

                    }catch (Exception ex){
                        logger.info("telNo外呼发生异常",ex);
                        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_FAIL_COUNT.getCode()+callTask.getCallTaskCode(),telNo,0);
                        //记录calllog
                        CallLogPojo callLogPojo=new CallLogPojo();
                        callLogPojo.setAddTime(new Timestamp(System.currentTimeMillis()));
                        callLogPojo.setCallTaskCode(callTask.getCallTaskCode());
                        callLogPojo.setDestinationNumber(telNo);
                        callLogPojo.setProjectCode(callTask.getProjectCode());
                        callLogPojo.setResultDetail("10");
                        callLogPojo.setResult("20");
                        callLogPojo.setHangupCase("CALLCENTER_ERROR");
                        callLogPojo.setEndTimestamp(new Timestamp(System.currentTimeMillis()));
                        callLogPojo.setStartTimestamp(new Timestamp(System.currentTimeMillis()));
                        callLogPojo.setCallUUID(StringTool.getSerno());

                        this.callLogService.insert(callLogPojo);
                    }
                }
                Long endTime = System.currentTimeMillis();
                logger.info("任务{}本次执行{}个耗时{}毫秒",callTask.getCallTaskCode(),running,endTime-startTime);
            }else {
                logger.info("任务{}callListNum=0",callTask.getCallTaskCode());
                clearCallTask(callTask, callProjectPojo);
                return;
            }
            //切换到下一个网关
            if (gwIndex==(callGW.size()-1)){
                gwIndex=0;
            }else {
                gwIndex++;
            }
        } while (2==baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_STATUS.getCode()+callTask.getCallTaskCode(),Integer.class));

    }

    private Integer getGwIndex(CallTaskPojo callTask, Integer gwIndex, List<GatewayPojo> callGW) {
        GatewayPojo gatewayPojo = callGW.get(gwIndex);
        Integer useCount = baseRedisDao.unorderedZcard(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+ callTask.getCallTaskCode()+"_"+ gatewayPojo.getGwCode()).intValue();
        if ((gatewayPojo.getConcurrency()-useCount)<=0){
            //切换到下一个网关
            if (gwIndex ==(callGW.size()-1)){
                gwIndex =0;
                try {
                    logger.info("任务{}已达最大并发执行数等待{}毫秒", callTask.getCallTaskCode(),sleep);
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    logger.info(e);
                }
                return getGwIndex(callTask,gwIndex,callGW);
            }else {
                gwIndex++;
                return getGwIndex(callTask,gwIndex,callGW);
            }
        }
        return gwIndex;
    }

    private void clearCallTask(CallTaskPojo callTask, CallProjectPojo callProjectPojo) {
        if (callTask.getExpiredTime().getTime()>System.currentTimeMillis()){
            Integer runningCount = Math.toIntExact(baseRedisDao.unorderedZcard(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode() + callTask.getCallTaskCode()));
            Integer sessionCount = getAgentSessionCnt(callTask);
            while (0!=runningCount||sessionCount>0){
                try {
                    logger.info("任务{}还有{}个在执行,或者还有{}个在通话。。。",callTask.getCallTaskCode(),runningCount,sessionCount);
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logger.info(e);
                }
                runningCount = Math.toIntExact(baseRedisDao.unorderedZcard(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode() + callTask.getCallTaskCode()));
                sessionCount = getAgentSessionCnt(callTask);
            }
        }

        if (callTask.getScheduleType().equals(1)){
            this.userInfoServiceApi.getListByProjectId(callProjectPojo.getId()).forEach(e->{
                String param=callTask.getProjectCode()+" {fifo_record_template=$${recordings_dir}/${strftime(%Y-%m-%d-%H-%M-%S)}."+e.getUsername()+".mp3," +
                        "sip_h_X-Call-Task-Code="+callTask.getCallTaskCode()+"}user/"+e.getUsername();
                EslMessage eslMessage =inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(e.getFreeswitchId()),"fifo_member del", param );
                logger.info("{}从呼叫队列移除{}结果{}",e.getUsername(),param, EslHelper.formatEslMessage(eslMessage));
            });
        }

        callTask.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        callTask.setMissCount(null);
        callTask.setTotalCount(null);
        callTask.setSuccessCount(null);
        callTask.setFailCount(null);
        callTask.setRate(null);
        callTask.setCallGWs(null);

        callTask.setStatus(4);
        this.update(callTask);
        CallListPojo callListPojo=baseRedisDaoDBCache.rightPop(ERedisCacheKey.CALLCENTER_TASK_CALLLIST.getCode()+callTask.getCallTaskCode(),CallListPojo.class);
        int count =0;
        while (callListPojo!=null){
            //记录calllog
            CallLogPojo callLogPojo=new CallLogPojo();
            callLogPojo.setAddTime(new Timestamp(System.currentTimeMillis()));
            callLogPojo.setCallTaskCode(callTask.getCallTaskCode());
            callLogPojo.setDestinationNumber(callListPojo.getPhone());
            callLogPojo.setProjectCode(callTask.getProjectCode());
            callLogPojo.setResultDetail("99999");
            callLogPojo.setHangupCase("NO_CALL");
            this.callLogService.insert(callLogPojo);
            count++;
            if (count%100==0){
                notifyCount(callTask,"50");
            }
            callListPojo=baseRedisDaoDBCache.rightPop(ERedisCacheKey.CALLCENTER_TASK_CALLLIST.getCode()+callTask.getCallTaskCode(),CallListPojo.class);
        }

        if (null!=baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_RATE.getCode()+callTask.getCallTaskCode(),Double.class)){
            notifyCount(callTask,"100");
            //清理缓存的任务key
            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_STATUS.getCode() + callTask.getCallTaskCode());
            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_RATE.getCode() + callTask.getCallTaskCode());
            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_GWS.getCode() + callTask.getCallTaskCode());
            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_CALLLIST_LASTID.getCode() + callTask.getCallTaskCode());
            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_SUCCESS_COUNT.getCode() + callTask.getCallTaskCode());
            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_FAIL_COUNT.getCode() + callTask.getCallTaskCode());
            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_FAIL_REJECY_COUNT.getCode() + callTask.getCallTaskCode());
            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_MISS_COUNT.getCode() + callTask.getCallTaskCode());
            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_TOTAL_COUNT.getCode() + callTask.getCallTaskCode());
            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode() + callTask.getCallTaskCode());
            baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_SESSION_COUNT.getCode() + callTask.getCallTaskCode());

            msgOperations.convertAndSend("/topic/task/" + callTask.getProjectCode(), callTask);

            callTaskPojos.remove(callTask);

        }

        logger.info("任务{}结束完成",callTask.getCallTaskCode());
    }


    @Override
    public void pause(CallTaskPojo callTask) {
        baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TASK_STATUS.getCode()+callTask.getCallTaskCode(),3);
        logger.info("任务{}暂停",callTask.getCallTaskCode());
    }

    @Override
    @Async
    public void end(CallTaskPojo callTask) {
        CallProjectPojo callProjectPojo= new CallProjectPojo();
        callProjectPojo.setProjectCode(callTask.getProjectCode());
        callTask.setStatus(4);
        clearCallTask(callTask,this.callProjectService.selectUnique(callProjectPojo));
    }

    @Override
    public void updateCacheAfterPorjectEnd(CallTaskPojo callTask) {
        //清理缓存的任务key
        baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_CALLLIST_LASTID.getCode() + callTask.getCallTaskCode());
        baseRedisDao.delete(ERedisCacheKey.CALLCENTER_TASK_TOTAL_COUNT.getCode() + callTask.getCallTaskCode());
        baseRedisDaoDBCache.delete(ERedisCacheKey.CALLCENTER_TASK_CALLLIST.getCode()+callTask.getCallTaskCode());

        callTask.setStatus(4);
        callTask.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        this.update(callTask);
    }

    @Async
    @Override
    public void notifyCount(CallTaskPojo e,String uncallListSyncStatus) {
        Map<String, String> params = new HashMap<>();
        params.put("code", e.getCallTaskCode());
        Integer totalCount = baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_TOTAL_COUNT.getCode()+e.getCallTaskCode(),Integer.class);
        if (totalCount==null){
            return;
        }
        params.put("totalCnt", String.valueOf(totalCount));
        Long failCount = baseRedisDao.unorderedZcard(ERedisCacheKey.CALLCENTER_TASK_FAIL_COUNT.getCode()+e.getCallTaskCode());
        params.put("failCnt", String.valueOf(failCount));
        Long rejectCnt = baseRedisDao.unorderedZcard(ERedisCacheKey.CALLCENTER_TASK_FAIL_REJECY_COUNT.getCode()+e.getCallTaskCode());
        params.put("rejectCnt", String.valueOf(rejectCnt));
        Long missCount = baseRedisDao.unorderedZcard(ERedisCacheKey.CALLCENTER_TASK_MISS_COUNT.getCode()+e.getCallTaskCode());
        params.put("missCnt", String.valueOf(missCount));
        Long successCount = baseRedisDao.unorderedZcard(ERedisCacheKey.CALLCENTER_TASK_SUCCESS_COUNT.getCode()+e.getCallTaskCode());
        params.put("successCnt", String.valueOf(successCount));
        params.put("uncallListSyncStatus", uncallListSyncStatus);
        Long runningCount = baseRedisDao.unorderedZcard(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode() + e.getCallTaskCode());
        Integer sessionCount = getAgentSessionCnt(e);
        params.put("runningCnt", String.valueOf(runningCount+sessionCount));

        CallTaskPojo callTaskPojo = new CallTaskPojo();
        callTaskPojo.setId(e.getId());
        callTaskPojo.setMissCount(missCount.intValue());
        callTaskPojo.setTotalCount(totalCount);
        callTaskPojo.setSuccessCount(successCount.intValue());
        callTaskPojo.setFailCount(failCount.intValue());

        if (e.getExpiredTime().getTime()<System.currentTimeMillis()&&e.getStatus()<4){
            logger.info("任务{}过期结束",e.getCallTaskCode());
            if (e.getStatus()>1){
                end(e);
            } else {
                updateCacheAfterPorjectEnd(e);
            }
        }else{
            this.update(callTaskPojo);
            if (e.getStatus().equals(1)){
                if ((e.getUpdateTime()==null&&(DateTool.getTimestamp().getTime()-e.getAddTime().getTime())>5*60*1000)){
                    return;
                }else if ((e.getUpdateTime()!=null&&(DateTool.getTimestamp().getTime()-e.getUpdateTime().getTime())>5*60*1000)){
                    return;
                }
            }

            if (!e.getStatus().equals(1)){
                if (successCount.equals(successCountMap.get(e.getCallTaskCode()))
                        &&failCount.equals(failCountMap.get(e.getCallTaskCode()))
                        &&missCount.equals(missCountMap.get(e.getCallTaskCode()))
                        &&runningCount.equals(runningCountMap.get(e.getCallTaskCode()))
                        &&uncallListSyncStatus.equals("0")){
                    return;
                }
            }

            successCountMap.put(e.getCallTaskCode(),successCount);
            failCountMap.put(e.getCallTaskCode(),failCount);
            missCountMap.put(e.getCallTaskCode(),missCount);
            runningCountMap.put(e.getCallTaskCode(),runningCount);

            params.put("status",e.getStatus().toString());
            if(e.getRate()!=null){
                params.put("rate",e.getRate().toString());
                msgOperations.convertAndSend("/topic/callTaskStatus/" + e.getProjectCode(), params);
            }
            Long timestamp=System.currentTimeMillis();
            params.put("sign", StringTool.MD5Encode(timestamp+"TheCALLV1"));
            params.put("timestamp", String.valueOf(timestamp));
            logger.info("通知任务执行进度请求参数{}", JsonTool.getJsonString(params));
            try {
                String result = HttpTool.requestPost(null,callTaskNotifyUrl,params);
                logger.info("通知任务执行进度请求结果{}",result);
            } catch (Exception ex) {
                logger.error("通知任务执行进度异常",ex);
            }
        }

    }

    @Override
    public void notifyAgentStatus(UserinfoPojo userinfoPojo) {
        Map<String, String> params = new HashMap<>();
        params.put("username",userinfoPojo.getUsername());
        params.put("workStatus",userinfoPojo.getWorkStatus().toString());
        params.put("sessionStatus",userinfoPojo.getSessionStatus().toString());
        params.put("status",userinfoPojo.getStatus().toString());

        this.callProjectService.callBack(params,callExtNumberUpdateState,"通知坐席状态");
    }

    @Override
    public void callTimeout(CallTaskPojo e) {
        CallProjectPojo callProjectPojo = new CallProjectPojo();
        callProjectPojo.setProjectCode(e.getProjectCode());
        callProjectPojo = this.callProjectService.selectUnique(callProjectPojo);
        CallProjectPojo finalCallProjectPojo = callProjectPojo;

        CallLogPojo callLogPojo = new CallLogPojo();
        callLogPojo.setCallTaskCode(e.getCallTaskCode());
        //设置超过半个小时算异常电话
        callLogPojo.setStartTimestamp(new Timestamp(System.currentTimeMillis()-30*60*1000));
        this.callLogService.selectListByCalling(callLogPojo).forEach(callLogPojo1 -> {
            EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(finalCallProjectPojo.getFreeswitchId()),"uuid_kill",callLogPojo1.getCallUUID());
            logger.info("任务{}号码{}呼叫超时{},结果{}",callLogPojo1.getCallTaskCode(),callLogPojo1.getDestinationNumber(),callLogPojo1.getCallUUID(),EslHelper.formatEslMessage(eslMessage));
        });
    }

    @Override
    public void setCallParams(Integer rateLimit, Long sleep, Integer originateTimeout) {
        if (rateLimit!=null&&rateLimit>=20&&rateLimit<=50){
            logger.info("设置rateLimit={}",rateLimit);
            this.rateLimiter.setRate(rateLimit.doubleValue());
        }
        if (sleep!=null&&sleep>=0&&sleep<=10000){
            this.sleep = sleep;
            logger.info("设置sleep={}",sleep);
        }
        if (originateTimeout!=null&&originateTimeout>=30&&originateTimeout<=60){
            this.originateTimeout = originateTimeout;
            logger.info("设置originateTimeout={}",originateTimeout);
        }
    }

    @Override
    public List<UserinfoPojo> getUserinfoPojos(CallProjectPojo callProjectPojo) {
        List<UserinfoPojo> userinfoList;
        userinfoList=this.userInfoServiceApi.getListByProjectId(callProjectPojo.getId());
        userinfoList.forEach(e->{
            EslMessage eslMessage =inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(e.getFreeswitchId()),
                    "sofia_contact",e.getUsername());
            if (eslMessage.getBodyLines().get(0).contains(e.getUsername())){
                e.setStatus(1);
            }else {
                e.setStatus(0);
            }

        });
        return userinfoList;
    }

    @Override
    @Async
    public void reloadGateway(GatewayPojo gatewayPojo) {
        inboundClient.option().serverOptions().forEach(e->{
            EslMessage eslMessageDel=inboundClient.sendSyncApiCommand(e.addr(), "sofia profile external killgw",gatewayPojo.getGwCode());
            EslMessage eslMessage=inboundClient.sendSyncApiCommand(e.addr(), "sofia profile external rescan",null);
            logger.info("FS{}重新加载网关{}结果{}",e.addr(),gatewayPojo.getGwName() , EslHelper.formatEslMessage(eslMessage));
        });
    }

    @Override
    public List<CallTaskPojo> getProcList(CallTaskPojo callTaskPojo) {
        return this.dao.selectProcList(callTaskPojo);
    }

    @Override
    public CallTaskPojo getProc(CallTaskPojo callTaskPojo) {
        return this.dao.selectProc(callTaskPojo);
    }

    @Async
    @Override
    public void previewCall(String jsonStr){
        Map<String,String> map=JsonTool.getObj(jsonStr,Map.class);
        String username=map.get("username");
        String telNo = map.get("telNo").trim();
        String callTaskCode = map.get("callTaskCode");
        CallTaskPojo callTask = new CallTaskPojo();
        callTask.setCallTaskCode(callTaskCode);
        callTask = selectUnique(callTask);
        CallProjectPojo callProjectPojo = new CallProjectPojo();
        callProjectPojo.setProjectCode(callTask.getProjectCode());
        callProjectPojo=this.callProjectService.selectUnique(callProjectPojo);

        Map<String,String> notify = new HashMap<>();
        notify.put("type","message");
        notify.put("duration","5000");
        if (callTask.getStatus().equals(4)){
            notify.put("data","任务结束");
            msgOperations.convertAndSend("/topic/notify/" + callTask.getProjectCode()+"/"+username, JsonTool.getFormatJsonString(notify));
            return;
        }else if (callTask.getStatus().equals(3)){
            notify.put("data","任务暂停");
            msgOperations.convertAndSend("/topic/notify/" + callTask.getProjectCode()+"/"+username, JsonTool.getFormatJsonString(notify));
            return;
        }

        UserinfoPojo userinfoPojo = this.userInfoServiceApi.getByUsername(username);
        List<GatewayPojo> callGW= JsonTool.getList(baseRedisDao.get(ERedisCacheKey.CALLCENTER_TASK_GWS.getCode()+callTask.getCallTaskCode(),String.class), GatewayPojo.class);
        Integer gwIndex = this.previewCallGwIndexMap.get(callTaskCode);
        if (gwIndex == null){
            gwIndex = 0;
            this.previewCallGwIndexMap.put(callTaskCode,gwIndex);
        }
        if (gwIndex>(callGW.size()-1)){
            gwIndex=0;
            this.previewCallGwIndexMap.put(callTaskCode,gwIndex);
        }
        GatewayPojo gatewayPojo = callGW.get(gwIndex);

        //切换到下一个网关
        if (gwIndex==(callGW.size()-1)){
            gwIndex=0;
            this.previewCallGwIndexMap.put(callTaskCode,gwIndex);
        }else {
            gwIndex++;
            this.previewCallGwIndexMap.put(callTaskCode,gwIndex);
        }

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
            EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
                    "sofia_contact",gatewayPojo.getUsername());
            logger.info("网关用户 sofia_contact {},结果{}",gatewayPojo.getUsername(),EslHelper.formatEslMessage(eslMessage));
            String gwurl=eslMessage.getBodyLines().get(0);
            callUrl="sofia/internal/"+callTelNo+gwurl.substring(gwurl.lastIndexOf("@"));
        }
        EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
                "create_uuid",null);
        logger.info("坐席点拨（预览式拨打）{}create_uuid结果{}",username,EslHelper.formatEslMessage(eslMessage));

        if (gatewayPojo.getCodec()!=null&&gatewayPojo.getCodec().equals("G711")){
            gatewayPojo.setCodec(null);//使用默认编码
        }
        String callStr="{preview_call=true,callcenter_project_code="+callTask.getProjectCode()+",callcenter_task_code="+callTask.getCallTaskCode()+",phonePrefix="+phonePrefix+"}user/"+username+
                " &bridge({sip_sticky_contact=true,preview_call=true,origination_uuid="+eslMessage.getBodyLines().get(0)+
                (StringUtils.isNotEmpty(gatewayPojo.getOriginationCallId())? ",origination_caller_id_number="+gatewayPojo.getOriginationCallId():"")+
                (StringUtils.isNotEmpty(gatewayPojo.getCodec())? ",absolute_codec_string="+gatewayPojo.getCodec():"")+
                ",execute_on_answer='record_session "+recordingsPath+ DateTool.formatDate("yyyyMMddHHmmss") +telNo+".mp3'"+
                ",callcenter_project_code="+callTask.getProjectCode()+",callcenter_task_code="+callTask.getCallTaskCode()+",phonePrefix="+phonePrefix+
                "}"+callUrl+")";

        //记录执行中的呼叫
        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+callTask.getCallTaskCode(),telNo,0);
        baseRedisDao.unorderedzadd(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+callTask.getCallTaskCode()+"_"+gatewayPojo.getGwCode(),telNo,0);

        Map<String,String> body=new HashMap<>();
        body.put("telNo",telNo);
        body.put("callId", eslMessage.getBodyLines().get(0));
        //记录calllog
        CallLogPojo newCallLogPojo=new CallLogPojo();
        newCallLogPojo.setDestinationNumber(telNo);
        newCallLogPojo.setCallTaskCode(callTaskCode);
        newCallLogPojo = this.callLogService.selectUnique(newCallLogPojo);
        if (newCallLogPojo!=null&&(StringTool.isEmpty(newCallLogPojo.getHangupCase())||newCallLogPojo.getHangupCase().equals("NO_CALL_DISTRIBUTION"))){
            newCallLogPojo.setGwCode(gatewayPojo.getGwCode());
            if (gatewayPojo.getBillType()!=null&&gatewayPojo.getBillType()==2){
                newCallLogPojo.setPriceRule(gatewayPojo.getBillsec()+"|"+gatewayPojo.getPrice());
            }
            newCallLogPojo.setCallerIdNumber(username);
            newCallLogPojo.setCallUUID(eslMessage.getBodyLines().get(0));
            newCallLogPojo.setStartTimestamp(new Timestamp(System.currentTimeMillis()));
            this.callLogService.update(newCallLogPojo);
        }else {
            newCallLogPojo=new CallLogPojo();
            newCallLogPojo.setAddTime(new Timestamp(System.currentTimeMillis()));
            newCallLogPojo.setProjectCode(callTask.getProjectCode());
            newCallLogPojo.setDestinationNumber(telNo);
            newCallLogPojo.setCallTaskCode(callTaskCode);
            newCallLogPojo.setGwCode(gatewayPojo.getGwCode());
            if (gatewayPojo.getBillType()!=null&&gatewayPojo.getBillType()==2){
                newCallLogPojo.setPriceRule(gatewayPojo.getBillsec()+"|"+gatewayPojo.getPrice());
            }
            newCallLogPojo.setCallerIdNumber(username);
            newCallLogPojo.setCallUUID(eslMessage.getBodyLines().get(0));
            newCallLogPojo.setStartTimestamp(new Timestamp(System.currentTimeMillis()));
            this.callLogService.insert(newCallLogPojo);
        }

        logger.info("坐席点拨（预览式拨打）{}执行了{},{}",username,callStr,inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),"originate",callStr));

        msgOperations.convertAndSend("/topic/call/" + username,  JsonTool.getJsonString(body));
    }

    @Override
    public List<CallListPojo> getCallList(CallTaskPojo callTaskPojo, Integer count) {
        List<CallListPojo> callListPojos = new ArrayList<>();
        Integer listSize = count;
        if (listSize == null){
            listSize = callTaskPojo.getRate().intValue();
        }
        for (int i=0;i<listSize;i++){
            CallListPojo callListPojo = baseRedisDaoDBCache.rightPop(ERedisCacheKey.CALLCENTER_TASK_CALLLIST.getCode()+callTaskPojo.getCallTaskCode(),CallListPojo.class);
            if (callListPojo != null){
                callListPojos.add(callListPojo);

                //记录calllog
                CallLogPojo callLogPojo=new CallLogPojo();
                callLogPojo.setAddTime(new Timestamp(System.currentTimeMillis()));
                callLogPojo.setCallTaskCode(callTaskPojo.getCallTaskCode());
                callLogPojo.setDestinationNumber(callListPojo.getPhone());
                callLogPojo.setProjectCode(callTaskPojo.getProjectCode());
                callLogPojo.setResultDetail("99999");
                callLogPojo.setHangupCase("NO_CALL_DISTRIBUTION");
                this.callLogService.insert(callLogPojo);
            }else {
                break;
            }
        }
        return callListPojos;
    }

    @Async
    @Override
    public void agentCall(Map<String, String> map, String username, String telNo) {
        UserinfoPojo userinfoPojo = this.userInfoServiceApi.getByUsername(username);
        Map<String,String> notify = new HashMap<>();
        notify.put("type","message");
        try {

            CallLogPojo callLogPojo = new CallLogPojo();
            callLogPojo.setDestinationNumber(telNo);
            callLogPojo.setCallerIdNumber(username);

            Calendar c1 = Calendar.getInstance();
            c1.set(Calendar.HOUR_OF_DAY, 0);
            c1.set(Calendar.MINUTE, 0);
            c1.set(Calendar.SECOND, 0);
            c1.set(Calendar.MILLISECOND, 0);
            callLogPojo.setEndTimestamp(new Timestamp(c1.getTimeInMillis()));
            callLogPojo = this.callLogService.selectUnique(callLogPojo);

            Integer callCount = baseRedisDao.get(ERedisCacheKey.CALLCENTER_TEST_CALL_DAY_COUNT.getCode()+ username,Integer.class);
            if (callLogPojo==null||StringUtils.isEmpty(callLogPojo.getCallTaskCode())){
                if (callCount!=null&&callCount>=20){
                    notify.put("data","当天拨打次数超限");
                    msgOperations.convertAndSend("/topic/notify/" + map.get("projectCode")+"/"+ username, JsonTool.getFormatJsonString(notify));
                    return;
                }
            }

            GatewayPojo gatewayPojo = this.gatewayServiceApi.getReturnVisitGw();
            if (gatewayPojo==null){
                notify.put("data","拨打线路未指定，请联系管理员");
                msgOperations.convertAndSend("/topic/notify/" + map.get("projectCode")+"/"+ username, JsonTool.getFormatJsonString(notify));
                return;
            }

            String callUrl=null;
            String callTelNo= telNo;
            if (StringTool.isPhoneNumber(telNo)&&StringUtils.isNotEmpty(gatewayPojo.getMobilePhonePrefix())){
                callTelNo=gatewayPojo.getMobilePhonePrefix()+ telNo;
            }else if (!StringTool.isPhoneNumber(telNo)&&StringUtils.isNotEmpty(gatewayPojo.getFixedPhonePrefix())){
                callTelNo=gatewayPojo.getFixedPhonePrefix()+ telNo;
            }
            if (gatewayPojo.getRegisterType()==1){
                callUrl="sofia/gateway/"+gatewayPojo.getGwCode()+"/"+callTelNo;
            }else {
                EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
                        "sofia_contact",gatewayPojo.getUsername());
                logger.info("网关用户 sofia_contact {},结果{}",gatewayPojo.getUsername(),EslHelper.formatEslMessage(eslMessage));
                String gwurl=eslMessage.getBodyLines().get(0);
                callUrl="sofia/internal/"+callTelNo+gwurl.substring(gwurl.lastIndexOf("@"));
            }
            EslMessage eslMessage = inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),
                    "create_uuid",null);
            logger.info("坐席{}回访create_uuid结果{}", username,EslHelper.formatEslMessage(eslMessage));

            if (gatewayPojo.getCodec()!=null&&gatewayPojo.getCodec().equals("G711")){
                gatewayPojo.setCodec(null);//使用默认编码
            }
            String callStr="{return_visit=true"+
                    (callLogPojo==null||callLogPojo.getCallTaskCode()==null?",execute_on_answer='sched_hangup +1800'":"")+
                    (callLogPojo!=null&&callLogPojo.getCallTaskCode()!=null?",callcenter_project_code="+callLogPojo.getProjectCode()+",callcenter_task_code="+callLogPojo.getCallTaskCode():"")+
                    "}user/"+ username +
                    " &bridge({sip_sticky_contact=true,return_visit=true,origination_uuid="+eslMessage.getBodyLines().get(0)+
                    (StringUtils.isNotEmpty(gatewayPojo.getOriginationCallId())? ",origination_caller_id_number="+gatewayPojo.getOriginationCallId():"")+
                    (StringUtils.isNotEmpty(gatewayPojo.getCodec())? ",absolute_codec_string="+gatewayPojo.getCodec():"")+
                    ",execute_on_answer='record_session "+recordingsPath+ DateTool.formatDate("yyyyMMddHHmmss") + telNo +".mp3'"+
                    (callLogPojo!=null&&callLogPojo.getCallTaskCode()!=null?",callcenter_project_code="+callLogPojo.getProjectCode()+",callcenter_task_code="+callLogPojo.getCallTaskCode():"")+
                    "}"+callUrl+")";

            Map<String,String> body=new HashMap<>();
            body.put("telNo", telNo);
            body.put("callId", eslMessage.getBodyLines().get(0));
            //记录calllog
            CallLogPojo newCallLogPojo=new CallLogPojo();
            newCallLogPojo.setAddTime(new Timestamp(System.currentTimeMillis()));
            if (callLogPojo!=null&&callLogPojo.getCallTaskCode()!=null){
                newCallLogPojo.setCallTaskCode(callLogPojo.getCallTaskCode());
                newCallLogPojo.setProjectCode(callLogPojo.getProjectCode());
                body.put("callTaskCode", callLogPojo.getCallTaskCode());
            }else {
                if (callCount==null){
                    callCount=0;
                }
                baseRedisDao.saveOrUpdate(ERedisCacheKey.CALLCENTER_TEST_CALL_DAY_COUNT.getCode()+ username,callCount+1,Long.valueOf((86400 - DateUtils.getFragmentInSeconds(Calendar.getInstance(), Calendar.DATE))).intValue());
            }
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

            logger.info("坐席{}执行了{},{}", username,callStr,inboundClient.sendAsyncApiCommand(freeswitchService.getServerAddr(userinfoPojo.getFreeswitchId()),"originate",callStr));

            msgOperations.convertAndSend("/topic/call/" + username,  JsonTool.getJsonString(body));
        }catch (Exception e){
            notify.put("data","处理异常");
            msgOperations.convertAndSend("/topic/notify/" + map.get("projectCode")+"/"+ username, JsonTool.getFormatJsonString(notify));
            logger.error("回访异常", e);
        }
    }

    public Integer getAgentIdleCnt(CallProjectPojo callProjectPojo,CallTaskPojo callTaskPojo) {
        if (callTaskPojo.getScheduleType().equals(5)){
            return callProjectPojo.getSeats();
        }else {
            List<UserinfoPojo> userinfoList = getUserinfoPojos(callProjectPojo);
            AtomicReference<Integer> agentCount = new AtomicReference<>(0);
            userinfoList.forEach(e->{
                if (e.getStatus()==1&&e.getWorkStatus()==1&&e.getSessionStatus().equals(0)){
                    if (callTaskPojo.getScheduleType().equals(4)){
                        EslMessage eslMessage =inboundClient.sendSyncApiCommand(freeswitchService.getServerAddr(e.getFreeswitchId()),"uuid_exists", e.getCalluuid() );
                        logger.info("坐席{}channel{}检查结果{}",e.getUsername(),e.getCalluuid(), EslHelper.formatEslMessage(eslMessage));
                        if (eslMessage.getBodyLines().get(0).contains("true")){
                            agentCount.getAndSet(agentCount.get() + 1);
                        }else {
//                        e.setWorkStatus(0);
//                        userInfoServiceApi.update(e);
//                        msgOperations.convertAndSend("/topic/status/" + e.getUsername(), e);
//                        logger.info("坐席{}channel不存在，系统置忙",e.getUsername());
                        }
                    }else {
                        agentCount.getAndSet(agentCount.get() + 1);
                    }
                }
            });
            return agentCount.get();
        }
    }

    public Integer getAgentSessionCnt(CallTaskPojo callTaskPojo) {
        return Math.toIntExact(baseRedisDao.unorderedZcard(ERedisCacheKey.CALLCENTER_TASK_SESSION_COUNT.getCode() + callTaskPojo.getCallTaskCode()));
    }

    public boolean isAvailableGw(GatewayPojo gatewayPojo){
        Map<String,Integer> gw=new HashMap<>();
        callTaskPojos.forEach(e->{
            List<Map> callGWs= JsonTool.getList(e.getCallGWs(), Map.class);
            callGWs.forEach(callGw->{
                Integer useCount = baseRedisDao.unorderedZcard(ERedisCacheKey.CALLCENTER_TASK_RUNNING_COUNT.getCode()+e.getCallTaskCode()+"_"+callGw.get("code")).intValue();
                if (gw.containsKey(callGw.get("code"))){
                    Integer concurrency=useCount+gw.get(callGw.get("code"));
                    gw.put((String) callGw.get("code"),concurrency);
                }else {
                    gw.put((String) callGw.get("code"),useCount);
                }
            });

        });
        return gatewayPojo.getExpectCount()>gw.get(gatewayPojo.getGwCode());
    }
}
