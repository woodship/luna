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
package org.woodship.luna.base;

import org.woodship.luna.db.ContainerUtils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PersonEditor extends Window  {
	JPAContainer<Person> persons = null;
	JPAContainerItem<Person> person = null;
	
	public PersonEditor(final Item item,  final JPAContainer<Person> persons) {
		this.persons = persons;
		this.person = (JPAContainerItem<Person>) item;
		FormLayout formLayout = new FormLayout();

		// Just edit the first item in the JPAContainer
		final FieldGroup fg = new FieldGroup(item) {
			/*
			 * Override configureField to add a bean validator to each field.
			 */
			@Override
			protected void configureField(Field<?> field) {
				super.configureField(field);
				// Add Bean validators if there are annotations
				// Note that this requires a bean validation implementation to
				// be available.
				field.removeAllValidators();
				BeanValidator validator = new BeanValidator(Person.class,
						getPropertyId(field).toString());
				field.addValidator(validator);
				if (field.getLocale() != null) {
					validator.setLocale(field.getLocale());
				}
				
				if (field instanceof TextField) {
		            ((TextField) field).setNullRepresentation("");
		        }
			}
		};

		/*
		 * This is an example of a field factory that constructs a complex
		 * field.
		 */
		fg.setFieldFactory(new DefaultFieldGroupFieldFactory() {
			@Override
			public <T extends Field> T createField(Class<?> type,
					Class<T> fieldType) {
				if (type.isAssignableFrom(Department.class)) {
					ComboBox cb = new ComboBox();
					Container container = ContainerUtils.getInstance().createJPAContainer(Department.class);
					cb.setContainerDataSource(container);
					cb.setItemCaptionPropertyId("name");
					cb.setConverter(new SingleSelectConverter(cb));
					return (T) cb;
				}
				return super.createField(type, fieldType);
			}
		});

		formLayout.addComponent(fg.buildAndBind("firstName"));
		formLayout.addComponent(fg.buildAndBind("lastName"));
		formLayout.addComponent(fg.buildAndBind("street"));
		formLayout.addComponent(fg.buildAndBind("city"));
		formLayout.addComponent(fg.buildAndBind("zipCode"));
		formLayout.addComponent(fg.buildAndBind("phoneNumber"));
		formLayout.addComponent(fg.buildAndBind("department"));

		Button saveButton = new Button("保存");
		saveButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				try {
					if(person.getEntity().getId() != null){
						fg.commit();
					}else{
						fg.commit();
						Person p = ((JPAContainerItem<Person>)fg.getItemDataSource()).getEntity();
						persons.addEntity(p);
					}
				} catch (FieldGroup.CommitException e) {
					e.printStackTrace();
					Notification.show("Couldn't commit values: "
							+ e.getCause().getMessage(),
							Notification.Type.ERROR_MESSAGE);
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

		formLayout.addComponent(saveButton);
		formLayout.addComponent(cancelButton);

		setContent(formLayout);
	}

}
