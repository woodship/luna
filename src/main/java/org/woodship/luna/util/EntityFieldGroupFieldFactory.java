package org.woodship.luna.util;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.metamodel.ManagedType;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;

public class EntityFieldGroupFieldFactory extends DefaultFieldGroupFieldFactory  implements TableFieldFactory{
	private static final long serialVersionUID = -5159857470726117668L;
	@SuppressWarnings("rawtypes")
	@Override
	public <T extends Field> T createField(Class<?> dataType, Class<T> fieldType) {
		Entity e = dataType.getAnnotation(Entity.class);
		if(e != null & AbstractSelect.class.isAssignableFrom(fieldType)){//实体类型生成下拉框
			return  createEntityField(dataType, fieldType);
		}
		if (Date.class.isAssignableFrom(dataType)) {
			return (T) createDateField();
		}
		return super.createField(dataType, fieldType);
	}

	/**
	 * 为实体类属性生成下拉选择框
	 * TODO 查找实体对应EntityProvider方法不严谨
	 * @param dataType
	 * @param fieldType
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T extends Field, E > T  createEntityField(Class<E> dataType, Class<T> fieldType) {
		ComboBox cb = new ComboBox();
		JPAContainer<E> container = Utils.getJPAContainer(dataType);
		cb.setContainerDataSource(container);
		cb.setItemCaptionMode(ItemCaptionMode.ITEM);
		cb.setConverter(new SingleSelectConverter<Object>(cb));
		return (T) cb;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T extends Field> T createDateField() {
		DateField field = new DateField();
		field.setDateFormat("yyyy-MM-dd");
		field.setImmediate(true);
		return (T) field;
	}

	@Override
	public Field<?> createField(Container container, Object itemId,
			Object propertyId, Component uiContext) {
		Property containerProperty = container.getContainerProperty(itemId,
				propertyId);
		Class<?> type = containerProperty.getType();
		Entity e = type.getAnnotation(Entity.class);
		if (e != null || Enum.class.isAssignableFrom(type)){//实体、enum类型自动生成下拉框
			return createField(type,ComboBox.class);
		}
		return createFieldByPropertyType(type);
	}
	/**
	 * Creates fields based on the property type.
	 * <p>
	 * The default field type is {@link TextField}. Other field types generated
	 * by this method:
	 * <p>
	 * <b>Boolean</b>: {@link CheckBox}.<br/>
	 * <b>Date</b>: {@link DateField}(resolution: day).<br/>
	 * <b>Item</b>: {@link Form}. <br/>
	 * <b>default field type</b>: {@link TextField}.
	 * <p>
	 * 
	 * @param type
	 *            the type of the property
	 * @return the most suitable generic {@link Field} for given type
	 */
	public static Field<?> createFieldByPropertyType(Class<?> type) {
		// Null typed properties can not be edited
		if (type == null) {
			return null;
		}

		// Item field
		if (Item.class.isAssignableFrom(type)) {
			return new Form();
		}

		// Date field
		if (Date.class.isAssignableFrom(type)) {
			final DateField df = new DateField();
			df.setResolution(DateField.RESOLUTION_DAY);
			return df;
		}

		// Boolean field
		if (Boolean.class.isAssignableFrom(type)) {
			return new CheckBox();
		}

		return new TextField();
	}
}
