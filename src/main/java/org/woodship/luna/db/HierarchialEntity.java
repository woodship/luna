package org.woodship.luna.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.woodship.luna.LunaException;

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
		if(parent == null){
			if(this.getAncestors() != null){
				this.getAncestors().clear();
			}
			return;
		}
		if(parent.equals(this)){
			throw new LunaException("上级不能选择自已！");
		}
		parent.setLeaf(false);
		this.getAncestors().add((E) this);
		E p = parent;
		//增加祖先
		while(p != null){
			if(p.equals(this)){
				throw new LunaException("上级不能选择自已的下级！");
			}
			getAncestors().add((E) p);
			p =p.getParent();
		}
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HierarchialEntity other = (HierarchialEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
