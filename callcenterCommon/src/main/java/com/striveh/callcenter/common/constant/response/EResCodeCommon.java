package com.striveh.callcenter.common.constant.response;

import com.striveh.callcenter.common.base.pojo.OptResult;
import org.apache.logging.log4j.Logger;

/**
 * 项目：callcenter
 * 日期：2020年4月6日
 * 作者：striveh
 * 功能： 通用内容
 */
public enum EResCodeCommon {

    /****************************************************************************************************/
    /**********************************        错误响应码     ********************************************/
    /****************************************************************************************************/
    /** -9980：客户端访问token无效，禁止请求 */
    noClientToken(-9980, "客户端访问禁止请求", EResCodeCommon.accessDenied),
    /** -9981：用户没有登录，无权访问。 */
    noUserToken(-9981, "您还没有登录，无权访问", "您还没有登录，请登录"),
    /** -9982：token过期 */
    errUserToken(-9982, "登录超时，请重新登录", "登录超时，请重新登录"),
    /** -9983：多用户同时登录。 */
    repeatUserToken(-9983, "您已在另一处登录，请确认密码是否外泄", "您已在另一处登录，请确认密码是否外泄"),
    /** -9984：请求无效/无权访问。 */
    errRequest(-9984, "请求无效/无权访问。", "请求无效/无权访问。"),
    /** 系统已升级，请您下载最新app再使用 */
    svceErrAppIsNotNewest(-9985, "该版本已无法继续使用，请更新至最新版本", "该版本已无法继续使用，请更新至最新版本"),

    /** 业务逻辑：签名超时/无效 */
    svceErrSysAuthInfoTimeOut(EResCodeCommon.exceptionCode, "账户签名超时/无效。", "签名超时/无效"),

    /****************************************************************************************************/
    /**********************************        异常响应码     ********************************************/
    /****************************************************************************************************/

    /** -9997：服务器开小差。 */
    exptSystemException(EResCodeCommon.exceptionCode, "服务器开小差了，稍后再试！", EResCodeCommon.exceptionMsg),
    exptIllegalArgument(EResCodeCommon.exceptionCode, "参数出现异常，或者参数为空", EResCodeCommon.exceptionMsg),
    exptDataException(EResCodeCommon.exceptionCode, "资料有误请重新提交", "资料有误请重新提交"),

    /****************************************************************************************************/
    /**********************************        成功响应码     ********************************************/
    /****************************************************************************************************/
    /** 业务逻辑：文件下载成功 */
    svceRigDownVideoFile(EResCodeCommon.optSuccess, "文件下载成功", "文件下载成功"),
    /** 业务逻辑：什么都没做 */
    svceRigDoNothing(EResCodeCommon.optNothing, "什么都没做", ""),
    /** 提交成功 */
    svceRigSubmitSuccess(EResCodeCommon.optSuccess, "提交成功", "提交成功"),
    /** 操作成功 */
    svceRigOptSuccess(EResCodeCommon.optSuccess, "操作成功", "操作成功"),
    /** 新增数据成功 */
    svceRigAddDataSuccess(EResCodeCommon.optSuccess, "新增数据成功", "新增数据成功"),
    /** 获取数据成功 */
    svceRigGetDataSuccess(EResCodeCommon.optSuccess, "获取数据成功", "获取数据成功"),
    /** 更新数据成功 */
    svceRigUpdateDataSuccess(EResCodeCommon.optSuccess, "更新数据成功", "更新数据成功"),
    /** 没有查询到数据 */
    svceRigNoDataSuccess(EResCodeCommon.optSuccess, "没有查询到数据记录", "没有查询到数据记录"),
    /** 提交成功 */
    svceRigSubmitNothingSuccess(EResCodeCommon.optSuccess, "提交成功(nothing)", "提交成功(nothing)"),
    ;


    /** 什么都没做 */
    public static final int optNothing = 0;
    /** 操作成功 */
    public static final int optSuccess = 1;
    /** 系统异常code */
    public static final int exceptionCode = -9997;
    /** 系统异常信息 */
    public static final String exceptionMsg = "服务器开小差了，请稍后再试！";
    /** 系统异常信息 */
    public static final String accessDenied = "服务器拒绝访问！";

    /** 消息代码,大于等于0表示正确，小于表示错误 */
    private int code = 0;
    /** 消息内容（内部消息） */
    private String innerMsg;
    /** 消息内容 （外部消息） */
    private String msg;

    EResCodeCommon(int code, String innerMsg, String msg) {
        this.code = code;
        this.msg = msg;
        this.innerMsg = innerMsg;
    }

    EResCodeCommon(String msg) {
        this.code = EResCodeCommon.optSuccess;
        this.msg = msg;
        this.innerMsg = msg;
    }

    /**
     * @取得 消息代码大于等于0表示正确，小于表示错误
     */
    public int getCode() {
        return code;
    }

    /** @取得 消息内容（外部消息） */
    public String getMsg() {
        return msg;
    }

    /**
     * 转换成OptResult对象
     *
     * @return
     */
    public OptResult getOptResult(Logger logger) {
        return getOptResult(logger, "");
    }

    /**
     * 转换成OptResult对象
     *
     * @return
     */
    public OptResult getOptResult(Logger logger, Object otherMsg) {
        logger.info(new OptResult(code, msg, innerMsg, otherMsg));
        return new OptResult(code, msg);
    }

    /**
     * 转换成OptResult对象
     *
     * @return
     */
    public OptResult getOptResult(Logger logger, Object otherMsg, Exception e) {
        logger.error(new OptResult(code, msg, innerMsg, otherMsg), e);
        return new OptResult(code, msg);
    }
}
