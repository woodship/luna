package org.woodship.luna.core.security;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

@SuppressWarnings("serial")
@Service
public class RoleService implements Serializable{
	@PersistenceContext
	private  EntityManager em;
	
	public List<Role> findAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Role> c = cb.createQuery(Role.class);
		c.from(Role.class);
		TypedQuery<Role> q = em.createQuery(c); 
		return q.getResultList();
	}
	
	public List<Role> findRoleByUsername(String username){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Role> c = cb.createQuery(Role.class);
		Root<Role> root =  c.from(Role.class);
		SetJoin<Role, User> join = root.join(Role_.users);
		Predicate p = cb.equal(join.get(User_.username), username);
		c.where(p);
		return em.createQuery(c).getResultList();
	}
}
