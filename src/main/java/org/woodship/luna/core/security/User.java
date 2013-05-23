package org.woodship.luna.core.security;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;
import org.woodship.luna.base.Person;
import org.woodship.luna.db.IdEntity;

import com.vaadin.data.fieldgroup.Caption;
@Entity
@Table(name="USER_")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends IdEntity<User>{
	private static final long serialVersionUID = 1L;
	public static final String ADMIN_USERNAME = "admin";
	public static final String DEFAULT_PASSWORD = "111";
	public User(){
	}

	public User(String username, String password,  String showName ) {
		this.username = username;
		this.password = password;
		this.showName = showName;
	}

	@Caption("登录名")
	@NotEmpty
	private String username;
	@Caption("密码")
	private String password;

	@NotEmpty
	@Caption("显示名")
	private String showName;
	
	@Caption("系统用户")
	private boolean sysUser;

	@Caption("对应人员")
	@OneToOne
	private Person person;

	@ManyToMany(fetch=FetchType.EAGER ,mappedBy="users")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE )
	private Set<Role> roles = new LinkedHashSet<Role>();

	public void addRole(Role role){
		roles.add(role);
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
		this.username = person.getWorkNum();
		this.showName = person.getTrueName();
	}

	public boolean isAdmin(){
		//TODO 只要是管理员角色都应该返回true
		return User.ADMIN_USERNAME.equals(this.getUsername());
	}

	public boolean isSysUser() {
		return sysUser;
	}

	public void setSysUser(boolean sysUser) {
		this.sysUser = sysUser;
	}


}
