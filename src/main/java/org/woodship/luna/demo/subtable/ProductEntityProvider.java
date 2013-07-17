package org.woodship.luna.demo.subtable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.woodship.luna.core.security.UserService;
import org.woodship.luna.db.TransactionalEntityProvider;

@Component
public class ProductEntityProvider  extends TransactionalEntityProvider<Product> {
	@Autowired
	UserService us;
	
	    @SuppressWarnings("serial")
		public ProductEntityProvider() {
	        super(Product.class);
	    }

	    
}