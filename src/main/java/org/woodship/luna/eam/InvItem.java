package org.woodship.luna.eam;

import javax.persistence.Entity;

import org.hibernate.validator.constraints.NotEmpty;
import org.woodship.luna.db.IdEntity;

import com.vaadin.data.fieldgroup.Caption;

/**
 * 库存项目(型号)
 * @author Administrator
 */
@Entity
public class InvItem extends IdEntity<InvItem>{
	private static final long serialVersionUID = 1L;

	@Caption("型号")
	@NotEmpty
	private String model;
	
	@Caption("定额")
	private Float quota;
	
	@Caption("备注")
	private String remark;

	public InvItem() {
	}
	public InvItem(String model, Float quota, String remark) {
		this.model = model;
		this.quota = quota;
		this.remark = remark;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	public Float getQuota() {
		return quota;
	}

	public void setQuota(Float quota) {
		this.quota = quota;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
