package org.woodship.luna.core.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.core.security.UserService;
import org.woodship.luna.db.TransactionalEntityProvider;

@Component
public class PersonEntityProvider  extends TransactionalEntityProvider<Person> {
	
		@Autowired
		UserService us;
		
	    public PersonEntityProvider() {
	        super(Person.class);
	    }

		@Override
		@Transactional(propagation = Propagation.REQUIRED)
		public Person addEntity(Person entity) {
			//增加人员同时增加用户
			Person p =  super.addEntity(entity);
			us.createUserFromPerson(p);
			return p;
		}

	    
}