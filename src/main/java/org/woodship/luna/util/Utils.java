package org.woodship.luna.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.metamodel.Attribute;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.woodship.luna.core.security.User;
import org.woodship.luna.db.BatchUpdateEntityProvider;
import org.woodship.luna.db.HierarchialEntity;
import org.woodship.luna.db.TransactionalEntityProvider;

import ru.xpoft.vaadin.SpringApplicationContext;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.Caption;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class Utils {
	/**
	 * 默认密码，已经加密
	 */
	public static final String DEFAULT_PASSWORD = (new DefaultPasswordService()).encryptPassword(User.DEFAULT_PASSWORD);

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
	public static void configTableHead(Table table,Class<?> beanClass, String... fieldNames){
		Map<String, String> map = new LinkedHashMap<String, String>();
		if(fieldNames != null && fieldNames.length > 0){
			//如果指定了fieldNames则不管存在不存在都增加，因为有可能是使用的自定义列
			for(String fn : fieldNames){
				String caption = getCaption(beanClass, fn);
				map.put(fn, caption);
			}
		}else{
			//如果没指定fieldNames，则只增加有注解的
			for(Field f : beanClass.getDeclaredFields()){
				Caption caption = f.getAnnotation(Caption.class);
				if(caption != null){
					map.put(f.getName(), caption.value());
				}
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
	 * 参考 configTableHead
	 * @param table
	 * @param beanClass
	 * @param fieldNames
	 */
	@SuppressWarnings("rawtypes")
	public static void configTableHeadA(Table table,Class<?> beanClass
			, Attribute ... fieldNames) {
		String[] fs  = null;
		if(fieldNames != null){
			fs = new String[fieldNames.length];
			for(int i = 0; i < fieldNames.length; i++){
				fs[i] = fieldNames[i].getName();
			}
		}
		configTableHead(table, beanClass,fs);
	}

	/**
	 * 为指定{@link BeanFieldGroup}绑定默认字段。(根据{@link Caption}，beanClass的字段上有该注解则绑定 ) </p>
	 * 该方法只负责确定生成哪些字段，生成Caption，，创建字段
	 * @param fieldGroup
	 * @param beanClass 要绑定到{@link BeanFieldGroup}上的beanClass
	 * @return 
	 */
	@SuppressWarnings("serial")
	public static void buildAndBindFieldGroup(FieldGroup fieldGroup, Class<?> beanClass,ComponentContainer layout,String... fieldNames){

		for(Field f : beanClass.getDeclaredFields()){
			//如果指定了fieldNames则过虑
			if(fieldNames != null && fieldNames.length > 0){
				boolean in = false;
				for(String fn : fieldNames){
					if(f.getName().equals(fn)){
						in = true;
						break;
					}
				}
				if(!in) continue;
			}
			Caption caption = f.getAnnotation(Caption.class);
			if(caption != null){
				Class<?> type = f.getType();
				Entity e = type.getAnnotation(Entity.class);
				if (e != null || Enum.class.isAssignableFrom(type)){//实体、enum类型自动生成下拉框
					layout.addComponent(fieldGroup.buildAndBind(caption.value(),f.getName(),ComboBox.class));
				}
				else{
					layout.addComponent(fieldGroup.buildAndBind(caption.value(),f.getName()));
				}
			}
		}
		Collection<com.vaadin.ui.Field<?>> fs =  fieldGroup.getFields();
		for(com.vaadin.ui.Field<?> f : fs){
			if(f instanceof TextField){
				final TextField tf = (TextField)f;
				tf.addFocusListener(new FocusListener() {
					@Override
					public void focus(FocusEvent event) {
						tf.selectAll();
					}
				});
			}
		}
		if(fs.size() > 0){
			fs.iterator().next().focus();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void buildAndBindFieldGroupA(FieldGroup fieldGroup, Class<?> beanClass,ComponentContainer layout
			, Attribute ... fieldNames) {
		String[] fs  = null;
		if(fieldNames != null){
			fs = new String[fieldNames.length];
			for(int i = 0; i < fieldNames.length; i++){
				fs[i] = fieldNames[i].getName();
			}
		}
		buildAndBindFieldGroup(fieldGroup, beanClass,layout,fs);
	}

	/**
	 * 根据类获得增加功能按钮的ID
	 * @param calss
	 * @return
	 */
	public static String getAddActionId(Class<?> calss){
		return calss.getName()+":ADD";
	}
	/**
	 * 根据类获得删除功能按钮的ID
	 * @param calss
	 * @return
	 */
	public static String getDelActionId(Class<?> calss){
		return calss.getName()+":DEL";
	}
	/**
	 * 根据类获得编辑功能按钮的ID
	 * @param calss
	 * @return
	 */
	public static String getEditActionId(Class<?> calss){
		return calss.getName()+":EDIT";
	}

	/**
	 * 为指定实体类生成JPAContaine
	 * @param entityClass
	 * @return
	 */
	public static <T> JPAContainer<T> getJPAContainer(final Class<T> entityClass){
		EntityProvider<T> ep =  getEntityProvider( entityClass);
		JPAContainer<T> container= new JPAContainer<T>(entityClass);
		container.setEntityProvider(ep);
		return container;
	}


	/**
	 * 为指定实体生成支持树的JPAContaine
	 * @param entityClass
	 * @return
	 */
	public static <T extends HierarchialEntity<T>> JPAContainer<T> getHierarchialJPAContainer(final Class<T> entityClass){
		EntityProvider<T> ep =  getEntityProvider( entityClass);

		class HJPAContainer<H extends HierarchialEntity<H>> extends JPAContainer<H>{
			private static final long serialVersionUID = 1L;

			public HJPAContainer(Class<H> entityClass, EntityProvider<H> entityProvide) {
				super(entityClass);
				setEntityProvider(entityProvide);
				setParentProperty("parent");
			}
			@Override
			public boolean areChildrenAllowed(Object itemId) {
				return super.areChildrenAllowed(itemId)
						&& !getItem(itemId).getEntity().isLeaf();
			}
		}

		JPAContainer<T> container= new HJPAContainer<T>(entityClass, ep);
		container.setEntityProvider(ep);
		return container;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static  <T> EntityProvider<T> getEntityProvider(Class<T> entityClass){
		EntityProvider<T> ep = null;
		Map<String, TransactionalEntityProvider> beans = SpringApplicationContext.getApplicationContext().getBeansOfType(TransactionalEntityProvider.class);
		for(Entry<String, TransactionalEntityProvider> en : beans.entrySet()) {
			TransactionalEntityProvider tep = en.getValue();
			if(entityClass.equals(tep.getEntityClass())){
				ep = tep; 
				break;
			}
		}
		if(ep == null){
			Map<String, BatchUpdateEntityProvider> beans2 = SpringApplicationContext.getApplicationContext().getBeansOfType(BatchUpdateEntityProvider.class);
			for(Entry<String, BatchUpdateEntityProvider> en : beans2.entrySet()) {
				BatchUpdateEntityProvider tep = en.getValue();
				if(entityClass.equals(tep.getEntityClass())){
					ep = tep; 
					break;
				}
			}
		}
		if(ep == null){
			throw new RuntimeException("未找到类 "+entityClass.getName()+" 对应的EntityProvider bean");
		}
		return ep;
	}


	/**
	 * 明码转换成密码，该方法比较慢，尽量少用
	 * @param pw
	 * @return
	 */
	public static String encryptPassword(String pw){
		DefaultPasswordService ps = new DefaultPasswordService();
		return ps.encryptPassword(pw);
	}

	public static void setCommitExceptionMsg(CommitException e,FieldGroup fg) {
		String msg = "提交异常，"+e.getMessage();
		
		if(e.getCause() instanceof EmptyValueException){
			//TODO 精确定位异常
			msg = "请填写完整，红色星号为必填";
		}
		else{
			msg = e.getCause().getMessage();
		}
		
		Notification.show(msg, Type.WARNING_MESSAGE);
		
	}

	/**
	 * 根据Caption注解返回caption
	 * @param clazz
	 * @param fieldname
	 * @return 找不到则返回fieldname
	 */
	public static String getCaption(Class<?> clazz, String fieldname){
		try {
			Field f = clazz.getDeclaredField(fieldname);
			Caption cap = f.getAnnotation(Caption.class);
			String c = f.getName();
			if(cap != null ){
				c = cap.value();
			}
			return c;
		} catch (Exception e1) {
			return fieldname;
		} 
	}

}
