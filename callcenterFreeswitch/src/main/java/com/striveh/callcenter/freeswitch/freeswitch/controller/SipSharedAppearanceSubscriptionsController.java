/** */
package com.striveh.callcenter.freeswitch.freeswitch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.striveh.callcenter.freeswitch.base.BaseController;
import com.striveh.callcenter.freeswitch.freeswitch.service.iservice.ISipSharedAppearanceSubscriptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.striveh.callcenter.pojo.freeswitch.SipSharedAppearanceSubscriptionsPojo;

/**
 * @功能:【sip_shared_appearance_subscriptions 】controller
 * @项目名:callcenterFreeswitch
 * @作者:xxx
 * @日期:2020-04-06 10:35:31
 * @说明：<pre></pre>
 */
@RestController
@RequestMapping("/sipSharedAppearanceSubscriptions")
public class SipSharedAppearanceSubscriptionsController extends BaseController<SipSharedAppearanceSubscriptionsPojo> {
	/** sip_shared_appearance_subscriptions service*/
    @Autowired
    private ISipSharedAppearanceSubscriptionsService sipSharedAppearanceSubscriptionsService;
    
  	/**
	 * get
	 * @param sipSharedAppearanceSubscriptions
	 * @param request
	 * @param response
	 */
	@RequestMapping("/get")
	public void get(SipSharedAppearanceSubscriptionsPojo sipSharedAppearanceSubscriptions, HttpServletRequest request, HttpServletResponse response) {

	}
}
