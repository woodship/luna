package org.woodship.luna.core;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.woodship.luna.db.ContainerUtils;
import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TreeTable;

@Component
@Scope("prototype")
public class ApplicationView extends HorizontalLayout implements View {
	private static final long serialVersionUID = 1L;

	@Autowired
	ContainerUtils conu;

	private JPAContainer<Resource> resContainer;
	private TreeTable table;
	@PostConstruct
	public void PostConstruct(){
	    this.setSizeFull();
		resContainer = conu.createJPAHierarchialContainer(Resource.class);
		table = new TreeTable("应用管理");
		table.setContainerDataSource(resContainer);
		table.setItemCaptionPropertyId("name");
		Utils.setTableDefaultHead(table, Resource.class);
		table.setSizeFull();
		this.addComponent(table);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
