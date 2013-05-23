package org.woodship.luna.core.security;

import java.util.LinkedHashSet;
import java.util.Set;

import org.woodship.luna.core.Resource;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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
		this.jpaitem = (JPAContainerItem<Role>) item;
		final VerticalLayout formLayout = new VerticalLayout();
		formLayout.setMargin(true);
		ttable = new TreeTable();
		ttable.setContainerDataSource(resContainer);
		ttable.setItemCaptionPropertyId("name");
		ttable.setVisibleColumns(new Object[]{"name","resType"});
		ttable.setColumnHeader("name","应用名");
		ttable.setColumnHeader("resType","类型");
		ttable.setWidth(300, Unit.PIXELS);
		ttable.setHeight(400, Unit.PIXELS);
		ttable.setMultiSelect(true);
		ttable.setSelectable(true);
		ttable.setMultiSelectMode(MultiSelectMode.SIMPLE);
		ttable.setImmediate(true);
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
		//TODO 未生效，需要进一步查找原因
		ttable.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				//选中时同时选中父节点
				if(!((Set<?>)ttable.getValue()).contains(event.getItemId())){
					Object pid = ttable.getParent(event.getItemId());
					while(pid != null){
						ttable.select(pid);
						pid = ttable.getParent(pid);
					}
				}
			}
		});

		formLayout.addComponent(ttable);
		final Label error = new Label("", ContentMode.HTML);
		error.setVisible(false);
		formLayout.addComponent(error);


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
		buttons.setMargin(true);
		buttons.addComponent(saveButton);
		formLayout.addComponent(buttons);
		formLayout.setComponentAlignment(buttons, Alignment.MIDDLE_LEFT);
		setContent(formLayout);
	}


}
