package org.woodship.luna.core.security;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.db.TransactionalEntityProvider;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

@Component
public class UserEntityProvider  extends TransactionalEntityProvider<User> {

	public UserEntityProvider() {
		super(User.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void removeEntity(final Object entityId) {
		User user = this.getEntity(getJPAContainer(), entityId);
		if(user.isSysUser()){
			Notification.show("禁止删除系统内置用户！" ,Type.WARNING_MESSAGE);
			return;
		}
		//解除Role引用，不然无法删除
		user.clearUserRoles();
		super.removeEntity(entityId);
	}

}