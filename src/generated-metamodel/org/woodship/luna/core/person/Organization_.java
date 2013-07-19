package org.woodship.luna.core.person;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Organization.class)
public abstract class Organization_ extends org.woodship.luna.db.HierarchialEntity_ {

	public static volatile SingularAttribute<Organization, OrgType> orgType;
	public static volatile SingularAttribute<Organization, String> name;
	public static volatile ListAttribute<Organization, Organization> ancestors;
	public static volatile SingularAttribute<Organization, Organization> parent;
	public static volatile SetAttribute<Organization, Person> persons;

}

