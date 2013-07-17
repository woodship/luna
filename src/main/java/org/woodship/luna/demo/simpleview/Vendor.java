package org.woodship.luna.demo.simpleview;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.woodship.luna.db.IdEntity;

import com.vaadin.data.fieldgroup.Caption;

@SuppressWarnings("serial")
@Entity
@Table( uniqueConstraints = { @UniqueConstraint(columnNames = {"name"})})
public class Vendor  extends IdEntity<Vendor>{
	
	@NotNull
	@Caption("名称")
	private String name;
	
	@Caption("地址")
	private String address;
	
	@Caption("联系人1")
	private String contacts1;
	
	@Caption("联系人1电话")
	private String contacts1tel;
	
	@Caption("联系人2")
	private String contacts2;
	
	@Caption("联系人2电话")
	private String contacts2tel;
	
	@Caption("开户行信息1")
	private String bank1;
	
	@Caption("开户行信息2")
	private String bank2;
	
	@Caption("备注")
	private String remark;

	public Vendor() {
	}
	public Vendor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContacts1() {
		return contacts1;
	}

	public void setContacts1(String contacts1) {
		this.contacts1 = contacts1;
	}

	public String getContacts1tel() {
		return contacts1tel;
	}

	public void setContacts1tel(String contacts1tel) {
		this.contacts1tel = contacts1tel;
	}

	public String getContacts2() {
		return contacts2;
	}

	public void setContacts2(String contacts2) {
		this.contacts2 = contacts2;
	}

	public String getContacts2tel() {
		return contacts2tel;
	}

	public void setContacts2tel(String contacts2tel) {
		this.contacts2tel = contacts2tel;
	}

	public String getBank1() {
		return bank1;
	}

	public void setBank1(String bank1) {
		this.bank1 = bank1;
	}

	public String getBank2() {
		return bank2;
	}

	public void setBank2(String bank2) {
		this.bank2 = bank2;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return name ;
	}
	
	
}
