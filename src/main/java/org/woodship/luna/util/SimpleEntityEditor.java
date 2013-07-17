package org.woodship.luna.util;

import org.woodship.luna.db.IdEntity;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SimpleEntityEditor<E extends IdEntity<E>> extends Window  {
	
	protected boolean fieldsReadOnly;

	public SimpleEntityEditor(final JPAContainerItem<E> selectedItem,  
			final JPAContainer<E> mainContainer, final Table mainTable, boolean fieldsReadOnly) {
		this.fieldsReadOnly = fieldsReadOnly;
		this.setCaption("编辑/增加");
		Class<E> entityClass = selectedItem.getContainer().getEntityClass();
		
		final JPAContainerItemFieldGroup<E> fg = new JPAContainerItemFieldGroup<E>(entityClass);
		fg.setItemDataSource(selectedItem);
		fg.setBuffered(true);

		fg.setReadOnly(fieldsReadOnly);
		
		final VerticalLayout root = new VerticalLayout();
		root.setSpacing(true);
		root.setMargin(true);
		root.addComponent(createForm(fg,entityClass));
		if(!fieldsReadOnly){
			Layout foot = createFoot(selectedItem, mainContainer, mainTable, fg);
			root.addComponent(foot);
		}
		setContent(root);
		
		this.center();
		this.setModal(true);
	}
	

	public Layout createForm(JPAContainerItemFieldGroup<E> fg, Class<E> entityClass){
		final FormLayout formLayout = new FormLayout();
		formLayout.setSpacing(true);
		Utils.buildAndBindFieldGroupA(fg, entityClass, formLayout);
		return formLayout;
	}
	
	public boolean beforeCommit(JPAContainerItem<E> jpaitem, JPAContainerItemFieldGroup<E> fg){
		return true;
	}
	
	public void afterCommit(JPAContainerItem<E> jpaitem, JPAContainerItemFieldGroup<E> fg) throws CommitException{
	}
	
	/**
	 * 创建 foot, 如果不需要foot,可覆盖此方法,返回null
	 * @param selectedItem
	 * @param mainContainer
	 * @param mainTable
	 * @param fg
	 * @return
	 */
	public Layout createFoot(final JPAContainerItem<E> selectedItem,  final JPAContainer<E> mainContainer
			, final Table mainTable, final JPAContainerItemFieldGroup<E> fg){
		HorizontalLayout foot = new HorizontalLayout();
		//处理保存事件
		Button saveButton = new Button("保存");
		saveButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				try {
					if(beforeCommit(selectedItem,fg)){
						//编辑的直接提交即可
						fg.commit();
						
						//新增的需要单独处理
						if(selectedItem.getEntity().getId() == null){
							E p =fg.getItemDataSource().getEntity();
							mainContainer.addEntity(p);
						}
						mainTable.refreshRowCache();
						afterCommit( selectedItem,fg);
						Notification.show("保存成功",Type.WARNING_MESSAGE);
						SimpleEntityEditor.this.close();//关闭，防止再点击，重复增加
					}
				} catch (FieldGroup.CommitException e) {
					Utils.setCommitExceptionMsg(e, fg);
				}
			}
		});
		
		foot.addComponent(saveButton);
		foot.setSpacing(true);
		return foot;
	}

}


