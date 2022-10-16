/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;



import com.striveh.callcenter.common.base.pojo.OptResult;
import com.striveh.callcenter.common.base.pojo.ResponseData;
import com.striveh.callcenter.common.constant.param.ERedisCacheKey;
import com.striveh.callcenter.common.constant.response.EResCodeCommon;
import com.striveh.callcenter.common.constant.response.EResCodeServer;
import com.striveh.callcenter.common.database.redis.BaseRedisDao;
import com.striveh.callcenter.common.util.JsonTool;
import com.striveh.callcenter.feignclient.callcenter.ICallTaskServiceApi;
import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IGatewayService;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IUserinfoService;
import com.striveh.callcenter.pojo.callcenter.CallTaskPojo;
import com.striveh.callcenter.pojo.freeswitch.UserinfoPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import com.striveh.callcenter.pojo.freeswitch.GatewayPojo;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @功能:【gateway 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:30
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/gateway")
public class GatewayController extends BaseController<GatewayPojo> {
	/** gateway service*/
    @Autowired
    private IGatewayService gatewayService;
    @Autowired
	private ICallTaskServiceApi callTaskServiceApi;
    @Autowired
	private IUserinfoService userinfoService;
	@Autowired
	@Qualifier("baseRedisDaoDef")
	private BaseRedisDao baseRedisDao;
  	/**
	 * 取得线路资源信息
	 */
	@RequestMapping("/api/getAvailableList/v1")
	public ResponseData getAvailableList() {
		OptResult info = null;
		List<GatewayPojo> gatewayPojoList=null;
		try {
			Map<String,Integer> gw=new HashMap<>();

			List<CallTaskPojo> callTaskPojos=this.callTaskServiceApi.getListByStatus(4);
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
			GatewayPojo gatewayPojo=new GatewayPojo();
			gatewayPojo.setStatus(1);
			gatewayPojoList=this.gatewayService.selectList(gatewayPojo);
			gatewayPojoList.forEach(e->{
				if (gw.containsKey(e.getGwCode())){
					e.setConcurrency(e.getExpectCount());
					e.setAvailableConcurrency(e.getConcurrency()-gw.get(e.getGwCode()));
					e.setUsedConcurrency(gw.get(e.getGwCode()));
				}
			});
			info = EResCodeCommon.svceRigGetDataSuccess.getOptResult(logger);
		} catch (Exception ex) {
			info = EResCodeServer.exptGatewayGetAvailableList.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info, gatewayPojoList);
	}
	/**
	 * 获取线路列表
	 */
	@RequestMapping("/api/getList/v1")
	public ResponseData getList(GatewayPojo gatewayPojo) {
		OptResult info = null;
		Map<String,Object> data=new HashMap<>();

		try {
			data.put("items",this.gatewayService.selectList(gatewayPojo));
			data.put("total",gatewayPojo.getTotalRowCount());
			info = EResCodeCommon.svceRigGetDataSuccess.getOptResult(logger);
		} catch (Exception ex) {
			info = EResCodeServer.exptGatewayGetList.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info, data);
	}
	/**
	 * 添加线路
	 */
	@RequestMapping("/api/add/v1")
	public ResponseData add(GatewayPojo gatewayPojo) {
		OptResult optResult=null;
		try {
			gatewayPojo.setGwCode("gw"+(this.gatewayService.selectList(new GatewayPojo()).size()+1));
			if (gatewayPojo.getRegisterType()==1){
				gatewayPojo.setRegister("true");
				this.callTaskServiceApi.addGateway(gatewayPojo);
			}else {
				UserinfoPojo userinfoPojo = new UserinfoPojo();
				userinfoPojo.setUsername(gatewayPojo.getUsername());
				userinfoPojo.setPassword(gatewayPojo.getPassword());
				userinfoPojo.setWorkStatus(0);
				this.userinfoService.insert(userinfoPojo);
				gatewayPojo.setRegister("false");
			}
			gatewayPojo.setAddTime(new Timestamp(System.currentTimeMillis()));
			this.gatewayService.insert(gatewayPojo);
			optResult = EResCodeCommon.svceRigSubmitSuccess.getOptResult(logger);
		} catch (Exception ex) {
			optResult = EResCodeServer.exptAddGateway.getOptResult(logger);
			logger.error(optResult.getMsg(), ex);
		}

		return new ResponseData<Object>(optResult);
	}

	/**
	 * 编辑线路
	 */
	@RequestMapping("/api/update/v1")
	public ResponseData update(GatewayPojo gatewayPojo) {
		OptResult optResult=null;
		try {
			gatewayPojo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
			if (gatewayPojo.getRegisterType()==1){
				gatewayPojo.setRegister("true");
				this.gatewayService.update(gatewayPojo);
				this.callTaskServiceApi.updateGateway(gatewayPojo);
			}else {
				UserinfoPojo userinfoPojo = new UserinfoPojo();
				userinfoPojo.setUsername(gatewayPojo.getUsername());
				userinfoPojo.setPassword(gatewayPojo.getPassword());

				this.userinfoService.update(userinfoPojo);
				gatewayPojo.setRegister("false");
				this.gatewayService.update(gatewayPojo);
			}
			optResult = EResCodeCommon.svceRigUpdateDataSuccess.getOptResult(logger);
		} catch (Exception ex) {
			optResult = EResCodeServer.exptUpdateGateway.getOptResult(logger);
			logger.error(optResult.getMsg(), ex);
		}

		return new ResponseData<Object>(optResult);
	}

	@RequestMapping(value = "/inner/update",method = RequestMethod.POST)
	public void innerUpdate(@RequestBody GatewayPojo gatewayPojo) {
		this.gatewayService.update(gatewayPojo);
	}

	@RequestMapping(value = "/inner/getByCode",method = RequestMethod.GET)
	public GatewayPojo getByCode (@RequestParam String gwCode){
		GatewayPojo gatewayPojo=new GatewayPojo();
		gatewayPojo.setGwCode(gwCode);
		return this.gatewayService.selectUnique(gatewayPojo);
	}
	@RequestMapping(value = "/inner/getReturnVisitGw",method = RequestMethod.GET)
	public GatewayPojo getReturnVisitGw (){
		GatewayPojo gatewayPojo=new GatewayPojo();
		gatewayPojo.setReturnVisit(1);
		return this.gatewayService.selectUnique(gatewayPojo);
	}
}
