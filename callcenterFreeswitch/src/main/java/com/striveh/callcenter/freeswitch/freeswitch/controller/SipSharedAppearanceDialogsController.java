/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ISipSharedAppearanceDialogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.SipSharedAppearanceDialogsPojo;

/**
 * @功能:【sip_shared_appearance_dialogs 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:31
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/sipSharedAppearanceDialogs")
public class SipSharedAppearanceDialogsController extends BaseController<SipSharedAppearanceDialogsPojo> {
	/** sip_shared_appearance_dialogs service*/
    @Autowired
    private ISipSharedAppearanceDialogsService sipSharedAppearanceDialogsService;
    
  	/**
	 * get
	 * @param sipSharedAppearanceDialogs
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(SipSharedAppearanceDialogsPojo sipSharedAppearanceDialogs, HttpServletRequest request, HttpServletResponse response) {

	}
}
