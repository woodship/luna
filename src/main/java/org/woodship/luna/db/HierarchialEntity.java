package org.woodship.luna.db;

import java.lang.reflect.ParameterizedType;

@SuppressWarnings("serial")
public abstract class HierarchialEntity<E> extends IdEntity<E> implements
		HierarchialEntityInterface<E> {
	
}
