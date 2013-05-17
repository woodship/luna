package org.woodship.luna.core;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.woodship.luna.db.IdEntity;

@Entity
@Table(name="DUTY_")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Duty extends IdEntity<Duty>{
	private static final long serialVersionUID = 1L;
	public static final String ZONG_JING_LI = "\u603B\u7ECF\u7406";
	public static final String BU_MENG_JING_LI = "\u90E8\u95E8\u7ECF\u7406";
	public static final String ZHI_YUAN = "\u804C\u5458";
	public static final String ADMIN = "\u7BA1\u7406\u5458";
	
	public Duty(String name) {
		this.name = name;
	}

	public Duty() {
	}

	@NotNull
	@Size(min = 2)
	private String name;
	
	private String remark;
	
	@Override
    public String toString(){
    	return name;
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
	
}
