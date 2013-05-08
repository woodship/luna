package org.woodship.luna.db;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.SpringApplicationContext;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;

@Component
public class ContainerUtils {
	
	
	@Autowired
	private EntityProviderUtil entityProviderUtil;
	
    public static String CAPTION_PROPERTY_NAME = "caption";
    
    public  Container createContainerFromMap(Map<?, String> hashMap) {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(CAPTION_PROPERTY_NAME, String.class, "");
        
        Iterator<?> iter = hashMap.keySet().iterator();
        while(iter.hasNext()) {
            Object itemId = iter.next();
            container.addItem(itemId);
            container.getItem(itemId).getItemProperty(CAPTION_PROPERTY_NAME).setValue(hashMap.get(itemId));
        }
        
        return container;
    }
    
    public  Container createEnumContainer(Class<? extends Enum<?>> enumClass) {
        LinkedHashMap<Enum<?>, String> enumMap = new LinkedHashMap<Enum<?>, String>();
        for (Object enumConstant : enumClass.getEnumConstants()) {
            enumMap.put((Enum<?>) enumConstant, enumConstant.toString());
        }
        return createContainerFromMap(enumMap);
    }
    
    
    public  <E extends IdEntity<E>>  JPAContainer<E> createJPAContainer(Class<E> c) {
		JPAContainer<E> container = new JPAContainer<E>(c);
		container.setEntityProvider(entityProviderUtil.getEntityProvider(c));
		return container;
	}
    
    public  <E extends IdEntity<E>>  JPAContainer<E> createJPABatchableContainer(Class<E> c) {
    	JPAContainer<E> container = new JPAContainer<E>(c);
    	container.setEntityProvider(entityProviderUtil.getBatchableEntityProvider(c));
    	return container;
    }
    
    public  <E extends HierarchialEntity<E>> JPAContainer<E> createJPAHierarchialContainer(Class<E> c) {
    	class HJPAContainer<H> extends JPAContainer<E>{
    		private static final long serialVersionUID = 1L;

			public HJPAContainer(Class<E> entityClass) {
				super(entityClass);
				setEntityProvider(entityProviderUtil.getEntityProvider(entityClass));
				setParentProperty("parent");
			}

			@Override
			public boolean areChildrenAllowed(Object itemId) {
				return super.areChildrenAllowed(itemId)
		                && !getItem(itemId).getEntity().isLeaf();
			}
    	   
    	 }
    	return new HJPAContainer<E>(c);
    }
    
    public static  ContainerUtils getInstance(){
    	Object bean = SpringApplicationContext.getApplicationContext().getBean(ContainerUtils.class);
    	return (ContainerUtils)bean;
    }
    
       
}