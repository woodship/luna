package org.woodship.luna.base;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

@Service
public class PersonService {
	
	@PersistenceContext
	private  EntityManager em;
	
	public List<Person> findByWorkNum(String workNum){
		  if (workNum == null) return null;
		  return em.createQuery("SELECT o FROM Person o where o.workNum = ?", Person.class)
				  .setParameter(1, workNum).getResultList();
				  
	}
	
	
}
