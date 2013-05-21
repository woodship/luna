package org.woodship.luna.core.security;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.db.TransactionalEntityProvider;

import com.vaadin.ui.Notification;

@Component
public class RoleEntityProvider  extends TransactionalEntityProvider<Role> {

	public RoleEntityProvider() {
		super(Role.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void removeEntity(final Object entityId) {
		Role r = this.getEntity(getJPAContainer(), entityId);
		if(r.isSysRole()){
			Notification.show("系统内置角色，禁止删除！");
			return;
		}
		super.removeEntity(entityId);
	}

}