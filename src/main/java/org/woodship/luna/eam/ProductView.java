package org.woodship.luna.eam;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.dialogs.ConfirmDialog;
import org.woodship.luna.core.person.OrgType;
import org.woodship.luna.core.person.Organization;
import org.woodship.luna.core.security.UserService;
import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.tableexport.ExcelExport;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
public class ProductView extends HorizontalSplitPanel implements ComponentContainer, View{
	public static final String NAME = "product";
	public static final String EXCEL_ACTION_KEY ="ProductView:EXCEL";
	
    private Tree groupTree;

    private Table mainTable;

    private TextField searchField;

    private Button newButton;
    private Button deleteButton;
    private Button editButton;
    private Button excelButton;

    private JPAContainer<Organization> treeContainer;
    private JPAContainer<Product> tableContainer;

    private Organization treeFilter;
    private String textFilter;
    
    @Autowired
    UserService us;

    @PostConstruct
	public void PostConstruct(){
        treeContainer = Utils.getHierarchialJPAContainer(Organization.class);
        tableContainer =Utils.getJPAContainer(Product.class);
        
        buildTree();
        buildMainArea();

        setSplitPosition(15);
        
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

        Utils.setTableDefaultHead(mainTable, Product.class);
        

        HorizontalLayout toolbar = new HorizontalLayout();
        newButton = new Button("增加");
        newButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final EntityItem<Product> newProductItem = tableContainer.createEntityItem(new Product());
                //优先从选择机构上选择默认车间
                if(treeFilter != null && (OrgType.顶级部门.equals(treeFilter.getOrgType()))){
                	newProductItem.getEntity().setOrg(treeFilter);
                }else{
                	//从机构树中找默认车间
                	List<Organization> topdepts = new ArrayList<Organization>();
                	for(Object id : groupTree.getItemIds()){
                		Organization org = treeContainer.getItem(id).getEntity();
                		if(OrgType.顶级部门.equals(org.getOrgType())){
                			topdepts.add(org);
                		}
                	}
                	if(topdepts.size() == 0){
                		Notification.show("你没有管理车间的权限，请联系管理员调整您的数据权限范围",Type.WARNING_MESSAGE);
                		return;
                	}else if(topdepts.size() > 1){
                		Notification.show("请先在左侧机构树上选择要增加的车间",Type.WARNING_MESSAGE);
                		return;
                	}else{
                		newProductItem.getEntity().setOrg(topdepts.get(0));
                	}
                }
                	
                ProductEditor organizationEditor = new ProductEditor(newProductItem,tableContainer,us.getCurrentUser());
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

            @Override
            public void buttonClick(ClickEvent event) {
            	ProductEditor pe = new ProductEditor(mainTable.getItem(mainTable.getValue()),tableContainer,us.getCurrentUser());
            	pe.center();
                UI.getCurrent().addWindow(pe);
            }
        });
        editButton.setEnabled(false);
        
        excelButton = new Button("导出EXCEL");
        excelButton.addClickListener(new Button.ClickListener() {
        	private ExcelExport ee;
            @Override
            public void buttonClick(ClickEvent event) {
            	ee = new ExcelExport(mainTable);
            	ee.excludeCollapsedColumns();
            	ee.setReportTitle("产品统计");
            	ee.export();
            }
        });

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
        toolbar.addComponent(excelButton);
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
            	Equal ea = new Equal("org",treeFilter);
            	Equal eb = new Equal("org.parent",treeFilter);
            	Equal ec = new Equal("org.parent.parent",treeFilter);
            	Or or = new Or(ea,eb,ec);
            	tableContainer.addContainerFilter(or);
            } 
        }
        if (textFilter != null && !textFilter.equals("")) {
            Like like =new Like("produceModel.model", "%"+textFilter + "%", false);
            tableContainer.addContainerFilter(like);
        }
        tableContainer.applyFilters();
    }

	@Override
	public void enter(ViewChangeEvent event) {
	}
	
    private void authenticate() {
    	Subject user = SecurityUtils.getSubject(); 
		newButton.setVisible(user.isPermitted(Utils.getAddActionId(ProductView.class)));
		deleteButton.setVisible(user.isPermitted(Utils.getDelActionId(ProductView.class)));
		editButton.setVisible(user.isPermitted(Utils.getEditActionId(ProductView.class)));
		excelButton.setVisible(user.isPermitted(ProductView.EXCEL_ACTION_KEY));
	}
	
	
}
