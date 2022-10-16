/** */
package com.striveh.callcenter.server.base;

import com.striveh.callcenter.common.base.controller.AbsBaseController;
import com.striveh.callcenter.common.base.pojo.BasePojo;
import org.springframework.stereotype.Component;

@Component
public class BaseController<T extends BasePojo> extends AbsBaseController<T> {

}
