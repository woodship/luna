/**
 * Copyright 2009-2013 Oy Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.woodship.luna.eam;

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
public class InvItemEditor extends Window  {
	JPAContainer<InvItem> mainContainer = null;
	JPAContainerItem<InvItem> jpaitem = null;

	@SuppressWarnings("unchecked")
	public InvItemEditor(final Item item,  final JPAContainer<InvItem> mainContainer) {
		this.setCaption("型号编辑/新增");
		this.mainContainer = mainContainer;
		this.jpaitem = (JPAContainerItem<InvItem>) item;
		final FormLayout formLayout = new FormLayout();
		formLayout.setMargin(true);
		final JPAContainerItemFieldGroup<InvItem> fg = new JPAContainerItemFieldGroup<InvItem>(InvItem.class);
		fg.setItemDataSource(jpaitem);
		
		//增加默认字段
		Utils.buildAndBindFieldGroup(fg, InvItem.class, formLayout);

		final Label error = new Label("", ContentMode.HTML);
		error.setVisible(false);
		formLayout.addComponent(error);

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
						InvItem p =fg.getItemDataSource().getEntity();
						mainContainer.addEntity(p);
					}
					Notification.show("保存成功");
//					error.setVisible(false);
					InvItemEditor.this.close();//关闭，防止再点击，重复增加
				} catch (FieldGroup.CommitException e) {
					Utils.setCommitExceptionMsg(e, fg, error);
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
