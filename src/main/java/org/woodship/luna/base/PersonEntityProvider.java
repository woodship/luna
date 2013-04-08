package org.woodship.luna.base;

import org.springframework.stereotype.Component;
import org.woodship.luna.db.TransactionalEntityProvider;

@Component
public class PersonEntityProvider  extends TransactionalEntityProvider<Person> {
	
	    public PersonEntityProvider() {
	        super(Person.class);
	    }

	    
}