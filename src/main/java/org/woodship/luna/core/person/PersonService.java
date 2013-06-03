package org.woodship.luna.core.person;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Service;

@Service
public class PersonService implements Serializable{
	private static final long serialVersionUID = -3001978271148311859L;
	@PersistenceContext
	private  EntityManager em;
	
	public List<Person> findByWorkNum(String workNum){
		  if (workNum == null) return null;
		  return em.createQuery("SELECT o FROM Person o where o.workNum = ?", Person.class)
				  .setParameter(1, workNum).getResultList();
	}

	/**
	 * 获得指定部门下的所有人员，包含子部门
	 * @param org
	 * @return
	 */
	public List<Person> getOrgPerson(Organization org) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Person> query = cb.createQuery(Person.class);
		Root<Person> from = query.from(Person.class);
		query.select(from);
		
		Subquery<Organization> subquery = query.subquery(Organization.class);
		Root<Organization> subFrom = subquery.from(Organization.class);
		subquery.select(subFrom);
		ListJoin<Organization, Organization> join = subFrom.join(Organization_.ancestors);
		Predicate p = cb.equal(join, org);
		subquery.where(p);
		
		Path<Organization> orgpath = from.get(Person_.org);
		query.where(cb.in(orgpath).value(subquery));
		
		return em.createQuery(query).getResultList();
	}
	
	
}
