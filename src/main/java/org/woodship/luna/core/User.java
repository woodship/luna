package org.woodship.luna.core;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.woodship.luna.base.Department;
import org.woodship.luna.db.IdEntity;

import com.vaadin.data.fieldgroup.Caption;
@Entity
@Table(name="USER_")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends IdEntity<User>{
	private static final long serialVersionUID = 1L;
	public User(){
	}
	
	public User(String username, String password, String truename, Department dev,
			Duty duty,String email,String phone,long qq , String welcome , String mobile) {
		this.username = username;
		this.password = password;
		this.truename = truename;
		this.department = dev;
		this.duty = duty;
		this.email = email;
		this.phone = phone;
		this.qq = qq;
		this.welcome = welcome;
		this.mobile = mobile;
	}

	@Caption("\u7528\u6237\u540D")
	@NotNull
	@Size(min = 2)
	private String username;

	private String password;

	@Size(min = 2)
	@NotBlank
	@Caption("\u59D3\u540D")
	private String truename;

	@Email
	@Caption("Email")
	private String email;
	
	@Caption("\u6027\u522B")
	private String sex;
	
	@Caption("\u624B\u673A")
	@NotNull
	private String phone;
	
	@Caption("QQ")
	private long qq;
	
	private String welcome;
	
	@Caption("\u56FA\u8BDD")
	private String mobile;

	@NotNull
	@ManyToOne
	@Caption("\u804C\u52A1")
	private Duty duty;
	
	@NotNull
	@ManyToOne
	@Caption("\u90E8\u95E8")
	private Department department;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Set<Role> roles = new LinkedHashSet<Role>();

	public void addRole(Role role){
		roles.add(role);
	}
	

	@Override
	public String toString() {
		return  truename ;
	}


	public String getWelcome() {
		if (welcome == null) {
			return truename;
		}
		return welcome;
	}

	public void setWelcome(String welcome) {
		this.welcome = welcome;
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

	public String getTruename() {
		return truename;
	}

	public void setTruename(String truename) {
		this.truename = truename;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public long getQq() {
		return qq;
	}

	public void setQq(long qq) {
		this.qq = qq;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Duty getDuty() {
		return duty;
	}

	public void setDuty(Duty duty) {
		this.duty = duty;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	


}
