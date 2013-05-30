package org.woodship.luna.core.person;

import org.springframework.stereotype.Component;
import org.woodship.luna.db.TransactionalEntityProvider;

@Component
public class OrganizationEntityProvider  extends TransactionalEntityProvider<Organization> {
		
	    public OrganizationEntityProvider() {
	        super(Organization.class);
	    }

	    
}