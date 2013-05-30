package org.woodship.luna.core.security;

import java.util.Set;

import org.apache.shiro.realm.Realm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.LunaException;
import org.woodship.luna.db.TransactionalEntityProvider;

@Component
public class RoleEntityProvider  extends TransactionalEntityProvider<Role> {
	@Autowired
	Realm realm;
	
	public RoleEntityProvider() {
		super(Role.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void removeEntity(final Object entityId) {
		Role r = this.getEntity(getJPAContainer(), entityId);
		if(r.isSysRole()){
			throw new LunaException("系统内置角色，禁止删除！");
		}
		super.removeEntity(entityId);
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	protected void runInTransaction(Runnable operation) {
		//更新角色时更新权限缓存
		((ShiroDbRealm)realm).clearAllCachedAuthorizationInfo();
		super.runInTransaction(operation);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateEntityProperty(Object entityId, String propertyName,
			Object propertyValue) throws IllegalArgumentException {
		//超级管理员禁止从系统管理员角色中删除
		if(propertyName.equals(Role_.users.getName())){
			Role role = this.getEntityManager().find(Role.class, entityId);
			if(Role.SUPER_ADMIN_ROLE_NAME.equals(role.getName())){
				Set<User> users = (Set<User>) propertyValue;
				boolean removedAdmin = true;
				for(User user : users){
					if(User.SUPER_ADMIN_USERNAME.equals(user.getUsername())){
						removedAdmin = false;
					}
				}
				if(removedAdmin){
					throw new LunaException("管理员禁止移除");
				}
			}
		}

		super.updateEntityProperty(entityId, propertyName, propertyValue);
	}

	
	

}