package org.woodship.luna.core.security;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.dialogs.ConfirmDialog;
import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
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
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
public class UserView extends VerticalLayout implements ComponentContainer, View{
	public static final String NAME = "user";
	
	@Autowired()
	UserService us;
	
    private Table mainTable;

    private TextField searchField;

    private Button newButton;
    private Button deleteButton;
    private Button editButton;
    private Button fromPersonButton;
    private Button setRolsesButton;
    private Button changePwButton;
    
    private JPAContainer<User> mainContainer ;
    private JPAContainer<Role> roleContainer ;
    private String textFilter;

    @PostConstruct
	public void PostConstruct(){
        mainContainer= Utils.getJPAContainer(User.class);
        roleContainer=Utils.getJPAContainer(Role.class);
        
        buildMainArea();

    }

    private void buildMainArea() {
        mainTable = new Table(null, mainContainer);
        mainTable.setSelectable(true);
        mainTable.setImmediate(true);
        mainTable.setRowHeaderMode(RowHeaderMode.INDEX);
//     mainTable.setColumnWidth(null, 24);//设置序号列宽度
        mainTable.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
            	Object value = event.getProperty().getValue();
            	if(value != null ){
            		User r = mainContainer.getItem(value).getEntity();
            		if(r.isSysUser()){
            			 deleteButton.setEnabled(false);
                         editButton.setEnabled(false);
                         setRolsesButton.setEnabled(true);
                        return;
            		}
            	}
                setModificationsEnabled( value != null);
            }

            private void setModificationsEnabled(boolean b) {
                deleteButton.setEnabled(b);
                editButton.setEnabled(b);
                fromPersonButton.setEnabled(b);
                setRolsesButton.setEnabled(b);
                changePwButton.setEnabled(b);
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

//        mainTable.setVisibleColumns(new Object[]{"username","showName","sysUser","person.trueName"});
        Utils.setTableDefaultHead(mainTable, User.class);
        

        HorizontalLayout toolbar = new HorizontalLayout();
        newButton = new Button("直接增加");
        newButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final EntityItem<User> newUserItem = mainContainer.createEntityItem(new User());
                User u = newUserItem.getEntity();
                u.setPassword(User.DEFAULT_PASSWORD);
                UserEditor personEditor = new UserEditor(newUserItem,mainContainer);
                personEditor.center();
                UI.getCurrent().addWindow(personEditor);
            }
        });

        fromPersonButton = new Button("从人员选择");
        fromPersonButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	Notification.show("暂未实现");
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
            	UserEditor pe = new UserEditor(mainTable.getItem(mainTable.getValue()),mainContainer);
            	pe.center();
                UI.getCurrent().addWindow(pe);
            }
        });
        editButton.setEnabled(false);
        
        
        setRolsesButton = new Button("角色设置");
        setRolsesButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	Notification.show("暂未实现，请先在角色管理中操作",Type.WARNING_MESSAGE);
//            	UserRoleEditor pe = new UserRoleEditor(mainTable.getItem(mainTable.getValue()) ,mainContainer, roleContainer);
//            	pe.center();
//                UI.getCurrent().addWindow(pe);
            }
        });
        setRolsesButton.setEnabled(false);
        
        changePwButton = new Button("修改密码");
        changePwButton.addClickListener(new Button.ClickListener() {
            @SuppressWarnings("unchecked")
			@Override
            public void buttonClick(ClickEvent event) {
            	User user = ((JPAContainerItem<User>)mainTable.getItem(mainTable.getValue())).getEntity();
            	UserChangePWEditor w = new UserChangePWEditor(user ,us);
            	w.center();
                UI.getCurrent().addWindow(w);
            }
        });
        changePwButton.setEnabled(false);

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
        toolbar.addComponent(fromPersonButton);
        toolbar.addComponent(setRolsesButton);
        toolbar.addComponent( changePwButton);
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
            Like like =new Like("showName", textFilter + "%", false);
            mainContainer.addContainerFilter(like);
        }
        mainContainer.applyFilters();
    }

	@Override
	public void enter(ViewChangeEvent event) {
	}
	
	
}
