package org.woodship.luna.db;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class HierarchialEntity<E> extends IdEntity<E> implements
		HierarchialEntityInterface<E> {
}
