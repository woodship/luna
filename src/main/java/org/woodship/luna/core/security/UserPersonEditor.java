package org.woodship.luna.core.security;

import java.util.Set;

import org.woodship.luna.core.person.Person;
import org.woodship.luna.core.person.Person_;
import org.woodship.luna.util.Utils;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class UserPersonEditor extends Window  {
	private Table table;
	public UserPersonEditor(final JPAContainer<User> userCon, final JPAContainer<Person> personCon) {
		this.setCaption("从人员创建用户（按Ctrl键可多选）");
		setWidth(400, Unit.PIXELS);
		setHeight(100, Unit.PERCENTAGE);
	
		
		table = new Table();
		table.setSizeFull();
		table.setMultiSelect(true);
		table.setSelectable(true);
		
		//过虑
		//TODO 提高性能
		personCon.removeAllContainerFilters();
		personCon.setApplyFiltersImmediately(false);
		for(Object uid : userCon.getItemIds()){
			User u = userCon.getItem(uid).getEntity();
			if( u.getPerson() != null){
				personCon.addContainerFilter(new Not(new Equal(Person_.id.getName(),u.getPerson().getId())));
			}
		}
		personCon.applyFilters();
		table.setContainerDataSource(personCon);
		table.setVisibleColumns(new Object[]{Person_.topDepartment.getName(),Person_.trueName.getName(),Person_.workNum.getName()});
		table.setColumnHeader(Person_.topDepartment.getName(), "部门");
		table.setColumnHeader(Person_.trueName.getName(), "姓名");
		table.setColumnHeader(Person_.workNum.getName(), "工号");
//		

		//处理保存事件
		Button saveButton = new Button("确定");
		saveButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
					Set<?> v = (Set<?>) table.getValue();
					for(Object rid : v){
						Person person = personCon.getItem(rid).getEntity();
						User u = new User();
						u.setPerson(person);
						u.setPassword(Utils.DEFAULT_PASSWORD);
						userCon.addEntity(u);
						//不再显示
						personCon.addContainerFilter(new Not(new Equal(Person_.id.getName(),person.getId())));
					}
					personCon.applyFilters();
//					UserPersonEditor.this.close();
					table.setValue(null);
			}
		});
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setMargin(true);
		buttons.addComponent(saveButton);
		buttons.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
		
		final VerticalLayout formLayout = new VerticalLayout();
		formLayout.setSizeFull();
		formLayout.setMargin(true);
		formLayout.addComponent(table);
		formLayout.addComponent(buttons);
		formLayout.setExpandRatio(table, 1);
		setContent(formLayout);
	}
	
	
}
