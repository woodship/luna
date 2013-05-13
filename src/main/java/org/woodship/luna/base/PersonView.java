package org.woodship.luna.base;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.woodship.luna.db.ContainerUtils;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
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
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
public class PersonView extends HorizontalSplitPanel implements ComponentContainer, View{
	@Autowired
	ContainerUtils conu;
	
	@Autowired()
	@Qualifier("personEntityProvider")
	EntityProvider<Person> personProvider;
	
	@PersistenceContext
	private  EntityManager entityManager;
	
    private Tree groupTree;

    private Table personTable;

    private TextField searchField;

    private Button newButton;
    private Button deleteButton;
    private Button editButton;

    private JPAContainer<Department> departments;
    private JPAContainer<Person> persons;

    private Department departmentFilter;
    private String textFilter;

    @PostConstruct
	public void PostConstruct(){
        departments = conu.createJPAHierarchialContainer(Department.class);
        persons = new JPAContainer<Person>(Person.class);
       persons.setEntityProvider(personProvider);
        
        persons.getEntityProvider();
        buildTree();
        buildMainArea();

        setSplitPosition(20);
    }

    private void buildMainArea() {
    	//右侧
        VerticalLayout verticalLayout = new VerticalLayout();
        setSecondComponent(verticalLayout);

        
        personTable = new Table(null, persons);
        personTable.setSelectable(true);
        personTable.setImmediate(true);
        personTable.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                setModificationsEnabled(event.getProperty().getValue() != null);
            }

            private void setModificationsEnabled(boolean b) {
                deleteButton.setEnabled(b);
                editButton.setEnabled(b);
            }
        });

        personTable.setSizeFull();
        // personTable.setSelectable(true);
        personTable.addItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    personTable.select(event.getItemId());
                }
            }
        });

        personTable.setVisibleColumns(new Object[] { "firstName", "lastName",
                "department", "phoneNumber", "street", "city", "zipCode" });

        HorizontalLayout toolbar = new HorizontalLayout();
        newButton = new Button("Add");
        newButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                final EntityItem<Person> newPersonItem = persons.createEntityItem(new Person());
                PersonEditor personEditor = new PersonEditor(newPersonItem,persons);
                UI.getCurrent().addWindow(personEditor);
            }
        });

        deleteButton = new Button("Delete");
        deleteButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
            	persons.removeItem(personTable.getValue());
            }
        });
        deleteButton.setEnabled(false);

        editButton = new Button("Edit");
        editButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                UI.getCurrent().addWindow(
                        new PersonEditor(personTable.getItem(personTable.getValue()),persons));
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

        verticalLayout.addComponent(toolbar);
        verticalLayout.addComponent(personTable);
        verticalLayout.setExpandRatio(personTable, 1);
        verticalLayout.setSizeFull();

    }

    private void buildTree() {
        groupTree = new Tree(null, departments);
        groupTree.setItemCaptionPropertyId("name");

        groupTree.setImmediate(true);
        groupTree.setSelectable(true);
        groupTree.addItemClickListener(new ItemClickListener() {
			
			@Override
			public void itemClick(ItemClickEvent event) {
				Object id = event.getItemId();
                if (id != null) {
                    Department entity = departments.getItem(id).getEntity();
                    departmentFilter = entity;
                } else if (departmentFilter != null) {
                    departmentFilter = null;
                }
                updateFilters();
				
			}
		});
        setFirstComponent(groupTree);
    }

    private void updateFilters() {
        persons.setApplyFiltersImmediately(false);
        persons.removeAllContainerFilters();
        if (departmentFilter != null) {
            // two level hierarchy at max in our demo
            if (departmentFilter.getParent() == null) {
                persons.addContainerFilter(new Equal("department.parent",
                        departmentFilter));
            } else {
                persons.addContainerFilter(new Equal("department",
                        departmentFilter));
            }
        }
        if (textFilter != null && !textFilter.equals("")) {
            Or or = new Or(new Like("firstName", textFilter + "%", false),
                    new Like("lastName", textFilter + "%", false));
            persons.addContainerFilter(or);
        }
        persons.applyFilters();
    }

	@Override
	public void enter(ViewChangeEvent event) {
	}
	
	
}
