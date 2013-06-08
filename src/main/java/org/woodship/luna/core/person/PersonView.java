package org.woodship.luna.core.person;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.vaadin.dialogs.ConfirmDialog;
import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
public class PersonView extends HorizontalSplitPanel implements ComponentContainer, View{
	public static final String NAME = "person";
	
	@PersistenceContext
	private  EntityManager entityManager;
	
    private Tree groupTree;

    private Table mainTable;

    private TextField searchField;

    private Button newButton;
    private Button deleteButton;
    private Button editButton;

    private JPAContainer<Organization> departments;
    private JPAContainer<Person> persons;

    private Organization treeFilter;
    private String textFilter;

    @PostConstruct
	public void PostConstruct(){
    	this.setSizeFull();
        departments = Utils.getHierarchialJPAContainer(Organization.class);
        persons =  Utils.getJPAContainer(Person.class);
        
        persons.getEntityProvider();
        buildTree();
        buildMainArea();

        setSplitPosition(20);
        
        authenticate();
    }


	private void buildMainArea() {
    	//右侧
        VerticalLayout verticalLayout = new VerticalLayout();
        setSecondComponent(verticalLayout);

        
        mainTable = new Table(null, persons);
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

        Utils.configTableHead(mainTable, Person.class);
        

        HorizontalLayout toolbar = new HorizontalLayout();
        newButton = new Button("增加");
        newButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                final EntityItem<Person> newPersonItem = persons.createEntityItem(new Person());
                if(treeFilter != null)
                	newPersonItem.getEntity().setOrg(treeFilter);
                PersonEditor personEditor = new PersonEditor(newPersonItem,persons);
                personEditor.center();
                UI.getCurrent().addWindow(personEditor);
            }
        });

        deleteButton = new Button("删除");
        deleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ConfirmDialog.show(UI.getCurrent(),"警告"
            			,"确定要删除吗？如果该人员关联有非系统用户，也将被删除，删除后将不能恢复！","是","否",
           			 new ConfirmDialog.Listener() {
					@Override
					public void onClose(ConfirmDialog dialog) {
						if(dialog.isConfirmed()){
							persons.removeItem(mainTable.getValue());
						}
					}
				});
            }
        });
        deleteButton.setEnabled(false);

        editButton = new Button("编辑");
        editButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
            	PersonEditor pe = new PersonEditor(mainTable.getItem(mainTable.getValue()),persons);
            	pe.center();
                UI.getCurrent().addWindow(pe);
            }
        });
        editButton.setEnabled(false);

        searchField = new TextField();
        searchField.setInputPrompt("姓名/工号");
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

        verticalLayout.addComponent(toolbar);
        verticalLayout.addComponent(mainTable);
        verticalLayout.setExpandRatio(mainTable, 1);
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
                    Organization entity = departments.getItem(id).getEntity();
                    treeFilter = entity;
                } else if (treeFilter != null) {
                    treeFilter = null;
                }
                updateFilters();
				
			}
		});
        //展开所有节点
        for(Object id : groupTree.getItemIds()){
        	groupTree.expandItem(id);
        }
        setFirstComponent(groupTree);
    }

    private void updateFilters() {
        persons.setApplyFiltersImmediately(false);
        persons.removeAllContainerFilters();
        if (treeFilter != null) {
        	//TODO 更多级的级联
            if (treeFilter.getParent() != null) {
            	Equal ea = new Equal("org",treeFilter);
            	Equal eb = new Equal("org.parent",treeFilter);
            	Equal ec = new Equal("org.parent.parent",treeFilter);
            	Or or = new Or(ea,eb,ec);
            	persons.addContainerFilter(or);
            } 
        }
        if (textFilter != null && !textFilter.equals("")) {
            Like likea =new Like("trueName","%"+ textFilter + "%", false);
            Like likeb =new Like("workNum","%"+ textFilter + "%", false);
            Or or = new Or(likea,likeb);
            persons.addContainerFilter(or);
        }
        persons.applyFilters();
    }

	@Override
	public void enter(ViewChangeEvent event) {
	}
	
    private void authenticate() {
    	Subject user = SecurityUtils.getSubject(); 
		newButton.setVisible(user.isPermitted(Utils.getAddActionId(PersonView.class)));
		deleteButton.setVisible(user.isPermitted(Utils.getDelActionId(PersonView.class)));
		editButton.setVisible(user.isPermitted(Utils.getEditActionId(PersonView.class)));
	}
}
