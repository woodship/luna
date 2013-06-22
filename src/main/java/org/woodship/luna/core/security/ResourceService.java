package org.woodship.luna.core.security;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.util.Utils;

import com.vaadin.navigator.View;

@Service
public class ResourceService implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2416370618448117352L;
	@PersistenceContext
	private  EntityManager em;
	
	/**
	 * 创建一个带增删改功能的应用
	 * @param appName
	 * @param parent
	 * @param resKey
	 * @param viewClass
	 * @return
	 */
	@Transactional
	public Resource createCUDApp(String appName,Resource parent, String resKey, Class<? extends View> viewClass){
		Resource app =  createApp( appName, parent, resKey, viewClass);
		em.persist(app);
		
		Resource add = new Resource(Utils.getAddActionId(viewClass), "新增", ResourceType.ACTION,app);
		em.persist(add);
		Resource del = new Resource(Utils.getDelActionId(viewClass), "删除", ResourceType.ACTION,app);
		em.persist(del);
		Resource edit = new Resource(Utils.getEditActionId(viewClass), "编辑", ResourceType.ACTION,app);
		em.persist(edit);
		
		return app;
	}
	
	/**
	 * 创建一个不带增删改功能的应用
	 * @param appName
	 * @param parent
	 * @param resKey
	 * @param viewClass
	 * @return
	 */
	@Transactional
	public Resource createApp(String appName,Resource parent, String resKey, Class<? extends View> viewClass){
		Resource app = new Resource(resKey, appName, ResourceType.APPLICATION, parent, "/"+resKey, viewClass);
		em.persist(app);
		return app;
	}
	
	public Resource getResByKey(String key){
		 return em.createQuery("SELECT o FROM Resource o where o.resKey = ?", Resource.class)
				  .setParameter(1, key)
				  .getSingleResult();
	}
	
	public List<Resource> findAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Resource> c = cb.createQuery(Resource.class);
		c.from(Resource.class);
		TypedQuery<Resource> q = em.createQuery(c); 
		return q.getResultList();
	}
	
	/**
	 * 创建按钮
	 * @param key
	 * @param name
	 * @param parent
	 * @return
	 */
	public Resource createAction(String key, String name ,Resource parent){
		Resource action = new Resource(key, name, ResourceType.ACTION, parent);
		em.persist(action);
		return action;
	}
}
