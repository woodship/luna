package org.woodship.luna.db;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.provider.MutableLocalEntityProvider;

@Component
public abstract class TransactionalEntityProvider<T> extends MutableLocalEntityProvider<T> {

    @PersistenceContext
    private EntityManager entityManager;
    
    private Class<T> entityClass;
    
    public TransactionalEntityProvider(Class<T> entityClass) {
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
    protected void runInTransaction(Runnable operation) {
        super.runInTransaction(operation);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public T updateEntity(final T entity) {
        return super.updateEntity(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public T addEntity(final T entity) {
        return super.addEntity(entity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeEntity(final Object entityId) {
        super.removeEntity(entityId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateEntityProperty(final Object entityId, final String propertyName, final Object propertyValue)
            throws IllegalArgumentException {
        super.updateEntityProperty(entityId, propertyName, propertyValue);
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