package com.striveh.callcenter.common.constant.status;

import com.striveh.callcenter.common.constant.EnumProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目：callcenter
 * 日期：2020年4月28日
 * 作者：striveh
 * 功能：
 */
public enum ECallResult {


    //1:空号5:未接通10:呼叫被拒绝20:客户忙25:客户未接听
    ////28:等待坐席接通时挂断30:坐席忙32:坐席未接听35:坐席全忙40:排队超时挂断
    ////50:呼叫成功坐席挂断
    ////55:呼叫成功客户挂断
    RECOVERY_ON_TIMER_EXPIRE("RECOVERY_ON_TIMER_EXPIRE","5"),
    NORMAL_CLEARING("NORMAL_CLEARING","55"),

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
            ECallResult[] vals = ECallResult.values();
            for (ECallResult val : vals) {
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
    private ECallResult(String code, String name) {
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
