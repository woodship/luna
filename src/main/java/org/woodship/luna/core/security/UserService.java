package org.woodship.luna.core.security;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	@PersistenceContext
	private  EntityManager em;
	
	public User findByUsername(String username) {
		  if (username == null) return null;
		  return em.createQuery("SELECT o FROM User o where o.username = ?", User.class)
				  .setParameter(1, username)
				  .getSingleResult();
	}
	
	/**
	 * 返回当前登录用户，且该用户为受控Entity
	 * @return
	 */
	public  User getCurrentUser(){
		Object username =  SecurityUtils.getSubject().getPrincipal();
		User u =findByUsername((String) username);
		return u;
	}
	
	
}
