package org.woodship.luna.eam;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.db.TransactionalEntityProvider;

import com.vaadin.ui.Notification;

@Component
public class InvItemEntityProvider  extends TransactionalEntityProvider<InvItem> {
		
	    public InvItemEntityProvider() {
	        super(InvItem.class);
	    }

		@Override
		@Transactional(propagation = Propagation.REQUIRED)
		public void removeEntity(Object entityId) {
			try {
				super.removeEntity(entityId);
			} catch (Exception e) {
				e.printStackTrace();
				Notification.show("删除失败，该记录可能已经被引用！");
			}
		}

	    
}