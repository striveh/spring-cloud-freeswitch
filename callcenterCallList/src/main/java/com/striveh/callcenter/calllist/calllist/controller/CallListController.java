/** */
package com.striveh.callcenter.calllist.calllist.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.striveh.callcenter.calllist.calllist.service.iservice.ICallListService;
import com.striveh.callcenter.common.util.HttpTool;
import com.striveh.callcenter.common.util.JsonTool;
import com.striveh.callcenter.common.util.StringTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.calllist.CallListPojo;
import com.striveh.callcenter.calllist.base.BaseController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @功能:【callList 】controller
 * @项目名:callcentercallList
 * @作者:xxx
 * @日期:2020-04-06 12:05:43
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/callList")
public class CallListController extends BaseController<CallListPojo> {
	/** callList service*/
    @Autowired
    private ICallListService callListService;
	@Value("${common.callList.url}")
	private String callListUrl;
  	/**
	 * get
	 * @param callList
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(CallListPojo callList, HttpServletRequest request, HttpServletResponse response) {

	}

	@RequestMapping("/inner/getListByCallListId")
	public List<CallListPojo> getListByCallListId(@RequestParam Long callListId,@RequestParam Integer size,@RequestParam(required = false) Long lastId){
		if (callListId>100){
			try {
				Map<String, String> params = new HashMap<>();
				params.put("code", String.valueOf(callListId));
				params.put("limit", String.valueOf(size));
				if (lastId!=null){
					params.put("last", String.valueOf(lastId));
				}
				Long timestamp=System.currentTimeMillis();
				params.put("sign", StringTool.MD5Encode(timestamp+"TheCALLV1"));
				params.put("timestamp", String.valueOf(timestamp));
				logger.info("拉取呼叫列表请求参数{}",JsonTool.getJsonString(params));
				String result = HttpTool.requestPost(null,callListUrl,params);
				logger.info("拉取呼叫列表结果{}",result);
				JSONObject jsonObject=JSONObject.parseObject(result);
				if (jsonObject.getBooleanValue("success")){
					return JsonTool.getList(jsonObject.getJSONObject("data").getString("list"),CallListPojo.class);
				}else {
					return null;
				}
			} catch (Exception e) {
				logger.info("获取呼叫列表异常",e);
				return null;
			}
		}else {
			CallListPojo callListPojo=new CallListPojo();
			callListPojo.setId(lastId);
			callListPojo.setPageSize(size);
			callListPojo.setCallListId(callListId);
			return this.callListService.selectList(callListPojo);
		}
	}
}
