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
package org.woodship.luna.demo;

import org.woodship.luna.LunaException;
import org.woodship.luna.util.EntityFieldGroupFieldFactory;
import org.woodship.luna.util.EntitySubTable;
import org.woodship.luna.util.JPAContainerItemFieldGroup;
import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class ProductEditor extends Window  {

	@SuppressWarnings("unchecked")
	public ProductEditor(final Item item,  final JPAContainer<Product> products, final Table mainTable) {
		this.setCaption("产品编辑/增加");
		this.setWidth(400, Unit.PIXELS);
		this.setHeight(600, Unit.PIXELS);
		final JPAContainerItem<Product> jpaitem = (JPAContainerItem<Product>) item;
		
		final JPAContainerItemFieldGroup<Product> fg = new JPAContainerItemFieldGroup<Product>(Product.class);
		fg.setFieldFactory(new EntityFieldGroupFieldFactory(){
			@Override
			public <T extends Field> T createField(Class<?> dataType, Class<T> fieldType) {
				if(dataType == Product_.ess.getJavaType()){
					EntitySubTable<ElementScope> st =  new EntitySubTable<ElementScope>(ElementScope.class);
					st.setCanAddOrRemove(false);
					st.setColumnReadOnly(ElementScope_.element.getName());
					st.setColumnWidth(ElementScope_.element.getName(),50);
					st.setColumnWidth(ElementScope_.min.getName(),100);
					st.setColumnWidth(ElementScope_.max.getName(),100);
					st.getTable().setColumnAlignment(ElementScope_.element.getName(), Table.Align.CENTER);
					return (T) st;
				}
				return super.createField(dataType, fieldType);
			}
		});
		fg.setItemDataSource(jpaitem);
		//增加默认字段
		final VerticalLayout formLayout = new VerticalLayout();
		formLayout.setSpacing(true);
		Utils.buildAndBindFieldGroup(fg, Product.class, formLayout);
		
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
					
					double maxSum = 0d;
					for(ElementScope es :jpaitem.getEntity().getEss()){
						es.validate();
						maxSum += es.getMax();
					}
					if(maxSum > 100){
						throw new LunaException("元素范围最大值之和大于100，可能会引起错误，请调整");
					}
					
					//新增的需要单独处理
					if(jpaitem.getEntity().getId() == null){
						Product p =fg.getItemDataSource().getEntity();
						products.addEntity(p);
					}
					mainTable.refreshRowCache();
					Notification.show("保存成功");
					ProductEditor.this.close();//关闭，防止再点击，重复增加
				} catch (FieldGroup.CommitException e) {
					Utils.setCommitExceptionMsg(e, fg);
				}
			}
		});
		
		final VerticalLayout root = new VerticalLayout();
		root.setSpacing(true);
		root.setMargin(true);
		root.addComponent(formLayout);
		root.addComponent(saveButton);
		setContent(root);
	}

}


