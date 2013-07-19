package org.woodship.luna.db;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(IdEntity.class)
public abstract class IdEntity_ {

	public static volatile SingularAttribute<IdEntity, Long> id;
	public static volatile SingularAttribute<IdEntity, Date> createDate;
	public static volatile SingularAttribute<IdEntity, Integer> version;

}

