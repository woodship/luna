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
package org.woodship.luna.core.person;

import org.woodship.luna.db.ContainerUtils;
import org.woodship.luna.util.JPAContainerItemFieldGroup;
import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;

@SuppressWarnings("serial")
public class OrganizationEditor extends Window  {
	JPAContainer<Organization> persons = null;
	JPAContainerItem<Organization> jpaitem = null;

	@SuppressWarnings("unchecked")
	public OrganizationEditor(final Item item,  final JPAContainer<Organization> persons) {
		this.persons = persons;
		this.jpaitem = (JPAContainerItem<Organization>) item;
		final FormLayout formLayout = new FormLayout();
		formLayout.setMargin(true);
		final JPAContainerItemFieldGroup<Organization> fg = new JPAContainerItemFieldGroup<Organization>(Organization.class);
		fg.setItemDataSource(jpaitem);
		/*
		 * 构建Field,在此处理自定义字段
		 */
		fg.setFieldFactory(new DefaultFieldGroupFieldFactory() {
			@SuppressWarnings("rawtypes")
			@Override
			public <T extends Field> T createField(Class<?> dataType, Class<T> fieldType) {
				if (dataType.isAssignableFrom(Organization.class)) {
					ComboBox cb = new ComboBox();
					Container container = ContainerUtils.getInstance().createJPAContainer(Organization.class);
					cb.setContainerDataSource(container);
					cb.setItemCaptionMode(ItemCaptionMode.ITEM);
					cb.setConverter(new SingleSelectConverter<Object>(cb));
					return (T) cb;
				}
				return super.createField(dataType, fieldType);
			}
		});
		
		//增加默认字段
		Utils.buildAndBindFieldGroup(fg, Organization.class, formLayout);
//		formLayout.addComponent(fg.buildAndBind("trueName"));

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
						Organization p =fg.getItemDataSource().getEntity();
						persons.addEntity(p);
					}
					Notification.show("保存成功");
//					error.setVisible(false);
					OrganizationEditor.this.close();//关闭，防止再点击，重复增加
				} catch (FieldGroup.CommitException e) {
					for (Field<?> field: fg.getFields()) {
						ErrorMessage errMsg = ((AbstractField<?>)field).getErrorMessage();
						if (errMsg != null) {
							error.setValue("<div style='color:red'> " + field.getCaption() + ": " +  errMsg.getFormattedHtmlMessage() + "</div>");
							error.setVisible(true);
							break;
						}
					}
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
