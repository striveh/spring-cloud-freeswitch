package com.striveh.callcenter.common.constant.response;

import com.striveh.callcenter.common.base.pojo.OptResult;
import org.apache.logging.log4j.Logger;

/**
 * 项目：callcenter 10开头
 * 日期：2020年4月6日
 * 作者：striveh
 * 功能：callcenterServer模块响应码
 */
public enum EResCodeServer {
    /****************************************************************************************************/
    /**********************************        错误响应码     ********************************************/
    /****************************************************************************************************/
    svceErrNoProjectCode(-100001,"项目代码不能为空","项目代码不能为空"),
    svceErrNoSeats(-100002,"坐席数不能为空","坐席数不能为空"),
    svceErrNoCallTaskCode(-100003,"任务代码不能为空","任务代码不能为空"),
    svceErrNoScheduleType(-100004,"系统调拨类型不能为空","系统调拨类型不能为空"),
    svceErrNoCallType(-100005,"拨打类型不能为空","拨打类型不能为空"),
    svceErrNoCallGWs(-100006,"线路列表不能为空","线路列表不能为空"),
    svceErrNoCallListId(-100007,"呼叫号码列表ID不能为空","呼叫号码列表ID不能为空"),
    svceErrNoExpiredTime(-100008,"过期时间不能为空","过期时间不能为空"),
    svceErrNoRate(-100009,"拨打倍率不能为空","拨打倍率不能为空"),
    svceErrNoCertificate(-100010,"坐席领取分机凭证不能为空","坐席领取分机凭证不能为空"),
    svceErrNoUsername(-100011,"分机号不能为空","分机号不能为空"),

    svceErrCertificate(-1000100,"凭证无效","凭证无效"),
    svceErrTaskCode(-1000101,"任务不存在","任务不存在"),
    svceErrTaskExpired(-1000102,"任务已过有效期","任务已过有效期"),
    svceErrProjectCode(-1000103,"项目不存在","项目不存在"),
    svceErrNotAvailalbeFSServer(-1000104,"项目不存在","项目不存在"),
    svceErrTaskEnd(-1000105,"任务已结束","任务已结束"),
    svceErrTaskProcessing(-1000106,"有未结束的任务，请先结束该任务","有未结束的任务，请先结束该任务"),
    svceErrDistributionSeatWorkStatus(-1000107,"坐席状态未置忙","坐席状态未置忙"),

    /****************************************************************************************************/
    /**********************************        系统异常响应码     ********************************************/
    /****************************************************************************************************/

    exptCreateProject(EResCodeCommon.exceptionCode, "创建项目获得分机异常", "创建项目获得分机异常"),
    exptCreateProjectNoServer(EResCodeCommon.exceptionCode, "服务器服务能力预计超标，请结束不再使用的项目，然后重新提交", "服务器服务能力预计超标，请结束不再使用的项目，然后重新提交"),
    exptGatewayGetAvailableList(EResCodeCommon.exceptionCode, "取得线路资源信息异常", "取得线路资源信息异常"),
    exptSubmitCallTask(EResCodeCommon.exceptionCode, "提交呼叫任务异常", "提交呼叫任务异常"),
    exptGetByCertificate(EResCodeCommon.exceptionCode, "坐席认领项目分机异常", "坐席认领项目分机异常"),
    exptCallTaskStart(EResCodeCommon.exceptionCode, "呼叫任务启动异常", "呼叫任务启动异常"),
    exptSetTaskConcurrent(EResCodeCommon.exceptionCode, "设置任务并发数异常", "设置任务并发数异常"),
    exptGetCallList(EResCodeCommon.exceptionCode, "给坐席分配号码（预览式拨打）异常", "给坐席分配号码（预览式拨打）异常"),
    exptCallTaskPause(EResCodeCommon.exceptionCode, "呼叫任务暂停异常", "呼叫任务暂停异常"),
    exptCallTaskEnd(EResCodeCommon.exceptionCode, "呼叫任务结束异常", "呼叫任务结束异常"),
    exptCallTaskAdditional(EResCodeCommon.exceptionCode, "呼叫任务追加异常", "呼叫任务追加异常"),
    exptCallTaskStatus(EResCodeCommon.exceptionCode, "获取任务进度异常", "获取任务进度异常"),
    exptGetExtentionByProject(EResCodeCommon.exceptionCode, "获取项目分机状态异常", "获取项目分机状态异常"),
    exptGetUploadVoiceFile(EResCodeCommon.exceptionCode, "上传语音文件异常", "上传语音文件异常"),
    exptSetWorkStatus(EResCodeCommon.exceptionCode, "置忙置闲异常", "置忙置闲异常"),
    exptSetExtensionStatus(EResCodeCommon.exceptionCode, "分机禁用启用异常", "分机禁用启用异常"),
    exptSetWorkStatusByProject(EResCodeCommon.exceptionCode, "全部置忙置闲异常", "全部置忙置闲异常"),
    exptGetCDR(EResCodeCommon.exceptionCode, "获取项目通话清单异常", "获取项目通话清单异常"),
    exptGetCallResult(EResCodeCommon.exceptionCode, "获取项目呼叫结果异常", "获取项目呼叫结果异常"),
    exptGetRecordFile(EResCodeCommon.exceptionCode, "获取录音文件异常", "获取录音文件异常"),
    exptFinishProject(EResCodeCommon.exceptionCode, "完结项目异常", "完结项目异常"),
    exptAddGateway(EResCodeCommon.exceptionCode, "添加线路异常", "添加线路异常"),
    exptUpdateGateway(EResCodeCommon.exceptionCode, "编辑线路异常", "编辑线路异常"),
    exptGatewayGetList(EResCodeCommon.exceptionCode, "获取线路列表异常", "获取线路列表异常"),
    exptSendToUser(EResCodeCommon.exceptionCode, "给坐席发送消息失败", "给坐席发送消息失败"),
    exptDistributionSeat(EResCodeCommon.exceptionCode, "给项目分配坐席失败", "给项目分配坐席失败"),
    exptSetCallParams(EResCodeCommon.exceptionCode, "设置系统参数异常", "设置系统参数异常"),

    /****************************************************************************************************/
    /**********************************        成功响应码     ********************************************/
    /****************************************************************************************************/

    ;
    /** 消息代码,大于等于0表示正确，小于表示错误 */
    private int code = 0;
    /** 消息内容（内部消息） */
    private String innerMsg;
    /** 消息内容 （外部消息） */
    private String msg;

    private EResCodeServer(int code, String innerMsg, String msg) {
        this.code = code;
        this.msg = msg;
        this.innerMsg = innerMsg;
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
