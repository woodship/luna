package org.woodship.luna.db;


/**
 * 树要实现该接口
 * @author Administrator
 *
 * @param <T>
 */
public interface HierarchialEntityInterface<T> {
	public boolean isLeaf() ;
	public T getParent();
}
