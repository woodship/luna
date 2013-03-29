package org.woodship.luna.core;

import java.awt.Button;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.db.ContainerUtils;
import org.woodship.luna.spring.VaadinView;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

@Component
@VaadinView("application")
@Scope("prototype")
public class ApplicationVeiw extends HorizontalLayout implements View {
	private static final long serialVersionUID = 1L;

	@Autowired
	ContainerUtils conu;

	private JPAContainer<Resource> resContainer;

	@PostConstruct
	public void PostConstruct(){
		resContainer = conu.createJPAContainer(Resource.class);
		Table table = new Table("应用管理");
		table.setContainerDataSource(resContainer);
		//    		table.setEditable(true);
		this.addComponent(table);
		Button add = new Button("add");
	}


	@Override
	@Transactional
	public void enter(ViewChangeEvent event) {
		if(resContainer.size() == 0){
			for(Resource e : Resource.getDemoResoures()){
				resContainer.addEntity(e);
			}
		}
	}

}
