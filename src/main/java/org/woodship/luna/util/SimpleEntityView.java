package org.woodship.luna.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.vaadin.dialogs.ConfirmDialog;
import org.woodship.luna.LunaException;
import org.woodship.luna.db.IdEntity;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;


public class SimpleEntityView<E extends IdEntity<E>> extends VerticalLayout implements ComponentContainer, View{
	private static final long serialVersionUID = 357151254609566821L;

	protected Table mainTable;

	protected TextField searchField;

	protected Button newButton;
	protected Button deleteButton;
	protected Button editButton;

	protected JPAContainer<E> mainContainer;

	
	protected Class<E> entityClass;

	public SimpleEntityView(Class<E> entityClass){
		this.entityClass = entityClass;
	}

	@PostConstruct
	public void PostConstruct(){
		mainContainer = Utils.getJPAContainer(entityClass);
		Filter defaultFilter = createDefaultFilter();
		if(defaultFilter != null){
			mainContainer.addContainerFilter(defaultFilter);
		}
		buildMainArea();
		authenticate() ;
	}

	private void buildMainArea() {
		this.setSizeFull();

		mainTable = new Table(null, mainContainer);

		mainTable.setSelectable(true);
		mainTable.setRowHeaderMode(RowHeaderMode.INDEX);
		mainTable.setSizeFull();
		//     mainTable.setColumnWidth(null, 24);//设置序号列宽度
		mainTable.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				E entity = null;
				Object iid = event.getProperty().getValue();
				if(iid != null){
					entity = mainContainer.getItem(iid).getEntity();
					deleteButton.setEnabled(iid != null);
					editButton.setEnabled(iid != null);
				}
				
				onTableSelected(entity);
			}

		});

		// personTable.setSelectable(true);
		mainTable.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					mainTable.select(event.getItemId());
				}
			}
		});

		configTable(mainTable, entityClass);

		HorizontalLayout toolbar = new HorizontalLayout();
		newButton = new Button("增加");
		newButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					final JPAContainerItem<E> newItem = (JPAContainerItem<E>) mainContainer.createEntityItem(entityClass.newInstance());
					SimpleEntityEditor<E> editor = createEditor( newItem,mainContainer,mainTable);
					UI.getCurrent().addWindow(editor);
				} catch (Exception e) {
					e.printStackTrace();
					throw new LunaException("创建编辑窗口失败，请联系管理员");
				}
			}
		});

		deleteButton = new Button("删除");
		deleteButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(UI.getCurrent(),"警告","确定要删除吗？删除后将不能恢复！","确定","取消",
						new ConfirmDialog.Listener() {
					@Override
					public void onClose(ConfirmDialog dialog) {
						if(dialog.isConfirmed()){
							mainContainer.removeItem(mainTable.getValue());
						}
					}
				});
			}
		});
		deleteButton.setEnabled(false);

		editButton = new Button("编辑");
		editButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				final JPAContainerItem<E> item = (JPAContainerItem<E>) mainContainer.getItem(mainTable.getValue());
				SimpleEntityEditor<E> editor = createEditor(item,mainContainer, mainTable);
				UI.getCurrent().addWindow(editor);
			}
		});
		editButton.setEnabled(false);


		searchField = new TextField();
		searchField.setInputPrompt("输入关键字搜索");
		searchField.addTextChangeListener(new TextChangeListener() {
			@Override
			public void textChange(TextChangeEvent event) {
				updateFilters();
			}
		});

		toolbar.addComponent(newButton);
		toolbar.addComponent(deleteButton);
		toolbar.addComponent(editButton);
		ArrayList<Component> coms = new ArrayList<Component>();
		addToolbarComponents(coms);
		for(Component c : coms){
			toolbar.addComponents(c);
		}
		toolbar.addComponent(searchField);
		toolbar.setWidth("100%");
		toolbar.setExpandRatio(searchField, 1);
		toolbar.setComponentAlignment(searchField, Alignment.TOP_RIGHT);
		toolbar.setMargin(true);
		toolbar.setSpacing(true);


		VerticalLayout verticalLayout = new VerticalLayout();
		this.addComponent(verticalLayout);
		verticalLayout.addComponent(toolbar);
		verticalLayout.addComponent(mainTable);
		verticalLayout.setExpandRatio(mainTable, 1);
		verticalLayout.setSizeFull();

	}


	private void updateFilters() {
		
		String textFilter = searchField.getValue();
		if (textFilter  != null && !textFilter.equals("")) {
			Filter f = createSearchFilter(textFilter);
			if(f != null){
				mainContainer.setApplyFiltersImmediately(false);
				mainContainer.removeAllContainerFilters();
				
				//加载defaultFilter
				Filter defaultFilter = createDefaultFilter();
				if(defaultFilter != null){
					mainContainer.addContainerFilter(defaultFilter);
				}
				
				mainContainer.addContainerFilter(f);
				mainContainer.applyFilters();
			}
		}
	}
	

	@Override
	public void enter(ViewChangeEvent event) {
		mainContainer.refresh();
	}

	private void authenticate() {
		Subject user = SecurityUtils.getSubject(); 
		newButton.setVisible(user.isPermitted(Utils.getAddActionKey(this.getClass())));
		deleteButton.setVisible(user.isPermitted(Utils.getDelActionKey(this.getClass())));
		editButton.setVisible(user.isPermitted(Utils.getEditActionKey(this.getClass())));
	}

	public Class<E> getEntityClass() {
		return entityClass;
	}
	
	public SimpleEntityEditor<E> createEditor(final JPAContainerItem<E> jpaitem,  final JPAContainer<E> mainContainer, final Table mainTable){
		return new SimpleEntityEditor<E>(jpaitem, mainContainer, mainTable, false);
	}
	
	/**
	 * 子类可以覆盖此方法，配制table要显示的列
	 * @param table
	 * @param entityClass
	 */
	public void configTable(Table table, Class<E> entityClass){
		Utils.configTableHead(mainTable, entityClass);
	}

	/**
	 * 子类可以覆盖此方法，定制要搜索的字段
	 * @param textFilter
	 * @return
	 */
	public Filter createSearchFilter(String textFilter){
		return null;
	}
	
	/**
	 * 子类可以覆盖此方法，定制默认加载时使用的过滤条件
	 * @param textFilter
	 * @return
	 */
	public Filter createDefaultFilter(){
		return null;
	}
	
	/**
	 * 子类可以覆盖此方法，添加工具栏按钮
	 * @param components
	 */
	public void addToolbarComponents(final ArrayList<Component> components) {
	}
	
	/**
	 * 子类可以覆盖此方法，实现不同状态下隐藏按钮等
	 * @param selectedEntity
	 */
	public  void onTableSelected(E selectedEntity) {
	}
	
	public JPAContainerItem<E> getSelectJPAItem(){
		final JPAContainerItem<E> item = (JPAContainerItem<E>) mainContainer.getItem(mainTable.getValue());
		return item;
	}
	
}
