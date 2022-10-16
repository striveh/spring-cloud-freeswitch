package com.striveh.callcenter.common.constant.param;

import com.striveh.callcenter.common.constant.EnumProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目：callcenter
 * 日期：2020年4月6日
 * 作者：striveh
 * 功能：
 */
public enum ERedisCacheKey {
    KEY_SYS_AUTHINFO_NEW_VER("sysAuthinfoNewVer", "系统验证信息NewVer"),
    KEY_SYS_FREESWITCH_NEW_VER("sysFreeswitchNewVer", "系统freeswitch实例信息NewVer"),
    CALLCENTER_TASK_STATUS("callcenterTaskStatus_","呼叫任务启动状态"),
    CALLCENTER_TASK_PAUSE("callcenterTaskPause_","呼叫任务暂停一会"),
    CALLCENTER_TASK_RATE("callcenterTaskRate_","呼叫任务倍率"),
    CALLCENTER_TASK_GWS("callcenterTaskGws_","呼叫任务网关列表"),
    CALLCENTER_TASK_CALLLIST_LASTID("callcenterTaskCallListLastId_","呼叫任务拉取到的最近呼叫列表ID"),
    CALLCENTER_TASK_CALLLIST("callcenterTaskCallList_","呼叫任务呼叫列表"),
    CALLCENTER_TASK_CALLLIST_REPEAT("callcenterTaskCallListRepeat_","呼叫任务重呼呼叫列表"),
    CALLCENTER_TASK_GATEWAY("callcenterTaskGateway_","呼叫任务呼叫网关"),
    CALLCENTER_TASK_RUNNING_COUNT("callcenterTaskRunningCount_","呼叫任务呼叫执行中的数量"),
    CALLCENTER_TASK_SESSION_COUNT("callcenterTaskSessionCount_","呼叫任务呼叫通话中的数量"),
    CALLCENTER_TASK_SUCCESS_COUNT("callcenterTaskSuccessCount_","呼叫任务呼叫成功数"),
    CALLCENTER_TASK_FAIL_COUNT("callcenterTaskCallFailCount_","呼叫任务呼叫失败数"),
    CALLCENTER_TASK_FAIL_REJECY_COUNT("callcenterTaskCallFailRejectCount_","呼叫任务呼叫被拒绝数"),
    CALLCENTER_TASK_MISS_COUNT("callcenterTaskCallMissCount_","呼叫任务呼叫漏接数"),
    CALLCENTER_TASK_TOTAL_COUNT("callcenterTaskCallTotalCount_","呼叫任务呼叫列表总数"),
    CALLCENTER_TEST_CALL_DAY_COUNT("callcenterTestCallCount_","坐席测试号码当天次数"),
    CALLCENTER_AGENT_WORK_STATUS_SESSION_AFTER("callcenterAgentWorkStatusSessionAfter_","通话结束后坐席的状态"),
    CALLCENTER_AGENT_WORK_STATUS_IDLE("callcenterAgentWorkStatusIdle_","坐席置闲"),
    CALLCENTER_AGENT_SESSION_EVALUATION("callcenterAgentSessionEvaluation_","坐席通话评分"),
    CALLCENTER_AGENT_SESSION_EVALUATION_PLAYBACK("callcenterAgentSessionEvaluationPlayback_","坐席通话评分防误触"),
    CALLCENTER_CALLLOG_QUEUE("callcenterCallLogQueue","呼叫记录队列"),

    ;
    /** 状态编号 */
    private String code;
    /** 状态名称 */
    private String name;
    /** 所有状态 */
    private static Map<Object, EnumProperty> statusMap;

    /**
     * 得到所有状态
     *
     * @return
     */
    public static Map<Object, EnumProperty> getStatusMap() {
        if (statusMap == null) {
            Map<Object, EnumProperty> map = new HashMap<Object, EnumProperty>();
            EnumProperty temp = null;
            ERedisCacheKey[] vals = ERedisCacheKey.values();
            for (ERedisCacheKey val : vals) {
                temp = new EnumProperty(val.code, val.name, val.name);
                map.put(val.code, temp);
            }
            statusMap = map;
        }
        return statusMap;
    }

    /**
     * 根据状态值取得对应状态信息
     *
     * @param key
     * @return
     */
    public static EnumProperty getStatusByVal(Object key) {
        return getStatusMap().get(key);
    }

    /**
     * 构造函数
     *
     * @param code
     * @param name
     */
    private ERedisCacheKey(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /** @取得 状态编号 */
    public String getCode() {
        return code;
    }

    /** @取得 状态名称 */
    public String getName() {
        return name;
    }

}
