/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.IDbDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.DbDataPojo;

/**
 * @功能:【db_data 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:30
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/dbData")
public class DbDataController extends BaseController<DbDataPojo> {
	/** db_data service*/
    @Autowired
    private IDbDataService dbDataService;
    
  	/**
	 * get
	 * @param dbData
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(DbDataPojo dbData, HttpServletRequest request, HttpServletResponse response) {

	}
}
