package org.woodship.luna.core.security;

import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;
import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

@Component
@Scope("prototype")
public class ApplicationView extends VerticalLayout implements View {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "application";

	@Autowired
	DataSource ds ;
	
	@PostConstruct
	public void PostConstruct(){
	    this.setSizeFull();
	    JPAContainer<Resource> resContainer =  Utils.getHierarchialJPAContainer(Resource.class);
		TreeTable ttable = new TreeTable();
		ttable.setContainerDataSource(resContainer);
		ttable.setItemCaptionPropertyId("name");
		Utils.configTableHead(ttable, Resource.class);
		ttable.setSizeFull();
		ttable.setSelectable(true);
		//展开节点
		for (Object itemId: ttable.getItemIds()){
			ttable.setCollapsed(itemId, false);
		}
		
		Button startupDB = new Button("管理数据库");
		startupDB.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1759069585352617475L;
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					org.h2.tools.Server.startWebServer(DataSourceUtils.getConnection(ds));
				} catch (CannotGetJdbcConnectionException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setMargin(true);
		buttons.addComponent(startupDB);
		
		this.addComponent(buttons);
		this.addComponent(ttable);
		this.setExpandRatio(ttable, 1);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
