package org.woodship.luna.core.security;

import org.woodship.luna.util.JPAContainerItemFieldGroup;
import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class UserEditor extends Window  {

	@SuppressWarnings("unchecked")
	public UserEditor(final Item item,  final JPAContainer<User> users) {
		this.setCaption("角色编辑/新增");
		final JPAContainerItem<User> jpaitem = (JPAContainerItem<User>) item;
		final JPAContainerItemFieldGroup<User> fg = new JPAContainerItemFieldGroup<User>(User.class);
		fg.setItemDataSource(jpaitem);
		
		final FormLayout formLayout = new FormLayout();
		formLayout.setMargin(true);
		//增加默认字段
		Utils.buildAndBindFieldGroup(fg, User.class, formLayout);
		
		//TODO 只能选择未选择过的人员
		
		// Buffer the form content
		fg.setBuffered(true);

		//处理保存事件
		Button saveButton = new Button("保存");
		saveButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				try {
					//编辑的直接提交即可
					fg.commit();
					//新增的需要单独处理
					if(jpaitem.getEntity().getId() == null){
						User p =fg.getItemDataSource().getEntity();
						p.setPassword(Utils.DEFAULT_PASSWORD);
						users.addEntity(p);
					}
					Notification.show("保存成功");
//					error.setVisible(false);
					UserEditor.this.close();//关闭，防止再点击，重复增加
				} catch (FieldGroup.CommitException e) {
					Utils.setCommitExceptionMsg(e, fg);
				}
			}
		});
		Button cancelButton = new Button("重置");
		cancelButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				fg.discard();
			}
		});
		
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setMargin(true);
		buttons.addComponent(saveButton);
		buttons.addComponent(cancelButton);
		formLayout.addComponent(buttons);
		formLayout.setComponentAlignment(buttons, Alignment.MIDDLE_LEFT);
		setContent(formLayout);
	}

}
