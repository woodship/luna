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
import com.vaadin.addon.jpacontainer.JPAContainerItem;
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
public class OrganizationView extends HorizontalSplitPanel implements ComponentContainer, View{
	public static final String NAME = "organization";
	
	@PersistenceContext
	private  EntityManager entityManager;
	
    private Tree groupTree;

    private Table mainTable;

    private TextField searchField;

    private Button newButton;
    private Button deleteButton;
    private Button editButton;

    private JPAContainer<Organization> treeContainer;
    private JPAContainer<Organization> tableContainer;

    private Organization treeFilter;
    private String textFilter;

    @PostConstruct
	public void PostConstruct(){
        tableContainer = Utils.getJPAContainer(Organization.class);
        treeContainer = Utils.getHierarchialJPAContainer(Organization.class);
        
        buildTree();
        buildMainArea();

        setSplitPosition(20);
        
        authenticate();
    }


	private void buildMainArea() {
    	//右侧
        VerticalLayout verticalLayout = new VerticalLayout();
        setSecondComponent(verticalLayout);
        
        mainTable = new Table(null, tableContainer);
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
        // organizationTable.setSelectable(true);
        mainTable.addItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    mainTable.select(event.getItemId());
                }
            }
        });

        Utils.setTableDefaultHead(mainTable, Organization.class);
        

        HorizontalLayout toolbar = new HorizontalLayout();
        newButton = new Button("增加");
        newButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final EntityItem<Organization> newOrganizationItem = tableContainer.createEntityItem(new Organization());
                if(treeFilter != null)
                	newOrganizationItem.getEntity().setParent(treeFilter);
                OrganizationEditor organizationEditor = new OrganizationEditor((JPAContainerItem<Organization>) newOrganizationItem,
                		tableContainer);
                organizationEditor.center();
                UI.getCurrent().addWindow(organizationEditor);
            }
        });

        deleteButton = new Button("删除");
        deleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ConfirmDialog.show(UI.getCurrent(),"警告","确定要删除吗？删除后将不能恢复！","是","否",
           			 new ConfirmDialog.Listener() {
					@Override
					public void onClose(ConfirmDialog dialog) {
						if(dialog.isConfirmed()){
							tableContainer.removeItem(mainTable.getValue());
						}
					}
				});
            }
        });
        deleteButton.setEnabled(false);

        editButton = new Button("编辑");
        editButton.addClickListener(new Button.ClickListener() {
            @SuppressWarnings("unchecked")
			@Override
            public void buttonClick(ClickEvent event) {
            	OrganizationEditor pe = new OrganizationEditor(
            			(JPAContainerItem<Organization>) mainTable.getItem(mainTable.getValue()),
            			tableContainer);
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

        verticalLayout.addComponent(toolbar);
        verticalLayout.addComponent(mainTable);
        verticalLayout.setExpandRatio(mainTable, 1);
        verticalLayout.setSizeFull();

    }

    private void buildTree() {
        groupTree = new Tree(null, treeContainer);
        groupTree.setItemCaptionPropertyId("name");

        groupTree.setImmediate(true);
        groupTree.setSelectable(true);
        for(Object id : groupTree.getItemIds()){
        	groupTree.expandItem(id);
        }
        groupTree.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				Object id = event.getItemId();
                if (id != null) {
                    Organization entity = treeContainer.getItem(id).getEntity();
                    treeFilter = entity;
                } else if (treeFilter != null) {
                    treeFilter = null;
                }
                updateFilters();
				
			}
		});
        setFirstComponent(groupTree);
    }

    private void updateFilters() {
        tableContainer.setApplyFiltersImmediately(false);
        tableContainer.removeAllContainerFilters();
        if (treeFilter != null) {
            //TODO 多级查询
            if (treeFilter.getParent() != null) {
            	Equal ea = new Equal("id",treeFilter.getId());
            	Equal eb = new Equal("parent",treeFilter);
            	Equal ec = new Equal("parent.parent",treeFilter);
            	Or or = new Or(ea,eb,ec);
                tableContainer.addContainerFilter(or);
            } 
        }
        if (textFilter != null && !textFilter.equals("")) {
            Like like =new Like("name", textFilter + "%", false);
            tableContainer.addContainerFilter(like);
        }
        tableContainer.applyFilters();
    }

	@Override
	public void enter(ViewChangeEvent event) {
	}
	
    private void authenticate() {
    	Subject user = SecurityUtils.getSubject(); 
		newButton.setVisible(user.isPermitted(Utils.getAddActionId(OrganizationView.class)));
		deleteButton.setVisible(user.isPermitted(Utils.getDelActionId(OrganizationView.class)));
		editButton.setVisible(user.isPermitted(Utils.getEditActionId(OrganizationView.class)));
	}
}
