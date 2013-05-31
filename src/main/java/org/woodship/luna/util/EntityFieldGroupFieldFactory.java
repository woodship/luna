package org.woodship.luna.util;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Entity;

import ru.xpoft.vaadin.SpringApplicationContext;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;

public class EntityFieldGroupFieldFactory extends DefaultFieldGroupFieldFactory {
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
		String beanName = dataType.getSimpleName()+"EntityProvider";
		Map<String,EntityProvider> beans = SpringApplicationContext.getApplicationContext().getBeansOfType(EntityProvider.class);
		for(Entry<String, EntityProvider> en : beans.entrySet()) {
			if(beanName.equalsIgnoreCase(en.getKey())){
				JPAContainer container = new JPAContainer<E>(dataType);
				container.setEntityProvider(en.getValue());
				cb.setContainerDataSource(container);
				cb.setItemCaptionMode(ItemCaptionMode.ITEM);
				cb.setConverter(new SingleSelectConverter<Object>(cb));
			}
		}
		return (T) cb;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <T extends Field> T createDateField() {
		DateField field = new DateField();
		field.setDateFormat("yyyy-MM-dd");
		field.setImmediate(true);
		return (T) field;
	}
}
