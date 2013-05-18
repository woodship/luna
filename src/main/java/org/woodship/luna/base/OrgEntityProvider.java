package org.woodship.luna.base;

import org.springframework.stereotype.Component;
import org.woodship.luna.db.TransactionalEntityProvider;

@Component
public class OrgEntityProvider  extends TransactionalEntityProvider<Organization> {
	
	    public OrgEntityProvider() {
	        super(Organization.class);
	    }

	    
}