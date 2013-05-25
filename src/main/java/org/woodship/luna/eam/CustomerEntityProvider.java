package org.woodship.luna.eam;

import org.springframework.stereotype.Component;
import org.woodship.luna.db.TransactionalEntityProvider;

@Component
public class CustomerEntityProvider  extends TransactionalEntityProvider<Customer> {
		
	    public CustomerEntityProvider() {
	        super(Customer.class);
	    }

	    
}