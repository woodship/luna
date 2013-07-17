package org.woodship.luna;


import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;

import org.woodship.luna.util.Utils;

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
				//自定义异常
				Notification.show(t.getMessage(),Type.ERROR_MESSAGE);
				return;
			}else if(t instanceof javax.validation.ConstraintViolationException){
				//验证不通过，此处不处理具体实现（hiberante）的验证，所在需要在实体的验证注解上使用javax.validation下的的注解
				for(ConstraintViolation<?> v : ((javax.validation.ConstraintViolationException)t).getConstraintViolations()){
					String fn = v.getPropertyPath().toString();
					String caption = Utils.getCaption(v.getRootBeanClass(),fn);
					Notification.show( caption+v.getMessage(),Type.ERROR_MESSAGE);
					return;
				}
			}else if(t instanceof javax.persistence.PersistenceException){
				//数据库验证异常
				String msg =Utils.getPersistenceErrorMsg((PersistenceException) t);
				if(msg == null){
					event.getThrowable().printStackTrace();
					break;
				}
				Notification.show(msg,Type.ERROR_MESSAGE);
				return;
			}
		}
		super.error(event);
	}

}
