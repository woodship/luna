package org.woodship.luna.eam;

import org.springframework.stereotype.Component;
import org.woodship.luna.db.TransactionalEntityProvider;

@Component
public class InvItemEntityProvider  extends TransactionalEntityProvider<InvItem> {
		
	    public InvItemEntityProvider() {
	        super(InvItem.class);
	    }

	    
}