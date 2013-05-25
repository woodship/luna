package org.woodship.luna.eam;

import org.springframework.stereotype.Component;
import org.woodship.luna.db.TransactionalEntityProvider;

@Component
public class ProductEntityProvider  extends TransactionalEntityProvider<Product> {
		
	    public ProductEntityProvider() {
	        super(Product.class);
	    }

	    
}