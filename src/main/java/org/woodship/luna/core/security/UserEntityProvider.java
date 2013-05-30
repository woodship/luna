package org.woodship.luna.core.security;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.db.TransactionalEntityProvider;

import com.vaadin.ui.Notification;

@Component
public class UserEntityProvider  extends TransactionalEntityProvider<User> {

	public UserEntityProvider() {
		super(User.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void removeEntity(final Object entityId) {
		User user = this.getEntity(getJPAContainer(), entityId);
		if(User.SUPER_ADMIN_USERNAME.equals(user.getUsername())){
			Notification.show("禁止删除管理员！");
			return;
		}
		super.removeEntity(entityId);
	}

}