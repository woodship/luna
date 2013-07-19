package org.woodship.luna.core.security;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Resource.class)
public abstract class Resource_ extends org.woodship.luna.db.HierarchialEntity_ {

	public static volatile SingularAttribute<Resource, String> icon;
	public static volatile SingularAttribute<Resource, Class> viewClass;
	public static volatile SingularAttribute<Resource, String> resKey;
	public static volatile SetAttribute<Resource, Role> roles;
	public static volatile SingularAttribute<Resource, String> name;
	public static volatile SingularAttribute<Resource, String> path;
	public static volatile ListAttribute<Resource, Resource> ancestors;
	public static volatile SingularAttribute<Resource, Resource> parent;
	public static volatile SingularAttribute<Resource, ResourceType> resType;

}

