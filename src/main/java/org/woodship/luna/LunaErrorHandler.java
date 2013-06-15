package org.woodship.luna;


import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ErrorEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class LunaErrorHandler extends DefaultErrorHandler {
	private static final long serialVersionUID = 4408958693908493996L;

	@Override
	public void error(ErrorEvent event) {
		for(Throwable t = event.getThrowable(); t != null; t = t.getCause()){
			if(t instanceof LunaException){
				Notification.show(t.getMessage(),Type.ERROR_MESSAGE);
				return;
			}else if(t instanceof org.hibernate.exception.ConstraintViolationException){
				String msg = t.getMessage();
				if(msg.contains("FOREIGN KEY")){
					Notification.show("该数据已被引用，禁止删除",Type.ERROR_MESSAGE);
					return;
				}
			}
		}
		super.error(event);
	}
	
}
