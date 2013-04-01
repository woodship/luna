package org.woodship.luna.core;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.h2.util.StringUtils;
import org.woodship.luna.base.PersonView;
import org.woodship.luna.db.IdEntity;

import com.vaadin.navigator.View;



/**
 * 系统资源，可生成菜单，进行权限控制
 * @author laocui
 */
@Entity
@Table(name = "Resource_", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"name", "path" }) })
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Resource extends IdEntity<Resource>{

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

	@Enumerated(EnumType.STRING)
	private ResourceType resType;

	private String icon;
	@ManyToOne
	private Resource parent;

	@Transient
	private List<Resource> children = new ArrayList<Resource>();

	public boolean isLeaf() {
		if (ResourceType.MODULE.equals(resType)) {
			return false;
		}
		return true;
	}
	
	public String getIcon(){
		if(!StringUtils.isNullOrEmpty(icon)){
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
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 注意目前仅支持两层
	 * @return
	 */
	public static List<Resource> getDemoResoures(){
		List<Resource> res = new  ArrayList<Resource>();
		
		//增加一个模块
		Resource sys = new Resource("系统管理", ResourceType.MODULE);
		//建立应用
		Resource app = new Resource("应用管理", ResourceType.APPLICATION, sys, "/application", ApplicationView.class);
		//把该应用增加到模块下
		sys.add(app);//
		res.add(sys);
		
		//增加一个模块
		Resource base = new Resource("基础应用", ResourceType.MODULE);
		//建立应用
		Resource person = new Resource("人员管理", ResourceType.APPLICATION, base, "/person", PersonView.class);
		//把该应用增加到模块下
		base.add(person);//
		res.add(base);
		
	
		return res;
	}
}
