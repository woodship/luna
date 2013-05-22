package org.woodship.luna.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.Caption;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Table;

public class Utils {
	/**
	 * 为table中已经存在的列设置caption，(根据{@link Caption},beanClass的字段上有该注解则增加 )
	 * @param table
	 * @param beanClass
	 */
	public static void setTableCaption(Table table,Class<?> beanClass){
        Object[] vcols = table.getVisibleColumns();
        for(Object col : vcols){
        	try {
				Field f = beanClass.getDeclaredField(col.toString());
				Caption caption = f.getAnnotation(Caption.class);
				if(caption != null){
					table.setColumnHeader(col, caption.value());
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
        }
	}
	
	/**
	 * 为table设置默认列与caption(根据{@link Caption},beanClass的字段上有该注解则增加 )
	 * @param table
	 * @param beanClass
	 */
	public static void setTableDefaultHead(Table table,Class<?> beanClass){
		Map<String, String> map = new LinkedHashMap<String, String>();
		//准备数据
		for(Field f : beanClass.getDeclaredFields()){
			Caption caption = f.getAnnotation(Caption.class);
			if(caption != null){
				map.put(f.getName(), caption.value());
			}
		}
		//设置数据
		Object[] cols = map.keySet().toArray();
		table.setVisibleColumns(cols);
		for(Object col : cols){
			table.setColumnHeader(col, map.get(col));
		}
	}
	
	/**
	 * 为指定{@link BeanFieldGroup}绑定默认字段。(根据{@link Caption},beanClass的字段上有该注解则绑定 )
	 * @param fieldGroup
	 * @param beanClass 要绑定到{@link BeanFieldGroup}上的beanClass
	 * @return 最终绑定的字段
	 */
	public static void buildAndBindFieldGroup(FieldGroup fieldGroup, Class<?> beanClass,ComponentContainer layout){
		for(Field f : beanClass.getDeclaredFields()){
			Caption caption = f.getAnnotation(Caption.class);
			if(caption != null){
				layout.addComponent(fieldGroup.buildAndBind(f.getName()));
			}
		}
	}
}
