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

import java.util.List;

import org.woodship.luna.core.person.OrgType;
import org.woodship.luna.core.person.Organization;
import org.woodship.luna.core.person.Organization_;
import org.woodship.luna.core.security.User;
import org.woodship.luna.util.JPAContainerItemFieldGroup;
import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Or;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ProductEditor extends Window  {

	@SuppressWarnings("unchecked")
	public ProductEditor(final Item item,  final JPAContainer<Product> products, User user) {
		this.setCaption("产品编辑/增加");
		this.setWidth(600, Unit.PIXELS);
		this.setHeight(400,Unit.PIXELS);
		final JPAContainerItem<Product> jpaitem = (JPAContainerItem<Product>) item;
		final GridLayout formLayout = new GridLayout(3,7);
		formLayout.setSizeFull();
		formLayout.setMargin(true);
		final JPAContainerItemFieldGroup<Product> fg = new JPAContainerItemFieldGroup<Product>(Product.class);
		fg.setItemDataSource(jpaitem);
		Product mainp =  jpaitem.getEntity();
		String[] fieldnames = ProductDeptFileds.getFiledNamesByDeptName(mainp.getOrg().getName());
		if( Product.JIAO_XIAN_DEPT_NAME.equals(jpaitem.getEntity().getOrg().getName())){
			this.setHeight(550,Unit.PIXELS);
		}
		
		//增加默认字段
		Utils.buildAndBindFieldGroup(fg, Product.class, formLayout, fieldnames);
		
		//所有字段设置成必填
		for(String fn : fieldnames){
			Field field =  fg.getField(fn);
			field.setRequired(true);
			if(fn.equals(Product_.org.getName())){
				field.setReadOnly(true);//车间只读
			}
		}
		
		//按车间过虑班次
		ComboBox cb = (ComboBox) fg.getField(Product_.classes.getName());
		JPAContainer<Organization> orgcon = (JPAContainer<Organization>) cb.getContainerDataSource();
		Equal equs = new Equal(Organization_.parent.getName(), jpaitem.getEntity().getOrg());
		orgcon.addContainerFilter(equs);
		

		final Label error = new Label("", ContentMode.HTML);
		error.setVisible(false);
		
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
						products.addEntity(p);
					}
					Notification.show("保存成功");
					ProductEditor.this.close();//关闭，防止再点击，重复增加
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
		buttons.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
		buttons.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
		formLayout.addComponent(buttons);
		
		
		final VerticalLayout root = new VerticalLayout();
		root.setSizeFull();
		root.setMargin(true);
		root.addComponent(formLayout);
		root.addComponent(error);
		root.addComponent(buttons);
		root.setExpandRatio(formLayout, 1);
		setContent(root);
	}

}
