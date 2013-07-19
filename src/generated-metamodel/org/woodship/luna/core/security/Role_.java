package org.woodship.luna.core.security;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Role.class)
public abstract class Role_ extends org.woodship.luna.db.IdEntity_ {

	public static volatile SetAttribute<Role, User> users;
	public static volatile SingularAttribute<Role, RoleDataScope> dataScope;
	public static volatile SingularAttribute<Role, String> remark;
	public static volatile SingularAttribute<Role, String> name;
	public static volatile SetAttribute<Role, Resource> resource;
	public static volatile SingularAttribute<Role, Boolean> sysRole;

}

