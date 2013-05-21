package org.woodship.luna.eam;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.woodship.luna.db.IdEntity;
import com.vaadin.data.fieldgroup.Caption;

@Entity
@Table(name = "Item_")
public class InvItem extends IdEntity<InvItem>{
	private static final long serialVersionUID = 1L;

	@Caption("型号")
	private String model;
	
	@Caption("定额")
	private float quota;
	
	@Caption("备注")
	private String remark;

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public float getQuota() {
		return quota;
	}

	public void setQuota(float quota) {
		this.quota = quota;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
