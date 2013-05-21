package org.woodship.luna.core.security;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.woodship.luna.db.ContainerUtils;
import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.Like;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
public class RoleView extends VerticalLayout implements ComponentContainer, View{
	@Autowired
	ContainerUtils conu;
	
	@Autowired()
	@Qualifier("roleEntityProvider")
	EntityProvider<Role> mainProvider;
	
	@PersistenceContext
	private  EntityManager entityManager;

    private Table mainTable;

    private TextField searchField;

    private Button newButton;
    private Button deleteButton;
    private Button editButton;

    private JPAContainer<Role> mainContainer;

    private String textFilter;

    @PostConstruct
	public void PostConstruct(){
        mainContainer = new JPAContainer<Role>(Role.class);
        mainContainer.setEntityProvider(mainProvider);
        
        mainContainer.getEntityProvider();
        buildMainArea();

    }

    private void buildMainArea() {
    	//右侧
        
        mainTable = new Table(null, mainContainer);
        mainTable.setSelectable(true);
        mainTable.setImmediate(true);
        mainTable.setRowHeaderMode(RowHeaderMode.INDEX);
//     mainTable.setColumnWidth(null, 24);//设置序号列宽度
        mainTable.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                setModificationsEnabled(event.getProperty().getValue() != null);
            }

            private void setModificationsEnabled(boolean b) {
                deleteButton.setEnabled(b);
                editButton.setEnabled(b);
            }
        });

        mainTable.setSizeFull();
        // personTable.setSelectable(true);
        mainTable.addItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    mainTable.select(event.getItemId());
                }
            }
        });

        Utils.configTableHead(mainTable, Role.class);
        

        HorizontalLayout toolbar = new HorizontalLayout();
        newButton = new Button("增加");
        newButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                final EntityItem<Role> newRoleItem = mainContainer.createEntityItem(new Role());
                RoleEditor personEditor = new RoleEditor(newRoleItem,mainContainer);
                personEditor.center();
                UI.getCurrent().addWindow(personEditor);
            }
        });

        deleteButton = new Button("删除");
        deleteButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
            	mainContainer.removeItem(mainTable.getValue());
            }
        });
        deleteButton.setEnabled(false);

        editButton = new Button("编辑");
        editButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
            	RoleEditor pe = new RoleEditor(mainTable.getItem(mainTable.getValue()),mainContainer);
            	pe.center();
                UI.getCurrent().addWindow(pe);
            }
        });
        editButton.setEnabled(false);

        searchField = new TextField();
        searchField.setInputPrompt("Search by name");
        searchField.addTextChangeListener(new TextChangeListener() {

            @Override
            public void textChange(TextChangeEvent event) {
                textFilter = event.getText();
                updateFilters();
            }
        });

        toolbar.addComponent(newButton);
        toolbar.addComponent(deleteButton);
        toolbar.addComponent(editButton);
        toolbar.addComponent(searchField);
        toolbar.setWidth("100%");
        toolbar.setExpandRatio(searchField, 1);
        toolbar.setComponentAlignment(searchField, Alignment.TOP_RIGHT);
        toolbar.setMargin(true);

        addComponent(toolbar);
        addComponent(mainTable);
        setExpandRatio(mainTable, 1);
        setSizeFull();

    }

    private void updateFilters() {
        mainContainer.setApplyFiltersImmediately(false);
        mainContainer.removeAllContainerFilters();
        if (textFilter != null && !textFilter.equals("")) {
            Like like =new Like("trueName", textFilter + "%", false);
            mainContainer.addContainerFilter(like);
        }
        mainContainer.applyFilters();
    }

	@Override
	public void enter(ViewChangeEvent event) {
	}
	
	
}
