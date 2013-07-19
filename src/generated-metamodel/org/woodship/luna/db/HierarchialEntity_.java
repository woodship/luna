package org.woodship.luna.db;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(HierarchialEntity.class)
public abstract class HierarchialEntity_ {

	public static volatile SingularAttribute<HierarchialEntity, Long> id;
	public static volatile SingularAttribute<HierarchialEntity, Integer> treeLevel;
	public static volatile SingularAttribute<HierarchialEntity, Boolean> leaf;
	public static volatile SingularAttribute<HierarchialEntity, Integer> version;

}

