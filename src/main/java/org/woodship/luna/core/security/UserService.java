package org.woodship.luna.core.security;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	@PersistenceContext
	private  EntityManager entityManager;
	
	public User findByUsername(String username) {
		  if (username == null) return null;
		  return entityManager.createQuery("SELECT o FROM User o where o.username = ?", User.class)
				  .setParameter(1, username)
				  .getSingleResult();
	}
}
