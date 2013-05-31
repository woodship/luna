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
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ProductEditor extends Window  {
	JPAContainer<Product> products = null;
	JPAContainerItem<Product> jpaitem = null;

	@SuppressWarnings("unchecked")
	public ProductEditor(final Item item,  final JPAContainer<Product> persons) {
		this.setCaption("产品编辑/增加");
		this.setWidth(600, Unit.PIXELS);
		this.setHeight(400,Unit.PIXELS);
		this.products = persons;
		this.jpaitem = (JPAContainerItem<Product>) item;
		final GridLayout formLayout = new GridLayout(3,7);
		formLayout.setSizeFull();
		formLayout.setMargin(true);
		final JPAContainerItemFieldGroup<Product> fg = new JPAContainerItemFieldGroup<Product>(Product.class);
		fg.setItemDataSource(jpaitem);
		//增加默认字段
		Utils.buildAndBindFieldGroup(fg, Product.class, formLayout);
		
		//配制工号
		ComboBox cb = (ComboBox) fg.getField("person");
		cb.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cb.setItemCaptionPropertyId("workNum");
		
		final Label error = new Label("", ContentMode.HTML);
		error.setVisible(false);
//		formLayout.addComponent(error, 0, 1, 0, row2)addComponent(error);
		

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
						Product p =fg.getItemDataSource().getEntity();
						persons.addEntity(p);
					}
					Notification.show("保存成功");
//					error.setVisible(false);
					ProductEditor.this.close();//关闭，防止再点击，重复增加
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
