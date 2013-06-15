package org.woodship.luna.eam;

import javax.annotation.PostConstruct;

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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
public class CustomerView extends VerticalLayout implements ComponentContainer, View{
	public static final String NAME = "customer";
	
	@Autowired()
	@Qualifier("customerEntityProvider")
	EntityProvider<Customer> mainProvider;
	
    private Table mainTable;

    private TextField searchField;

    private Button newButton;
    private Button deleteButton;
    private Button editButton;

    private JPAContainer<Customer> mainContainer;

    private String textFilter;

    @PostConstruct
	public void PostConstruct(){
    	
        mainContainer = new JPAContainer<Customer>(Customer.class);
        mainContainer.setEntityProvider(mainProvider);
        
        mainContainer.getEntityProvider();
        buildMainArea();

    }

    private void buildMainArea() {
    	this.setSizeFull();
        
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

        Utils.configTableHead(mainTable, Customer.class);
        

        HorizontalLayout toolbar = new HorizontalLayout();
        newButton = new Button("增加");
        newButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final EntityItem<Customer> newItemItem = mainContainer.createEntityItem(new Customer());
                CustomerEditor personEditor = new CustomerEditor(newItemItem,mainContainer);
                personEditor.center();
                UI.getCurrent().addWindow(personEditor);
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
							mainContainer.removeItem(mainTable.getValue());
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
            	CustomerEditor pe = new CustomerEditor(mainTable.getItem(mainTable.getValue()),mainContainer);
            	pe.center();
                UI.getCurrent().addWindow(pe);
            }
        });
        editButton.setEnabled(false);

        searchField = new TextField();
        searchField.setInputPrompt("输入型号搜索");
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

        
        VerticalLayout verticalLayout = new VerticalLayout();
        this.addComponent(verticalLayout);
        verticalLayout.addComponent(toolbar);
        verticalLayout.addComponent(mainTable);
        verticalLayout.setExpandRatio(mainTable, 1);
        verticalLayout.setSizeFull();

    }


    private void updateFilters() {
        mainContainer.setApplyFiltersImmediately(false);
        mainContainer.removeAllContainerFilters();
        
        if (textFilter != null && !textFilter.equals("")) {
            Like ia =new Like("cusNum", "%"+textFilter + "%", false);
            Like ib =new Like("name", "%"+textFilter + "%", false);
            Like ic =new Like("remark", "%"+textFilter + "%", false);
            Or or = new Or(ia,ib,ic);
            mainContainer.addContainerFilter(or);
        }
        mainContainer.applyFilters();
    }

	@Override
	public void enter(ViewChangeEvent event) {
	}
	
	
}
