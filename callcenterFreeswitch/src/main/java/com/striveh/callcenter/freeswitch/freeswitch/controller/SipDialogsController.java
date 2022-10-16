/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.SipDialogsPojo;
import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ISipDialogsService;

/**
 * @功能:【sip_dialogs 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:31
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/sipDialogs")
public class SipDialogsController extends BaseController<SipDialogsPojo> {
	/** sip_dialogs service*/
    @Autowired
    private ISipDialogsService sipDialogsService;
    
  	/**
	 * get
	 * @param sipDialogs
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(SipDialogsPojo sipDialogs, HttpServletRequest request, HttpServletResponse response) {

	}
}
