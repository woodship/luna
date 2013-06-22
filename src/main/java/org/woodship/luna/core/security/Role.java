package org.woodship.luna.core.security;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;
import org.woodship.luna.db.IdEntity;

import com.vaadin.data.fieldgroup.Caption;

/**
 * 权限组.
 */
@Entity
@Table(name ="ROLE_", uniqueConstraints = { @UniqueConstraint(columnNames = {"name"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role extends IdEntity<Role>{
	private static final long serialVersionUID = 1L;
	public  static final String SUPER_ADMIN_ROLE_NAME = "超级管理员";
	
	@NotEmpty
	@Caption("角色名称")
	private String name;
	
	@Caption("数据范围")
	@NotNull
	private RoleDataScope dataScope;
	
	@Caption("内置角色")
	private boolean sysRole;
	
	@Caption("备注")
	private String remark;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<Resource> resource = new LinkedHashSet<Resource>();
	
	@ManyToMany(fetch=FetchType.EAGER)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<User> users = new LinkedHashSet<User>();

	public Role(String name) {
		this.name = name;
	}

	public Role(String name, String desc) {
		this.name = name;
		this.remark = desc;
	}

	public Role() {
	}

	@Transient
	public List<String> toPermissionNames() {
		List<String> permissionNameList = new ArrayList<String>();
		for (Resource res : resource) {
			permissionNameList.add(res.getResKey());
		}
		return permissionNameList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Set<Resource> getResource() {
		return resource;
	}

	public void setResource(Set<Resource> resource) {
		this.resource = resource;
	}

	public boolean isSysRole() {
		return sysRole;
	}

	public void setSysRole(boolean sysRole) {
		this.sysRole = sysRole;
	}

	public void addResource(Resource res){
		if(res != null){
			this.resource.add(res);
		}
	}

	public void addUser(User user) {
		if(user != null){
			users.add(user);
		}
		
	}


	public RoleDataScope getDataScope() {
		return dataScope;
	}

	public void setDataScope(RoleDataScope dataScope) {
		this.dataScope = dataScope;
	}

	@Override
	public String toString() {
		return "Role [" + name + "]";
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
		Role other = (Role) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
