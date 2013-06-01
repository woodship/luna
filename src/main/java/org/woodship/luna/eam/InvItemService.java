package org.woodship.luna.eam;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

@Service
public class InvItemService implements Serializable{
	private static final long serialVersionUID = -7294753691171907896L;
	@PersistenceContext
	private  EntityManager em;
	
	public List<InvItem> findByModel(String model){
		  if (model == null) return null;
		  return em.createQuery("SELECT o FROM InvItem o where o.model = ?", InvItem.class)
				  .setParameter(1, model).getResultList();
	}
	
	
}
