package org.woodship.luna.core.security;

import java.util.LinkedHashSet;
import java.util.Set;

import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.Buffered.SourceException;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class RoleUserEditor extends Window  {
	private JPAContainerItem<Role> jpaitem = null;
	private Table table;
	@SuppressWarnings("unchecked")
	public RoleUserEditor(final Item item,  final JPAContainer<Role> container, final JPAContainer<User> userContainer) {
		this.setCaption("角色关联用户设置");
		this.jpaitem = (JPAContainerItem<Role>) item;
		final VerticalLayout formLayout = new VerticalLayout();
		formLayout.setMargin(true);
		table = new Table();
		table.setContainerDataSource(userContainer);
		table.setVisibleColumns(new Object[]{"username","showName"});
		Utils.setTableCaption(table, User.class);
		table.setWidth(300, Unit.PIXELS);
		table.setHeight(400, Unit.PIXELS);
		table.setMultiSelect(true);
		table.setSelectable(true);
		table.setMultiSelectMode(MultiSelectMode.SIMPLE);
		//设置值
		Role r = jpaitem.getEntity();
		for(User u : r.getUsers()){
			table.select(u.getId());
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
				try {
					Set<?> v = (Set<?>) table.getValue();
					Set<User> users = new LinkedHashSet<User>();
					for(Object rid : v){
						users.add(userContainer.getItem(rid).getEntity() );
					}
					jpaitem.getItemProperty("users").setValue(users);
					container.commit();
					Notification.show("保存成功");
				} catch (RemoveAdminUserException e) {
					Notification.show("内置系统管理员不能移除");
				}
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
