package org.woodship.luna.db;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
@MappedSuperclass
public  class IdEntity<E> implements Serializable{
	private static final long serialVersionUID = 7904559135688465527L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
	protected String id;
	
	@Version
	protected Integer version;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	} 

	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
}
