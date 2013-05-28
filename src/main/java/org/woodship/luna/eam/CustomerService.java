package org.woodship.luna.eam;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

@Service
public class CustomerService {
	
	@PersistenceContext
	private  EntityManager em;
	
	public List<Customer> findByCusNum(String cusNum){
		  if (cusNum == null) return null;
		  return em.createQuery("SELECT o FROM Customer o where o.cusNum = ?", Customer.class)
				  .setParameter(1, cusNum).getResultList();
	}
	
	
}
