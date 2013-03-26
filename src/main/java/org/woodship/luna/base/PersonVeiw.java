package org.woodship.luna.base;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class PersonVeiw extends HorizontalLayout implements View {
	private static final long serialVersionUID = 1L;

	
	public PersonVeiw() {
		super();
		
		this.addComponent(new Label("实现人员增删改查"));
	}


	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
