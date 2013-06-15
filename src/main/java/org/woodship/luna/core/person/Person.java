package org.woodship.luna.core.person;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;
import org.woodship.luna.core.security.RoleDataScope;
import org.woodship.luna.db.IdEntity;

import com.vaadin.data.fieldgroup.Caption;

@SuppressWarnings("serial")
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"workNum"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Person extends IdEntity<Person>{

	@NotEmpty
    @Caption("姓名")
    private String trueName;
	@NotEmpty
    @Caption("工号")
    private String workNum;
    @Caption("电话")
    private String phoneNumber;
    @Caption("身份证")
    private String idCard;
    @Caption("现住址")
    private String address;
    @NotNull
    @ManyToOne
    @Caption("部门")
    private Organization org;
    
    @ManyToOne
//    @Caption("单位")
    private Organization company;
    
    @ManyToOne
//    @Caption("顶级部门")
    private Organization topDepartment;
    
    
    public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public String getStreet() {
        return address;
    }

    public void setStreet(String street) {
        this.address = street;
    }


	public String getWorkNum() {
		return workNum;
	}

	public void setWorkNum(String workNum) {
		this.workNum = workNum;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

	public Organization getOrg() {
		return org;
	}

	/**
	 * 同时设置所在单位与顶级部门
	 * @param org
	 */
	public void setOrg(Organization org) {
		this.org = org;
		this.company = null;
		this.topDepartment = null;
		Organization porg = org;
		while(porg != null){
			if(OrgType.顶级部门.equals(porg.getOrgType())){
				topDepartment = porg;
			}else if(OrgType.单位.equals(porg.getOrgType())){
				company = porg;
			}
			porg = porg.getParent();
		}
	}
	
	public Organization getCompany() {
		return company;
	}

	public Organization getTopDepartment() {
		return topDepartment;
	}
	
	public void setCompany(Organization company) {
		this.company = company;
	}

	public void setTopDepartment(Organization topDepartment) {
		this.topDepartment = topDepartment;
	}

	@Override
	public String toString(){
		return topDepartment+"/"+ org.getName()+":"+trueName;
	}
	
	public Organization getOrgByScope(RoleDataScope scope){
		if(RoleDataScope.本单位.equals(scope)){
			return getCompany();
		}else if(RoleDataScope.本顶级部门.equals(scope)){
			return getTopDepartment();
		}else if(RoleDataScope.本部门.equals(scope)){
			return getOrg();
		}
		return null;
	}
}
