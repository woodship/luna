package org.woodship.luna.core.security;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class RoleResEditor extends Window  {
	private JPAContainerItem<Role> jpaitem = null;
	private TreeTable ttable;
	@SuppressWarnings("unchecked")
	public RoleResEditor(final Item item,  final JPAContainer<Role> container, final JPAContainer<Resource> resContainer) {
		this.setCaption("角色关联功能设置");
		setWidth(500, Unit.PIXELS);
		setHeight(100, Unit.PERCENTAGE);
		this.jpaitem = (JPAContainerItem<Role>) item;

		ttable = new TreeTable();
		ttable.setContainerDataSource(resContainer);
		ttable.setItemCaptionPropertyId("name");
		ttable.setVisibleColumns(new Object[]{"name","resType"});
		ttable.setColumnHeader("name","应用名");
		ttable.setColumnHeader("resType","类型");
		ttable.setMultiSelect(true);
		ttable.setSelectable(true);
		ttable.setMultiSelectMode(MultiSelectMode.SIMPLE);
		ttable.setImmediate(true);
		ttable.setSizeFull();
		// Expand the tree
		for (Object itemId: ttable.getItemIds()){
			ttable.setCollapsed(itemId, false);
		}
		//设置值
		Role r = jpaitem.getEntity();
		for(Resource res : r.getResource()){
			ttable.select(res.getId());
		}
		
		//选中子节点同时选中父节点
		//TODO 全部取消一个模块，全部选择一个模块
		ttable.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
//				Collection<Object> changedid = (Collection<Object>) event.getProperty().getValue();
//				for(Object id : changedid){
//					Resource chRes = resContainer.getItem(id).getEntity();
//					System.out.println(chRes);
//				}
				Collection<Object> ids =  (Collection<Object>) ttable.getValue();
				for(Object id : ids){
					Object pid =ttable.getParent(id);
					while(pid != null){
						ttable.select(pid);
						pid = ttable.getParent(pid);
					}
				}
			}
		});

	

		//处理保存事件
		Button saveButton = new Button("保存");
		saveButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				Set<?> v = (Set<?>) ttable.getValue();
				Set<Resource> res = new LinkedHashSet<Resource>();
				for(Object rid : v){
					res.add(resContainer.getItem(rid).getEntity() );
				}
				jpaitem.getItemProperty("resource").setValue(res);
				container.commit();
				Notification.show("保存成功");
			}
		});

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponent(saveButton);
		buttons.setMargin(true);
		
		final VerticalLayout formLayout = new VerticalLayout();
		formLayout.setSizeFull();
		formLayout.setMargin(true);
		formLayout.addComponent(ttable);
		formLayout.addComponent(buttons);
		formLayout.setExpandRatio(ttable, 1);
		
		setContent(formLayout);
	}


}
