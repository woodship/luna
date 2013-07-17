package org.woodship.luna.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.PersistenceException;
import javax.persistence.metamodel.Attribute;
import javax.validation.constraints.NotNull;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.woodship.luna.LunaException;
import org.woodship.luna.core.security.User;
import org.woodship.luna.core.security.UserService;
import org.woodship.luna.db.BatchUpdateEntityProvider;
import org.woodship.luna.db.HierarchialEntity;
import org.woodship.luna.db.TransactionalEntityProvider;

import ru.xpoft.vaadin.SpringApplicationContext;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Validator;
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
	 * @param entityClass
	 * @param fieldNames
	 */
	@SuppressWarnings("rawtypes")
	public static void configTableHeadA(Table table,Class<?> entityClass
			, Attribute ... fieldNames) {
		String[] fs  = null;
		if(fieldNames != null){
			fs = new String[fieldNames.length];
			for(int i = 0; i < fieldNames.length; i++){
				fs[i] = fieldNames[i].getName();
			}
		}
		configTableHead(table, entityClass,fs);
	}

	/**
	 * 为指定{@link BeanFieldGroup}绑定字段。同时生成UI Field并添加到 layout 中。</p>
	 * 如果fieldNames为null, 则beanClass中有{@link Caption}注解的字段被绑定 ，否则按fieldNames指定字段绑定。 </p>
	 * 最终UI上显示的caption根据{@link Caption}注解生成
	 * 该方法只负责确定生成哪些字段，生成Caption。创建字段工作最终调用{@link EntityFieldGroupFieldFactory}完成
	 * @param fieldGroup 
	 * @param beanClass 要绑定到{@link BeanFieldGroup}上的beanClass
	 * @param layout 目标UI
	 * @param fieldNames 指定的字段必须在beanClass中存在，否则抛出{@link LunaException}
	 */
	@SuppressWarnings("serial")
	public static void buildAndBindFieldGroup(FieldGroup fieldGroup, Class<?> beanClass,ComponentContainer layout,String... fieldNames){
		//如果指定了fieldNames则按指定字段及顺序绑定
		if(fieldNames != null && fieldNames.length > 0){
			for(String fn : fieldNames){
				try {
					Field f = beanClass.getDeclaredField(fn);
					String caption = getCaption(beanClass, fn);
					buildAndBind(fieldGroup,  beanClass, layout,  f,  caption);
				} catch (Exception e) {
					e.printStackTrace();
					throw new LunaException("从"+beanClass.getName()+"获取字段"+fn+"出错。请确认设置了正确的字段名。");
				}
			}
		}else{
			//如果未绑定字段，则使用有Caption注解的字段绑定
			for(Field f : beanClass.getDeclaredFields()){
				Caption caption = f.getAnnotation(Caption.class);
				if(caption != null){
					buildAndBind(fieldGroup,  beanClass, layout,  f,  caption.value());
				}
			}
		}
		//设置绑定后字段的默认属性
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
		//focus第一个字段
//		if(fs.size() > 0){
//			fs.iterator().next().focus();
//		}
	}
	
	private static void buildAndBind(FieldGroup fieldGroup, Class<?> beanClass,ComponentContainer layout, Field f, String caption){
		Class<?> type = f.getType();
		Entity e = type.getAnnotation(Entity.class);
		if (e != null || Enum.class.isAssignableFrom(type)){//实体、enum类型自动生成下拉框
			layout.addComponent(fieldGroup.buildAndBind(caption,f.getName(),ComboBox.class));
		}
		else{
			layout.addComponent(fieldGroup.buildAndBind(caption,f.getName()));
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
	 * 根据类获得增加功能按钮的KEY
	 * @param calss
	 * @return
	 */
	public static String getAddActionKey(Class<?> viewCalss){
		return viewCalss.getName()+":ADD";
	}
	/**
	 * 根据类获得删除功能按钮的KEY
	 * @param viewCalss
	 * @return
	 */
	public static String getDelActionKey(Class<?> viewCalss){
		return viewCalss.getName()+":DEL";
	}
	/**
	 * 根据类获得编辑功能按钮的KEY
	 * @param viewCalss
	 * @return
	 */
	public static String getEditActionKey(Class<?> viewCalss){
		return viewCalss.getName()+":EDIT";
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
	 * 查找bean
	 * @param beanClass
	 * @return
	 */
	public static <T> T getBeanByType(Class<T> beanClass){
		return SpringApplicationContext.getApplicationContext().getBean(beanClass);
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
		Throwable ta = e.getCause();
		 if(ta instanceof EmptyValueException){
			//TODO 主表精确定位异常,因为设置了setRequired, 如果不设置则样式不对,需要自定义红*样式
			msg = "请填写完整，红色星号为必填";
		}
		 else if(ta instanceof Validator.InvalidValueException){
			msg = ta.getMessage();
		}
		else{
			for(Throwable t = e.getCause(); t != null; t = t.getCause()){
				if(t instanceof javax.persistence.PersistenceException){
					msg = Utils.getPersistenceErrorMsg((PersistenceException) t);;
					break;
				}
			}
		}
		
		Notification.show(msg, Type.WARNING_MESSAGE);
		
	}

	/**
	 * 根据Caption注解返回caption,不生成必填标识
	 * @param clazz
	 * @param fieldname
	 * @return 找不到则返回fieldname
	 */
	public static String getCaption(Class<?> clazz, String fieldname){
		return getCaption(clazz, fieldname, false);
	}
	
	/**
	 * 根据Caption注解返回caption
	 * @param clazz
	 * @param fieldname
	 * @param showRequiredSign 是否生成必填标识
	 * @return
	 */
	public static String getCaption(Class<?> clazz, String fieldname, boolean showRequiredSign){
		try {
			Field f = clazz.getDeclaredField(fieldname);
			String caption = getCaption(f,showRequiredSign);
			return caption == null ? fieldname : caption;
		} catch (Exception e1) {
			return fieldname;
		}
	}
	
	/**
	 * 获得Caption
	 * @param field
	 * @param showRequiredSign 是否生成必填标识(红*)
	 * @return
	 */
	public static  String getCaption(Field field, boolean showRequiredSign){
		Caption caption = field.getAnnotation(Caption.class);
		NotNull notNull = field.getAnnotation(NotNull.class);
		if(caption != null){
			String cap = caption.value();
			if(showRequiredSign && notNull != null){
				cap += "<font color=red>*</font>";
			}
			return cap;
		}else{
			return field.getName();
		}
	}

	/**
	 * 根据异常信息生成友好提示
	 * @param e
	 * @return
	 */
	public static String getPersistenceErrorMsg(javax.persistence.PersistenceException e){
		String msg = e.getMessage();
		if(msg == null){
			return null;
		}
		if(msg.contains("FOREIGN KEY")){
			msg = "该数据已被引用，禁止删除:"+e.getMessage();
		}else if(msg.contains("Unique")){
			msg ="关键字段数据重复\n"+e.getMessage();
		}
		return msg;
	}
	
	/**
	 * 获得当前登录用户
	 * @return
	 */
	public static User getCurrentUser(){
		UserService us = getBeanByType(UserService.class);
		return us.getCurrentUser();
	}
}
