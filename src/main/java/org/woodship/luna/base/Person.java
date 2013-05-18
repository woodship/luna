package org.woodship.luna.base;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.woodship.luna.db.IdEntity;

import com.vaadin.data.fieldgroup.Caption;

@SuppressWarnings("serial")
@Entity
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

	public void setOrg(Organization org) {
		this.org = org;
	}

}
