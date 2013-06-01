package org.woodship.luna.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class HierarchialEntity<E> implements Serializable {

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid.hex")
	protected String id;

	@Version
	protected Integer version;

	protected boolean leaf = true;

	protected int treeLevel;

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

	public abstract E getParent();

	public abstract void setParent(E parent);

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public abstract List<E> getAncestors();

	public abstract void setAncestors(List<E> ancestors);

	public int getTreeLevel() {
		return treeLevel;
	}

	public void setTreeLevel(int treeLevel) {
		this.treeLevel = treeLevel;
	}

}
