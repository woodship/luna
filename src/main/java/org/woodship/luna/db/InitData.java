package org.woodship.luna.db;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.base.PersonView;
import org.woodship.luna.core.ApplicationView;
import org.woodship.luna.core.HomeView;
import org.woodship.luna.core.Resource;
import org.woodship.luna.core.ResourceType;

@Component
public class InitData {

	@Autowired
	ContainerUtils conu;

	@PersistenceContext
	private  EntityManager entityManager;
	
	@Transactional
	public void init(){
		//有数据则不再初始化
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		cq.select(cb.count(cq.from(Resource.class)));
		long size = entityManager.createQuery(cq).getSingleResult();
		if(size>0) return;
		
		//增加系统管理模块
		Resource sys = new Resource("系统管理", ResourceType.MODULE);
		entityManager.persist(sys);
		
		//建立应用
		Resource app = new Resource("应用管理", ResourceType.APPLICATION, sys, "/application", ApplicationView.class);
		entityManager.persist(app);
		
		//增加基础应用模块
		Resource base = new Resource("基础应用", ResourceType.MODULE);
		entityManager.persist(base);
		Resource person = new Resource("人员管理", ResourceType.APPLICATION, base, "/person", PersonView.class);
		entityManager.persist(person);
		Resource home = new Resource("主页", ResourceType.APPLICATION, base, "/home", HomeView.class);
		entityManager.persist(home);
	}


}
