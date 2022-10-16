/** */
package com.striveh.callcenter.common.base.controller;

import com.striveh.callcenter.common.base.pojo.BasePojo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class AbsBaseController<T extends BasePojo> {
	/** 日志对象 */
	protected Logger logger = LogManager.getLogger(this.getClass());

}
