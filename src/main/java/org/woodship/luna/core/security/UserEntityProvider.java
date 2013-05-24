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
		User r = this.getEntity(getJPAContainer(), entityId);
		if(r.isAdmin()){
			Notification.show("禁止删除管理员！");
			return;
		}
		super.removeEntity(entityId);
	}

}