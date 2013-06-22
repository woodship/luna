package org.woodship.luna.demo;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.dialogs.ConfirmDialog;
import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.EntityItem;
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
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
public class ProductView extends VerticalLayout implements ComponentContainer, View{
	
	public static final String PEI_LIAO_KEY = ProductView.class.getSimpleName()+":"+"PeiLiao";
	@Autowired()
	private ProductEntityProvider mainProvider;
	
    private Table mainTable;

    private TextField searchField;

    private Button newButton;
    private Button deleteButton;
    private Button editButton;

    private JPAContainer<Product> mainContainer;

    private String textFilter;


    @PostConstruct
	public void PostConstruct(){
    	
        mainContainer = new JPAContainer<Product>(Product.class);
        mainContainer.setEntityProvider(mainProvider);
        
        mainContainer.getEntityProvider();
        buildMainArea();
        authenticate();

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

        for(final Element e : Element.values()){
        	mainTable.addGeneratedColumn(e.toString(), new ColumnGenerator() {
				@Override
				public Object generateCell(Table source, Object itemId, Object columnId) {
					Product m = mainContainer.getItem(itemId).getEntity();
					return m.getScope(e);
				}
			});
        }
        List<String> fn = new ArrayList<String>();
        fn.add(Product_.name.getName());
        for(final Element e : Element.values()){
        	fn.add(e.toString());
        }
        String[] s = new String[fn.size()];
        Utils.configTableHead(mainTable, Product.class,  fn.toArray(s));
        

        HorizontalLayout toolbar = new HorizontalLayout();
        newButton = new Button("增加");
        newButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final EntityItem<Product> newItemItem = mainContainer.createEntityItem(new Product());
                Product p = newItemItem.getEntity();
                for(Element e : Element.values()){
                	ElementScope es = new ElementScope(e, 0, 0);
                	es.setId(java.util.UUID.randomUUID().toString());
                	p.addElementScope(es);
                }
                ProductEditor personEditor = new ProductEditor(newItemItem,mainContainer,mainTable);
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
            	ProductEditor pe = new ProductEditor(mainTable.getItem(mainTable.getValue()),mainContainer,mainTable);
            	pe.center();
                UI.getCurrent().addWindow(pe);
            }
        });
        editButton.setEnabled(false);
        

        searchField = new TextField();
        searchField.setInputPrompt("输入名称搜索");
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
        toolbar.setSpacing(true);

        
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
	 private void authenticate() {
	    	Subject user = SecurityUtils.getSubject(); 
			newButton.setVisible(user.isPermitted(Utils.getAddActionId(this.getClass())));
			deleteButton.setVisible(user.isPermitted(Utils.getDelActionId(this.getClass())));
			editButton.setVisible(user.isPermitted(Utils.getEditActionId(this.getClass())));
		}
	
}
