package org.woodship.luna.core.security;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;
import org.woodship.luna.core.person.Person;
import org.woodship.luna.db.IdEntity;

import com.vaadin.data.fieldgroup.Caption;
@Entity
@Table(name="USER_")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends IdEntity<User>{
	private static final DefaultPasswordService ps = new DefaultPasswordService();
	private static final long serialVersionUID = 1L;
	public static final String SUPER_ADMIN_USERNAME = "admin";
	public static final String DEFAULT_PASSWORD = "111";
	public User(){
		this.password = ps.encryptPassword(DEFAULT_PASSWORD);
	}

	public User(String username, String showName ) {
		this.username = username;
		this.password = ps.encryptPassword(DEFAULT_PASSWORD);;
		this.showName = showName;
	}

	@Caption("登录名")
	@NotEmpty
	private String username;
	
//	@Caption("密码")
	private String password;

	@NotEmpty
	@Caption("显示名")
	private String showName;
	
	@Caption("系统用户")
	private boolean sysUser;

	@Caption("人员信息")
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


	public boolean isSysUser() {
		return sysUser;
	}

	public void setSysUser(boolean sysUser) {
		this.sysUser = sysUser;
	}


}
