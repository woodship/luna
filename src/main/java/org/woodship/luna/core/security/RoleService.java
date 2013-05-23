package org.woodship.luna.core.security;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Service;

@Service
public class RoleService {
	@PersistenceContext
	private  EntityManager em;
	
	public List<Role> findAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Role> c = cb.createQuery(Role.class);
		c.from(Role.class);
		TypedQuery<Role> q = em.createQuery(c); 
		return q.getResultList();
	}
}
