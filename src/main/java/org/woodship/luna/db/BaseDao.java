package org.woodship.luna.db;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.base.Person;

import com.vaadin.addon.jpacontainer.JPAContainer;

@Component
public class BaseDao {
	@PersistenceContext
	private  EntityManager entityManager;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional
	public void deleteEntity(Class clazz,  Object id){
		Person person = entityManager.find(clazz, id);
		entityManager.remove(person);
	}
	@Transactional
	public <T> void deleteEntity(JPAContainer<T> container,  Object id){
		container.removeItem(id);
	}
}
