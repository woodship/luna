package org.woodship.luna.core.security;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TreeTable;

@Component
@Scope("prototype")
public class ApplicationView extends HorizontalLayout implements View {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "application";

	@PostConstruct
	public void PostConstruct(){
	    this.setSizeFull();
	    JPAContainer<Resource> resContainer =  Utils.getHierarchialJPAContainer(Resource.class);
		TreeTable ttable = new TreeTable("应用管理");
		ttable.setContainerDataSource(resContainer);
		ttable.setItemCaptionPropertyId("name");
		Utils.configTableHead(ttable, Resource.class);
		ttable.setSizeFull();
		ttable.setSelectable(true);
		//展开节点
		for (Object itemId: ttable.getItemIds()){
			ttable.setCollapsed(itemId, false);
		}
		this.addComponent(ttable);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
