package org.woodship.luna.core.security;

import java.util.LinkedHashSet;
import java.util.Set;

import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.Item;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class UserRoleEditor extends Window  {
	private JPAContainerItem<User> jpaitem = null;
	private Table table;
	@SuppressWarnings("unchecked")
	public UserRoleEditor(final Item item,  final JPAContainer<User> container, final JPAContainer<Role> roleContainer) {
		this.setCaption("为用户设置角色");
		this.jpaitem = (JPAContainerItem<User>) item;
		final VerticalLayout formLayout = new VerticalLayout();
		formLayout.setMargin(true);
		table = new Table();
		table.setContainerDataSource(roleContainer);
		table.setVisibleColumns(new Object[]{"name","remark"});
		Utils.setTableCaption(table, User.class);
		table.setWidth(300, Unit.PIXELS);
		table.setHeight(400, Unit.PIXELS);
		table.setMultiSelect(true);
		table.setSelectable(true);
		table.setMultiSelectMode(MultiSelectMode.SIMPLE);
		//设置值
		User user = jpaitem.getEntity();
		for(Role r : user.getRoles()){
			table.select(r.getId());
		}
		
		
		formLayout.addComponent(table);
		final Label error = new Label("", ContentMode.HTML);
		error.setVisible(false);
		formLayout.addComponent(error);


		//处理保存事件
		Button saveButton = new Button("保存");
		saveButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				Set<?> v = (Set<?>) table.getValue();
				for(Object rid : v){
					Role role = roleContainer.getItem(rid).getEntity() ;
					role.addUser(jpaitem.getEntity());
					roleContainer.getItem(rid).getItemProperty("users").setValue(role.getUsers());
				}
				roleContainer.commit();
				Notification.show("保存成功");
			}
		});
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setMargin(true);
		buttons.addComponent(saveButton);
		formLayout.addComponent(buttons);
		formLayout.setComponentAlignment(buttons, Alignment.MIDDLE_LEFT);
		setContent(formLayout);
	}
	

}
