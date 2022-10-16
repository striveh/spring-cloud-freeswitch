SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for callList
-- ----------------------------
DROP TABLE IF EXISTS `callList`;
CREATE TABLE `callList` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) NOT NULL,
  `result` tinyint(4) DEFAULT '1' COMMENT '1未处理',
  `callListId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='呼叫列表表 目前用不到';

-- ----------------------------
-- Table structure for callLog
-- ----------------------------
DROP TABLE IF EXISTS `callLog`;
CREATE TABLE `callLog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `callUUID` varchar(50) DEFAULT NULL COMMENT 'callUUID',
  `projectCode` varchar(20) DEFAULT NULL COMMENT '项目代码',
  `callTaskCode` varchar(20) DEFAULT NULL COMMENT '任务代码',
  `callerIdNumber` varchar(20) DEFAULT NULL COMMENT '主叫号码',
  `destinationNumber` varchar(20) DEFAULT NULL COMMENT '被叫号码',
  `startTimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `endTimestamp` timestamp NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束时间',
  `recordFile` varchar(100) DEFAULT NULL COMMENT '录音文件在文件服务器的地址',
  `recordFileQiniu` varchar(100) DEFAULT NULL COMMENT '录音文件在七牛文件服务器的地址',
  `duration` int(11) DEFAULT NULL COMMENT '持续时间,单位秒',
  `billsec` int(11) DEFAULT NULL COMMENT '计费时长,单位秒',
  `gwCode` varchar(20) DEFAULT NULL COMMENT '网关代码',
  `priceRule` varchar(10) DEFAULT NULL COMMENT '每多少秒多少钱[用|隔开] 格式：50|0.6',
  `priceCal` decimal(6,2) DEFAULT NULL COMMENT '实际计算价格，单位分',
  `result` varchar(2) DEFAULT NULL COMMENT '呼叫结果，10:成功20:失败30:漏接',
  `resultDetail` varchar(5) DEFAULT NULL COMMENT '呼叫结果明细，1:空号5:未接通10:呼叫被拒绝20:客户忙25:客户未接听28:等待坐席接通时挂断30:坐席忙32:坐席未接听35:坐席全忙40:排队超时挂断50:呼叫成功坐席挂断55:呼叫成功客户挂断99999:未处理',
  `hangupCase` varchar(50) DEFAULT NULL COMMENT 'FS外呼结果',
  `addTime` timestamp NULL DEFAULT NULL COMMENT '添加时间',
  `updateTime` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `evaluation` tinyint(1) DEFAULT NULL COMMENT '用户满意度评分，1非常满意2满意3不满意',
  PRIMARY KEY (`id`),
  KEY `index_callerIdNumber` (`callerIdNumber`) USING BTREE,
  KEY `index_endTimestamp` (`endTimestamp`) USING BTREE,
  KEY `index_result` (`result`) USING BTREE,
  KEY `index_resultDCode` (`resultDetail`,`callTaskCode`) USING BTREE,
  KEY `index_dnctc` (`destinationNumber`,`callTaskCode`) USING BTREE,
  KEY `index_uuid` (`callUUID`),
  KEY `index_hangupCase` (`hangupCase`),
  KEY `index_startTimestamp` (`startTimestamp`),
  KEY `index_callTaskCode` (`callTaskCode`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='呼叫日志表';

-- ----------------------------
-- Table structure for callProject
-- ----------------------------
DROP TABLE IF EXISTS `callProject`;
CREATE TABLE `callProject` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectCode` varchar(50) NOT NULL COMMENT '项目代码',
  `seats` smallint(6) DEFAULT NULL COMMENT '坐席数',
  `certificate` varchar(50) DEFAULT NULL COMMENT '坐席领取项目分机凭证',
  `extNumInterval` varchar(20) DEFAULT NULL COMMENT '分配给该项目的分机号段',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态1进行中2完结',
  `freeswitchId` bigint(20) DEFAULT NULL COMMENT '项目当前占用的Freeswitch服务实例ID',
  `addTime` timestamp NULL DEFAULT NULL COMMENT '添加时间',
  `updateTime` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_code` (`projectCode`),
  KEY `index_status` (`status`) USING BTREE,
  KEY `index_freeswitchId` (`freeswitchId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='呼叫项目表';

-- ----------------------------
-- Table structure for callTask
-- ----------------------------
DROP TABLE IF EXISTS `callTask`;
CREATE TABLE `callTask` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectCode` varchar(50) NOT NULL COMMENT '项目代码',
  `callTaskCode` varchar(50) NOT NULL COMMENT '任务代码',
  `scheduleType` tinyint(6) NOT NULL COMMENT '系统调拨类型，1直接转坐席2播放录音后转坐席3先接坐席再拨客户4摘机坐席5播放录音',
  `callType` tinyint(4) DEFAULT NULL COMMENT '拨打类型，1预测式拨打2预览式拨打',
  `callGWs` varchar(200) DEFAULT NULL COMMENT '线路列表[{"code":"1001","concurrency":100},{"code":"1002","concurrency":200}]',
  `callListId` bigint(20) DEFAULT NULL COMMENT '呼叫号码列表ID',
  `expiredTime` timestamp NULL DEFAULT NULL COMMENT '过期时间',
  `voiceCode` varchar(25) DEFAULT NULL COMMENT '语音代码，当scheduleType等于2或5时必传',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态1初始化2启动3暂定4结束',
  `rate` double(4,1) DEFAULT NULL COMMENT '拨打倍率',
  `totalCount` int(11) DEFAULT NULL COMMENT '呼叫列表总数',
  `successCount` smallint(6) DEFAULT NULL COMMENT '成功数',
  `failCount` int(11) DEFAULT NULL COMMENT '失败数',
  `missCount` smallint(6) DEFAULT NULL COMMENT '漏接数',
  `addTime` timestamp NULL DEFAULT NULL COMMENT '添加时间',
  `updateTime` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_callTaskCode` (`callTaskCode`),
  KEY `index_projectCode` (`projectCode`),
  KEY `index_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='呼叫任务表';

-- ----------------------------
-- Table structure for voice
-- ----------------------------
DROP TABLE IF EXISTS `voice`;
CREATE TABLE `voice` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `voiceCode` varchar(25) NOT NULL COMMENT '语音代码',
 `patch` varchar(255) DEFAULT NULL COMMENT '文件地址',
 `createTime` datetime DEFAULT NULL,
 PRIMARY KEY (`id`),
 UNIQUE KEY `index_code` (`voiceCode`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='语音信息表';

-- ----------------------------
-- Table structure for freeswitch
-- ----------------------------
DROP TABLE IF EXISTS `freeswitch`;
CREATE TABLE `freeswitch` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `host` varchar(20) NOT NULL COMMENT '服务实例主机地址',
    `port` int(6) NOT NULL COMMENT '服务实例主机ESL端口',
    `password` varchar(20) NOT NULL COMMENT '服务实例主机ESL密码',
    `callConcurrent` int(11) DEFAULT 100 COMMENT '支持的最大通话并发数',
    `nativeSipPhone` varchar(50) NOT NULL COMMENT '服务实例用于native软电话使用的服务地址',
    `webSipPhone` varchar(50) NOT NULL COMMENT '服务实例用于web软电话使用的服务地址',
    `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态0停用1启用',
    `addTime` timestamp NULL DEFAULT NULL COMMENT '添加时间',
    `updateTime` timestamp NULL DEFAULT NULL COMMENT '更新时间',
    `projectId` bigint(20) DEFAULT NULL COMMENT '当前使用该服务实例的项目ID',
    `weight` tinyint(2) DEFAULT '0' COMMENT '服务能力权重',
    PRIMARY KEY (`id`),
    KEY `index_status` (`status`),
    KEY `index_host` (`host`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='freeswitch服务实例配置表';

-- ----------------------------
-- Table structure for gateway
-- ----------------------------
DROP TABLE IF EXISTS `gateway`;
CREATE TABLE `gateway` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gwCode` varchar(50) NOT NULL COMMENT '线路编号',
  `gwName` varchar(50) NOT NULL COMMENT '线路名称',
  `concurrency` int(11) DEFAULT NULL COMMENT '最大并发量',
  `usedConcurrency` int(11) DEFAULT NULL COMMENT '已用并发量',
  `availableConcurrency` int(11) DEFAULT NULL COMMENT '可用并发量',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注描述',
  `username` varchar(50) DEFAULT NULL COMMENT '网关参数username',
  `password` varchar(50) DEFAULT NULL COMMENT '网关参数password',
  `realm` varchar(50) DEFAULT NULL COMMENT '网关参数realm',
  `register` varchar(10) DEFAULT NULL COMMENT '网关参数register',
  `registerType` tinyint(1) DEFAULT NULL COMMENT '网关注册类型1主动模式2被动模式',
  `originationCallId` varchar(20) DEFAULT NULL COMMENT '主叫号码',
  `codec` varchar(10) DEFAULT NULL COMMENT '语音编码',
  `fixedPhonePrefix` varchar(10) DEFAULT NULL COMMENT '呼叫座机前缀',
  `mobilePhonePrefix` varchar(10) DEFAULT NULL COMMENT '呼叫前缀',
  `callLimit` tinyint(2) DEFAULT NULL COMMENT '呼叫次数',
  `price` decimal(5,2) DEFAULT NULL COMMENT '计费单价（分）',
  `billType` tinyint(1) DEFAULT NULL COMMENT '计费方式 1按次数、2按秒(每多少秒多少钱)',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 0禁用、1启用',
  `expectCount` smallint(6) DEFAULT NULL COMMENT '预计线路数',
  `billsec` tinyint(2) DEFAULT NULL COMMENT '按秒(每多少秒多少钱)秒数',
  `addTime` timestamp NULL DEFAULT NULL COMMENT '添加时间',
  `updateTime` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `returnVisit` tinyint(1) DEFAULT NULL COMMENT '回访线路',
  `accountBalance` decimal(10,2) DEFAULT NULL COMMENT '账户余额',
  `rechargedAmount` decimal(10,2) DEFAULT NULL COMMENT '已充值金额',
  `smartRoute` tinyint(1) DEFAULT NULL COMMENT '0非智能线路1智能线路',
  `useSmartRoutePercent` varchar(4) DEFAULT NULL COMMENT '使用智能线路百分比',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_status` (`status`),
  KEY `index_gwCode` (`gwCode`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='FreeSWITCH线路表';

-- ----------------------------
-- Table structure for sysAuthInfo
-- ----------------------------
DROP TABLE IF EXISTS `sysAuthInfo`;
CREATE TABLE `sysAuthInfo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `account` varchar(20) NOT NULL COMMENT '账号',
  `secret` varchar(50) NOT NULL COMMENT '密钥',
  `type` smallint(6) NOT NULL DEFAULT '1' COMMENT '账户类型',
  `user` varchar(50) NOT NULL COMMENT '使用者',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `expirTime` datetime DEFAULT NULL COMMENT '过期时间',
  `createrId` bigint(20) NOT NULL COMMENT '创建人id',
  `createrName` varchar(30) NOT NULL COMMENT '创建人姓名',
  `createTime` datetime(3) NOT NULL COMMENT '创建时间',
  `modId` bigint(20) DEFAULT NULL COMMENT '修改人id',
  `modName` varchar(30) DEFAULT NULL COMMENT '修改人姓名',
  `modTime` datetime(3) DEFAULT NULL COMMENT '修改时间',
  `status` int(2) NOT NULL COMMENT '状态(0停用、1正常)',
  PRIMARY KEY (`id`),
  KEY `ak_sysAuthInfo_code` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='接口访问认证配置表';

-- ----------------------------
-- Table structure for userinfo
-- ----------------------------
DROP TABLE IF EXISTS `userinfo`;
CREATE TABLE `userinfo` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `username` varchar(10) NOT NULL COMMENT '分机用户名',
    `password` varchar(30) NOT NULL COMMENT '分机密码',
    `projectId` bigint(20) DEFAULT NULL COMMENT '项目ID',
    `workStatus` tinyint(2) NOT NULL DEFAULT '0' COMMENT '-1不可用、0不可通话、1可通话',
    `calluuid` varchar(40) DEFAULT NULL COMMENT '摘机坐席的calluuid',
    `lastSessionBeginTime` timestamp NULL DEFAULT NULL COMMENT '最近一次通话开始时间',
    `sessionStatus` tinyint(2) DEFAULT '0' COMMENT '会话状态，0未通话10通话中',
    `status` tinyint(1) DEFAULT '0' COMMENT '0未注册1已注册',
    `destinationNumber` varchar(20) DEFAULT NULL COMMENT '最新一次通话手机号',
    `callTaskCode` varchar(11) DEFAULT NULL COMMENT '任务代码',
    `destinationUUID` varchar(40) DEFAULT NULL,
    `type` tinyint(1) DEFAULT '1' COMMENT '1语音网关2坐席顾问3项目经理',
    `freeswitchId` int(11) DEFAULT NULL COMMENT '分配给的Freeswitch服务实例ID',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `index_username` (`username`) USING BTREE,
    KEY `index_projectId` (`projectId`),
    KEY `index_workStatus` (`workStatus`),
    KEY `index_proIdStatus` (`workStatus`,`projectId`),
    KEY `index_freeswitchId` (`freeswitchId`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='FreeSWITCH分机信息表';

SET FOREIGN_KEY_CHECKS = 1;




