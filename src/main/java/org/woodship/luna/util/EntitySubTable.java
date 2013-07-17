package org.woodship.luna.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.woodship.luna.db.IdEntity;
import org.woodship.luna.db.IdEntity_;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.Caption;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Table.HeaderClickListener;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.Runo;


/**
 * 目前主要用于@OneToMany(cascade=CascadeType.ALL)
 * 有Caption注解的字段默认会可见
 * 支持上下左右小箭头切换光标位置
 * 
 * @author laocui
 *
 * @param <E>
 */
@SuppressWarnings("serial")
public class EntitySubTable<E extends IdEntity<E>> extends CustomField<List<E>> {
	 static Logger logger = Logger.getLogger(EntitySubTable.class.getName());
	
	private final String actionid = "action" + System.currentTimeMillis();
	private BeanContainer<String, E> container ;

	private Table table;
	private Class<E> entityClass;
//	private Set<Field<?>> fields = new HashSet<Field<?>>();
	private boolean edited = false;//是否编辑了数据，不包含删除
	private List<Object> visibleColumns = new ArrayList<Object>();
	private boolean readOnly = false;
	private boolean canRemove = true;
	private boolean canAdd = true;
	private List<String> readOnlyColumns = new ArrayList<String>();
	private Map<String,Integer> columnsWidth = new HashMap<String,Integer>();
	private String[] customerVisibleColumns;
	private final int defaultTextFieldWith = 80;

	// Map to find a field component by its item ID and property ID，key:itemId,value:
	//{itemid : [{fieldid : Field},...]}
	final HashMap<Object,HashMap<Object,Field<?>>> editablefields = new HashMap<Object,HashMap<Object,Field<?>>>();
	        
	// {Field : itemid}
	final HashMap<Field<?>,Object> itemIds = new HashMap<Field<?>,Object>(); 

	public EntitySubTable(final Class<E> entityClass) {
		this(entityClass, false);
	}
	public EntitySubTable(final Class<E> entityClass , boolean readOnly) {
		this.entityClass = entityClass;
		table = new Table();
		table.setSelectable(true);
		container = new BeanContainer<String, E>(entityClass);
		container.setBeanIdProperty(IdEntity_.id.getName());
		table.setContainerDataSource(container);
		this.setHeight(350,Unit.PIXELS);
		this.readOnly = readOnly;
	}

	
	
	@SuppressWarnings("unchecked")
	@Override
	protected Component initContent() {
		//		init table
		table.setStyleName(Runo.TABLE_SMALL);
		table.setEditable(!readOnly);
		table.setSizeFull();

		Property<?> p  =this.getPropertyDataSource();
		final List<E> value = (List<E>) p.getValue();
		for(E e : value){
			container.addBean(e);
		}

		if(table.isEditable()){
			table.setTableFieldFactory(new EntityFieldGroupFieldFactory() {
				@Override
				public Field<?> createField(Container container, Object itemId,
						Object propertyId, Component uiContext) {
					Field<?> field =  super.createField(container, itemId, propertyId, uiContext);
	
					LunaBeanValidator validator = new LunaBeanValidator(entityClass,propertyId.toString());
					field.addValidator(validator);
					field.addValueChangeListener(new ValueChangeListener() {
						@Override
						public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
							edited = true;						
						}
					});
					
					if(readOnlyColumns.contains(propertyId)){
						field.setReadOnly(true);
						field.setTabIndex(-1);
					}else{
					     // Manage the field in the field storage
				        HashMap<Object,Field<?>> itemMap = editablefields.get(itemId);
				        if (itemMap == null) {
				            itemMap = new HashMap<Object,Field<?>>();
				            editablefields.put(itemId, itemMap);
				        }
				        itemMap.put(propertyId, field);
						
						itemIds.put(field, itemId);
						
						if(field instanceof TextField){
							final TextField tf = ((TextField)field);
							tf.setData(new ItemPropertyId(itemId, propertyId));
							tf.addFocusListener(new FocusListener() {//移入选中
								@Override
								public void focus(FocusEvent event) {
									tf.selectAll();
								}
							});
							tf.setNullRepresentation("");
						}else if (field instanceof TextArea){
							final TextArea ta = ((TextArea)field);
							ta.setNullRepresentation("");
						}
					}
					
					//设置宽度
					if(field instanceof TextField){
						field.setWidth(defaultTextFieldWith,Unit.PIXELS);//默认
					}
					if(columnsWidth.containsKey(propertyId)){
						field.setWidth(columnsWidth.get(propertyId),Unit.PIXELS);//订制
					}
					return field;
				}
			});
			
			
			if(canAdd || canRemove){
				visibleColumns.add(actionid);
				table.setColumnWidth(actionid, 40);
				table.setColumnAlignment(actionid, Table.Align.CENTER);
				
				//增加删除图标, 按钮并绑定删除事件
				if(canRemove){
					table.addGeneratedColumn(actionid, new ColumnGenerator() {
						@Override
						public Object generateCell(Table source, final Object itemId, Object columnId) {
							final Button bt = new Button();
							bt.setIcon(new ThemeResource("icons/x_14x14.png"));
							bt.setStyleName(Runo.BUTTON_LINK);
							bt.addClickListener(new ClickListener() {
								@Override
								public void buttonClick(ClickEvent event) {
									E e = container.getItem(itemId).getBean();
									value.remove(e);
									container.removeItem(itemId);
									edited = true;
									
									for(Entry<Object, Field<?>> en : editablefields.get(itemId).entrySet()){
										itemIds.remove(en.getValue());
									}
									editablefields.remove(itemId);
								}
							});
							return bt;
						}
					});
				}
				
				//新增图标及事件
				if(canAdd){
					//TODO 根据不同主题自动适应路径
					table.setColumnHeader(actionid, "<img style='width:14px'' src='/VAADIN/themes/dashboard/icons/plus1_16x16.png'>");
		
					table.addHeaderClickListener(new HeaderClickListener() {
						@Override
						public void headerClick(HeaderClickEvent event) {
							Object pid = event.getPropertyId();
							if(pid.equals(actionid)){
								try {
									table.setEditable(true);
									E bean = entityClass.newInstance();
									bean.setId(java.util.UUID.randomUUID().toString());
									container.addBean(bean);
									edited = true;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					});
				}else{
					table.setColumnHeader(actionid, "操作");
				}
			}
		}
		//设置可见列及每列标题
		if(customerVisibleColumns != null && customerVisibleColumns.length > 0){
			for(String col : customerVisibleColumns){
				boolean showRequiredSign = !readOnlyColumns.contains(col)&& !readOnly;
				String cap = Utils.getCaption(entityClass, col,showRequiredSign);
				visibleColumns.add(col);
				table.setColumnHeader(col,cap);
			}
		}else{
			for(java.lang.reflect.Field field : entityClass.getDeclaredFields()){
				boolean showRequiredSign = !readOnlyColumns.contains(field.getName())&& !readOnly;
					Caption caption = field.getAnnotation(Caption.class);
					if(caption != null){
						String cap = Utils.getCaption(field,showRequiredSign);
						visibleColumns.add(field.getName());
						table.setColumnHeader(field.getName(),cap);
					}
			}
		}
		table.setVisibleColumns(visibleColumns.toArray());

		
		Panel navigator = new Panel();
		navigator.setStyleName(Runo.PANEL_LIGHT);
		navigator.setContent(table);
		navigator.addActionHandler(new KbdHandler());
		navigator.setSizeFull();
		return navigator;
	}


	public Table getTable(){
		return table;
	}
	public Object getSelected(){
		return table.getValue();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void validate() throws InvalidValueException {
		for(Entry<Object, HashMap<Object, Field<?>>> ien : editablefields.entrySet()){
			for(Entry<Object, Field<?>> fen : ien.getValue().entrySet()){
				fen.getValue().validate();
			}
		}
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public boolean isModified() {
		return edited == true;
	}


	@SuppressWarnings("rawtypes")
	@Override
	public void discard() throws SourceException {
		for(Field f :itemIds.keySet()){
			f.discard();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getType() {
		return  List.class;
	}

	@Override
	protected List<E> getInternalValue() {
		List<E> list = new ArrayList<E>();
		for(Object id : container.getItemIds()){
			list.add(container.getItem(id).getBean());
		}
		return list;
	}


	public void setReadOnlyColumns(String... propertyIds) {
		if(propertyIds != null){
			for(String pid : propertyIds){
				this.readOnlyColumns.add(pid);
			}
		}
	}

	public void setColumnWidth(String name, int width) {
		columnsWidth.put(name, width);
	}


	public boolean isCanRemove() {
		return canRemove;
	}
	public void setCanRemove(boolean canRemove) {
		this.canRemove = canRemove;
	}
	public boolean isCanAdd() {
		return canAdd;
	}
	public void setCanAdd(boolean canAdd) {
		this.canAdd = canAdd;
	}
	@Override
	public void focus() {
		super.focus();
		if(itemIds.size() > 0)
			itemIds.keySet().iterator().next().focus();
	}

    

	public HashMap<Object, HashMap<Object, Field<?>>> getEditablefields() {
		return editablefields;
	}

	public void setVisiableColumns(String ...cols){
		this.customerVisibleColumns = cols;
	}


	// Keyboard navigation
	private class KbdHandler implements Handler {
	    Action tab_next = new ShortcutAction("Shift",
	            ShortcutAction.KeyCode.TAB, null);
	    Action tab_prev = new ShortcutAction("Shift+Tab",
	            ShortcutAction.KeyCode.TAB,
	            new int[] {ShortcutAction.ModifierKey.SHIFT});
	    Action cur_down = new ShortcutAction("Down",
	            ShortcutAction.KeyCode.ARROW_DOWN, null);
	    Action cur_left = new ShortcutAction("Left",
	    		ShortcutAction.KeyCode.ARROW_LEFT, null);
	    Action cur_right = new ShortcutAction("Right",
	    		ShortcutAction.KeyCode.ARROW_RIGHT, null);
	    Action cur_up   = new ShortcutAction("Up",
	            ShortcutAction.KeyCode.ARROW_UP,   null);
	    Action enter    = new ShortcutAction("Enter",
	            ShortcutAction.KeyCode.ENTER,      null);

	    public Action[] getActions(Object target, Object sender) {
	        return new Action[] {tab_next, tab_prev, cur_down,
	                             cur_up, enter, cur_left, cur_right};
	    }

	    public void handleAction(Action action, Object sender,
	                             Object target) {
	        if (target instanceof TextField) {
	            TextField tf = (TextField) target;
	            ItemPropertyId ipId = (ItemPropertyId) tf.getData();
	            
	            Object propertyId = ipId.getPropertyId();
	            
	            // Find the index of the property
	            Object cols[] = table.getVisibleColumns();
	            int pidIndex = 0;
	            for (int i=0; i<cols.length; i++)
	                if (cols[i].equals(propertyId))
	                    pidIndex = i;
	            
	            Object newItemId     = null;
	            Object newPropertyId = null;
	            
	            // Move according to keypress
	            if (action == cur_down)
	                newItemId = container.nextItemId(ipId.getItemId());
	            else if (action == cur_up)
	                newItemId = container.prevItemId(ipId.getItemId());
	            else if (action == tab_next || action == enter || action == cur_right){
	            	if(pidIndex >= cols.length-1){
	            		//查找首个非只读列
	            		for(int i = 0; i < cols.length; i++){
	            			Object id  = cols[i];
	            			if(!readOnlyColumns.contains(id)){
	            				newPropertyId = id;
	            				break;
	            			}
	            		}
	            		newItemId = container.nextItemId(ipId.getItemId());
	            	}else
	            		newPropertyId = cols[Math.min(pidIndex+1, cols.length-1)];
	            }else if (action == tab_prev || action == cur_left){
	                newPropertyId = cols[Math.max(pidIndex-1, 0)];
	            }


	            // If tried to go past first or last, just stay there
	            if (newItemId == null)
	                newItemId = ipId.getItemId();
	            if (newPropertyId == null)
	                newPropertyId = ipId.getPropertyId();
	            
	            // On enter, just stay where you were. If we did
	            // not catch the enter action, the focus would be
	            // moved to wrong place.
	            Field newField = editablefields.get(newItemId).get(newPropertyId);
	            if (newField != null)
	                newField.focus();
	        } 
	    }
	}

	public BeanContainer<String, E> getContainer() {
		return container;
	}


	// This is needed for storing back-references
	class ItemPropertyId {
	    Object itemId;
	    Object propertyId;
	    
	    public ItemPropertyId(Object itemId, Object propertyId) {
	        this.itemId = itemId;
	        this.propertyId = propertyId;
	    }
	    
	    public Object getItemId() {
	        return itemId;
	    }
	    
	    public Object getPropertyId() {
	        return propertyId;
	    }
	}

}
