package org.woodship.luna.eam;

import javax.persistence.Entity;

import org.hibernate.validator.constraints.NotEmpty;
import org.woodship.luna.db.IdEntity;

import com.vaadin.data.fieldgroup.Caption;

@Entity
public class Customer extends IdEntity<Customer>{
	private static final long serialVersionUID = 1L;

	@Caption("编号")
	@NotEmpty
	private String cusNum;
	
	@Caption("名称")
	private String name;
	
	@Caption("备注")
	private String remark;

	public Customer() {
	}
	
	
	public Customer(String cusNum, String name, String remark) {
		super();
		this.cusNum = cusNum;
		this.name = name;
		this.remark = remark;
	}


	public String getCusNum() {
		return cusNum;
	}

	public void setCusNum(String cusNum) {
		this.cusNum = cusNum;
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
	@Override
	public String toString() {
		return cusNum +  (name==null?"":":"+name);
	}
}
