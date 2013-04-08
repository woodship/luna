package org.woodship.luna.core;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.woodship.luna.db.ContainerUtils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

@Component
@Scope("prototype")
public class ApplicationView extends HorizontalLayout implements View {
	private static final long serialVersionUID = 1L;

	@Autowired
	ContainerUtils conu;

	private JPAContainer<Resource> resContainer;
	private Table table;
	@PostConstruct
	public void PostConstruct(){
		resContainer = conu.createJPAContainer(Resource.class);
		table = new Table("应用管理");
		table.setContainerDataSource(resContainer);
		this.addComponent(table);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
