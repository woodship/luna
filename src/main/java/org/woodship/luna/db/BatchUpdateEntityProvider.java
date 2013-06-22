package org.woodship.luna.db;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.provider.BatchableLocalEntityProvider;

@Component
public abstract class BatchUpdateEntityProvider<T> extends BatchableLocalEntityProvider<T> {

    @PersistenceContext
    private EntityManager entityManager;
    
    private Class<T> entityClass;
    
    public BatchUpdateEntityProvider(Class<T> entityClass) {
        super(entityClass);
        this.entityClass = entityClass;
        setTransactionsHandledByProvider(false);
    }

    @PostConstruct
    public void init() {
        setEntityManager(entityManager);
        /*
         * The entity manager is transaction scoped, which means that the entities will be automatically detached when the transaction is
         * closed. Therefore, we do not need to explicitly detach them.
         */
        setEntitiesDetached(false);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
	public void batchUpdate(BatchUpdateCallback<T> callback)
			throws UnsupportedOperationException {
		super.batchUpdate(callback);
	}

	public JPAContainer<T> getJPAContainer(){
    	JPAContainer<T> container = new JPAContainer<T>(entityClass);
    	container.setEntityProvider(this);
    	return container;
    }

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	
    
}