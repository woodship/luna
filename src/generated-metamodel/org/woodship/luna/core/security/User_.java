package org.woodship.luna.core.security;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.woodship.luna.core.person.Person;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(User.class)
public abstract class User_ extends org.woodship.luna.db.IdEntity_ {

	public static volatile SingularAttribute<User, String> username;
	public static volatile SingularAttribute<User, Person> person;
	public static volatile SingularAttribute<User, String> showName;
	public static volatile SingularAttribute<User, Boolean> sysUser;
	public static volatile SetAttribute<User, Role> roles;
	public static volatile SingularAttribute<User, String> password;

}

