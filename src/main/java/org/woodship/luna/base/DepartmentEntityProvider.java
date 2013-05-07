package org.woodship.luna.base;

import org.springframework.stereotype.Component;
import org.woodship.luna.db.TransactionalEntityProvider;

@Component
public class DepartmentEntityProvider  extends TransactionalEntityProvider<Department> {
	
	    public DepartmentEntityProvider() {
	        super(Department.class);
	    }

	    
}