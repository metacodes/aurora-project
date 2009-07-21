package com.aurora.ui;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.features.DataBinding;

/**
 * 文本输入框
 * 
 * @version $Id: TextField.java v 1.0 2009-7-20 上午11:27:00 znjqolf Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 * 
 * 
 * 
 */
public class TextField extends Field {	
	
	
	public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
		super.onPreparePageContent(session, context);
		
		String classname = getStyleSheet(session, context, "textfield/TextField.css");
		if(!"".equals(classname)){
			String sb = (String)context.getContextMap().get("css");
			context.getContextMap().put("css",sb + classname);
		}
		
		String javascript = getJavaScript(session, context, "textfield/TextField.js");
		if(!"".equals(javascript)){
			String sb = (String)context.getContextMap().get("script");
			context.getContextMap().put("script",sb + javascript);
		}
	}

	public void onCreateViewContent(BuildSession session, ViewContext context) {
		super.onCreateViewContent(session, context);
		CompositeMap view = context.getView();
		Map map = context.getMap();
		
		
		map.put(PROPERTITY_VALUE, "测试");
	}
}
