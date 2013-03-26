package org.woodship.luna.security;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.woodship.luna.DashboardView;
import org.woodship.luna.ReportsView;
import org.woodship.luna.SalesView;
import org.woodship.luna.ScheduleView;
import org.woodship.luna.TransactionsView;
import org.woodship.luna.base.PersonVeiw;

import com.vaadin.navigator.View;



/**
 * 系统资源，可生成菜单，进行权限控制
 * @author laocui
 */
public class Resource {

	public Resource(String name, ResourceType resType, Resource parent, String path, Class<? extends View> viewClass) {
		super();
		this.name = name;
		this.path = path;
		this.resType = resType;
		this.parent = parent;
		this.viewClass = viewClass;
	}

	public Resource() {
	}

	public Resource(String name, ResourceType resType) {
		this.name = name;
		this.resType = resType;
	}

	private String name;

	private String path;

	private Class<? extends View> viewClass;

	private ResourceType resType;

	private String icon;

	private Resource parent;

	private List<Resource> children = new ArrayList<Resource>();

	public boolean isLeaf() {
		if (ResourceType.MODULE.equals(resType)) {
			return false;
		}
		return true;
	}
	
	public String getIcon(){
		if(StringUtils.isBlank(icon)){
			return "icons/item.png";
		}
		return icon;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource other = (Resource) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String toString(){
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	public Class<? extends View> getViewClass() {
		return viewClass;
	}

	public void setViewClass(Class<? extends View> viewClass) {
		this.viewClass = viewClass;
	}

	public ResourceType getResType() {
		return resType;
	}

	public void setResType(ResourceType resType) {
		this.resType = resType;
	}

	public Resource getParent() {
		return parent;
	}

	public void setParent(Resource parent) {
		this.parent = parent;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public List<Resource> getChildren() {
		return children;
	}

	public void setChildren(List<Resource> children) {
		this.children = children;
	}
	
	public List<Resource> add(Resource child){
		this.children.add(child);
		return this.children;
	}
	
	/**
	 * 注意目前仅支持两层
	 * @return
	 */
	public static List<Resource> getDemoResoures(){
		List<Resource> res = new  ArrayList<Resource>();
		//保留原始示例模块
		Resource demo = new Resource("示例模块", ResourceType.MODULE);
		Resource dashboard = new Resource("dashboard", ResourceType.APPLICATION, demo, "/dashboard", DashboardView.class);
		Resource sales = new Resource("sales", ResourceType.APPLICATION, demo, "/sales", SalesView.class);
		Resource transactions = new Resource("transactions", ResourceType.APPLICATION, demo, "/transactions", TransactionsView.class);
		Resource reports = new Resource("reports", ResourceType.APPLICATION, demo, "/reports", ReportsView.class);
		Resource schedule = new Resource("schedule", ResourceType.APPLICATION, demo, "/schedule", ScheduleView.class);
		demo.add(dashboard);
		demo.add(sales);
		demo.add(transactions);
		demo.add(reports);
		demo.add(schedule);
		res.add(demo);
		
		//增加一个模块
		Resource base = new Resource("基础应用", ResourceType.MODULE);
		//建立应用
		Resource person = new Resource("人员管理", ResourceType.APPLICATION, base, "/person", PersonVeiw.class);
		//把该应用增加到模块下
		base.add(person);//
		res.add(base);
	
		return res;
	}
}
