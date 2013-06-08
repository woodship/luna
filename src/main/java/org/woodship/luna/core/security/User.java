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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.woodship.luna.core.person.Person;
import org.woodship.luna.db.IdEntity;

import com.vaadin.data.fieldgroup.Caption;
@Entity
@Table(name="USER_")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends IdEntity<User>{
	private static final long serialVersionUID = 1L;
	public static final String SUPER_ADMIN_USERNAME = "admin";
	public static final String DEFAULT_PASSWORD = "111";
	public User(){
		//不能在构造函数中增加过多的东西，不然会使整个应用缓慢
		//this.password = ps.encryptPassword(DEFAULT_PASSWORD);
	}

	/**
	 * @param username
	 * @param password 明码，会自动转换成密码
	 * @param showName
	 */
	public User(String username, String password,  String showName ) {
		this.username = username;
		setPassword(password);
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

	@Caption("关联人员")
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

	/**
	 * 明码，不会被自动转换成密码，需要自已在设置前转换
	 * @param password 明码
	 */
	public void setPassword(String password) {
		this.password =password;
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
		if(StringUtils.isEmpty(username)){
			this.username = person.getWorkNum();
		}
		if(StringUtils.isEmpty(showName)){
			this.showName = person.getTrueName();
		}
	}


	public boolean isSysUser() {
		return sysUser;
	}

	public void setSysUser(boolean sysUser) {
		this.sysUser = sysUser;
	}
	
	public void clearUserRoles(){
		for(Role role : getRoles()){
			role.getUsers().remove(this);
		}
		this.setRoles(null);
	}

}
