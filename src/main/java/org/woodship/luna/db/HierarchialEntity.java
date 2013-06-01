package org.woodship.luna.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class HierarchialEntity<E extends HierarchialEntity<E>> implements Serializable {

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


	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public int getTreeLevel() {
		return treeLevel;
	}

	public void setTreeLevel(int treeLevel) {
		this.treeLevel = treeLevel;
	}

	public abstract List<E> getAncestors() ;

	public abstract void setAncestors(List<E> ancestors);

	public abstract  E getParent();

	/**
	 * 设置父级的同时，设置祖先，子类最好不要覆盖该方法
	 * @param parent
	 */
	public  void setParent(E parent) {
		if(this.getAncestors() == null){
			return;
		}
		this.getAncestors().clear();
		this.getAncestors().add((E) this);
		if(parent == null){
			return;
		}
		E p = parent;
		p.setLeaf(false);
		//增加祖先
		while(p != null){
			getAncestors().add((E) p);
			p =p.getParent();
		}
	}
	
}
