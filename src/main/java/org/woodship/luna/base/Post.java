package org.woodship.luna.base;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.woodship.luna.db.IdEntity;

/**
 * 岗位
 * @author Administrator
 */
@Entity
@Table(name="POST")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Post extends IdEntity<Post>{
	private static final long serialVersionUID = 1L;
	
	public Post(String name) {
		this.name = name;
	}

	public Post() {
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
