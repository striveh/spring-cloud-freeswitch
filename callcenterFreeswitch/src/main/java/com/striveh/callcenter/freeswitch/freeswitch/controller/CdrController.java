/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.common.base.pojo.OptResult;
import com.striveh.callcenter.common.base.pojo.ResponseData;
import com.striveh.callcenter.common.constant.response.EResCodeCommon;
import com.striveh.callcenter.common.constant.response.EResCodeServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.CdrPojo;
import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ICdrService;

import java.util.HashMap;
import java.util.Map;

/**
 * @功能:【cdr 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:29
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/cdr")
public class CdrController extends BaseController<CdrPojo> {
	/** cdr service*/
    @Autowired
    private ICdrService cdrService;
    
  	/**
	 * get
	 * @param cdr
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(CdrPojo cdr, HttpServletRequest request, HttpServletResponse response) {

	}

	/**
	 * 获取项目通话清单
	 * 1 pageNum Integer √ 第几页
	 * 2 pageSize Integer √ 每页条数
	 * 3 projectCode String X 项目代码
	 * 4 callTask Code String X 任务代码
	 */
	@RequestMapping("/api/getList/v1")
	public ResponseData getList(CdrPojo cdr) {
		OptResult info = null;
		Map<String,Object> data=new HashMap<>();
		try {
			data.put("items",this.cdrService.selectList(cdr));
			data.put("total",cdr.getTotalRowCount());
			info = EResCodeCommon.svceRigGetDataSuccess.getOptResult(logger);
		} catch (Exception ex) {
			info = EResCodeServer.exptGetCDR.getOptResult(logger);
			logger.error(info.getMsg(), ex);
		}
		return new ResponseData<Object>(info, data);
	}
}
