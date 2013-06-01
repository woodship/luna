package org.woodship.luna.core.person;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
	
	
}
