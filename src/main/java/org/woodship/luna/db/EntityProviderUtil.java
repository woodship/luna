package org.woodship.luna.db;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.addon.jpacontainer.provider.BatchableLocalEntityProvider;
import com.vaadin.addon.jpacontainer.provider.CachingMutableLocalEntityProvider;
import com.vaadin.addon.jpacontainer.util.HibernateLazyLoadingDelegate;

/**
 * Utility class for constructing/obtaining a shared entity provider instance for an entity class.
 */
@Component
@Scope("prototype")
public class EntityProviderUtil {
	@PersistenceContext
	@Transient
	private  EntityManager entityManager;
	
    private static Map<Class<?>, CachingMutableLocalEntityProvider<?>> providerMap = new HashMap<Class<?>, CachingMutableLocalEntityProvider<?>>();
    private static Map<Class<?>, BatchableLocalEntityProvider<?>> batchableProviderMap = new HashMap<Class<?>, BatchableLocalEntityProvider<?>>();
   
    @SuppressWarnings("unchecked")
    public <T extends IdEntity<T>>  CachingMutableLocalEntityProvider<T> getEntityProvider(Class<T> cls) {
        synchronized (providerMap) {
            if (!providerMap.containsKey(cls)) {
            	CachingMutableLocalEntityProvider<?> provider = buildEntityProvider(cls);
                if (null != provider) {
                    providerMap.put(cls, provider);
                }
            }
            return (CachingMutableLocalEntityProvider<T>) providerMap.get(cls);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T extends IdEntity<T>> BatchableLocalEntityProvider<T> getBatchableEntityProvider(Class<T> cls) {
    	synchronized (batchableProviderMap) {
    		if (!batchableProviderMap.containsKey(cls)) {
    			BatchableLocalEntityProvider<?> provider = buildBatchableEntityProvider(cls);
    			if (null != provider) {
    				batchableProviderMap.put(cls, provider);
    			}
    		}
    		return (BatchableLocalEntityProvider<T>) batchableProviderMap.get(cls);
    	}
    }
    

    private <T extends IdEntity<T>>  CachingMutableLocalEntityProvider<T> buildEntityProvider(Class<T> cls) {
        if (null != entityManager) {
        	CachingMutableLocalEntityProvider<T> provider = new CachingMutableLocalEntityProvider<T>(cls, entityManager);
            // Spring should manage transactions - writable
            provider.setTransactionsHandledByProvider(false);
            //
            HibernateLazyLoadingDelegate hlld = new HibernateLazyLoadingDelegate();
        	provider.setLazyLoadingDelegate(hlld);
            
            return provider;
        } else {
            return null;
        }
    }
    private <T extends IdEntity<T>> BatchableLocalEntityProvider<T> buildBatchableEntityProvider(Class<T> cls) {
    	if (null != entityManager) {
    		BatchableLocalEntityProvider<T> provider = new BatchableLocalEntityProvider<T>(cls, entityManager);
    		// Spring should manage transactions - writable
    		provider.setTransactionsHandledByProvider(false);
    		return provider;
    	} else {
    		return null;
    	}
    }
    
}
