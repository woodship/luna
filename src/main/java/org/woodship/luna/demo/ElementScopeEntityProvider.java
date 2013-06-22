package org.woodship.luna.demo;

import org.springframework.stereotype.Component;
import org.woodship.luna.db.BatchUpdateEntityProvider;

@Component
public class ElementScopeEntityProvider  extends BatchUpdateEntityProvider<ElementScope> {
		
	    public ElementScopeEntityProvider() {
	        super(ElementScope.class);
	    }

	    
}