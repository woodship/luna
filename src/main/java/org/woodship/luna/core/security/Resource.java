package org.woodship.luna.core.security;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.h2.util.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.woodship.luna.db.HierarchialEntity;

import com.vaadin.data.fieldgroup.Caption;
import com.vaadin.navigator.View;


/**
 * 系统资源，用于功能菜单，功能按钮的标识，进行权限控制
 * @author laocui
 */
@Entity
@Table(name = "Resource_", uniqueConstraints = { @UniqueConstraint(columnNames = {"resKey", "path","resType" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Resource extends HierarchialEntity<Resource>{

	private static final long serialVersionUID = 1L;

	public Resource() {
	}

	public Resource(String resKey, String name,  ResourceType resType) {
		this.resKey = resKey;
		this.name = name;
		this.resType = resType;
	}
	
	public Resource(String resKey,String name, ResourceType resType, Resource parent) {
		this.resKey = resKey;
		this.name = name;
		this.resType = resType;
		this.setParent(parent);
	}
	
	public Resource(String resKey,String name, ResourceType resType, Resource parent, String path, Class<? extends View> viewClass) {
		this.resKey = resKey;
		this.name = name;
		this.path = path;
		this.resType = resType;
		this.setParent(parent);
		this.viewClass = viewClass;
	}

	
	
	@Caption("名称")
	private String name;
	
	@Caption("访问路径")
	private String path;

	@Caption("上级")
	@ManyToOne
	private Resource parent;
	
	@Caption("视图类")
	private Class<? extends View> viewClass;

	@Caption("类型")
	private ResourceType resType;

	@Caption("显示图标")
	private String icon;
	
	@Caption("资源KEY")
	private String resKey;

//	@Caption("叶子节点")
	private boolean leaf = true;;
	@ManyToMany(fetch=FetchType.EAGER ,mappedBy="resource")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE )
	private Set<Role> roles = new LinkedHashSet<Role>();
	
	
	
	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		if(parent != null){
			parent.setLeaf(false);
		}
		this.parent = parent;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResKey() {
		return resKey;
	}

	public void setResKey(String resKey) {
		this.resKey = resKey;
	}

	
}
