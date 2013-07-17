package org.woodship.luna.demo.simpleview;

import org.springframework.stereotype.Component;
import org.woodship.luna.db.TransactionalEntityProvider;

@Component
public class VendorEntityProvider  extends TransactionalEntityProvider<Vendor> {
	
		public VendorEntityProvider() {
	        super(Vendor.class);
	    }

}