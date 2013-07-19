package org.woodship.luna.core.person;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Person.class)
public abstract class Person_ extends org.woodship.luna.db.IdEntity_ {

	public static volatile SingularAttribute<Person, Organization> topDepartment;
	public static volatile SingularAttribute<Person, String> idCard;
	public static volatile SingularAttribute<Person, String> trueName;
	public static volatile SingularAttribute<Person, String> phoneNumber;
	public static volatile SingularAttribute<Person, String> address;
	public static volatile SingularAttribute<Person, Organization> company;
	public static volatile SingularAttribute<Person, String> workNum;
	public static volatile SingularAttribute<Person, Organization> org;

}

