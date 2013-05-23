package org.woodship.luna.core.security;

import org.springframework.stereotype.Component;
import org.woodship.luna.db.TransactionalEntityProvider;

@Component
public class ResourceEntityProvider  extends TransactionalEntityProvider<Resource> {
	
	    public ResourceEntityProvider() {
	        super(Resource.class);
	    }
	    
}