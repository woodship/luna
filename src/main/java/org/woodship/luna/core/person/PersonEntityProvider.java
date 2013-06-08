package org.woodship.luna.core.person;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.core.security.UserService;
import org.woodship.luna.db.TransactionalEntityProvider;

import com.vaadin.addon.jpacontainer.util.DefaultQueryModifierDelegate;

@Component
public class PersonEntityProvider  extends TransactionalEntityProvider<Person> {
	
		@Autowired
		UserService us;
		
	    public PersonEntityProvider() {
	        super(Person.class);
	        setQueryModifierDelegate(
					new DefaultQueryModifierDelegate () {
						private static final long serialVersionUID = -6863923383606277544L;

						@SuppressWarnings("unchecked")
						@Override
						public void filtersWillBeAdded( CriteriaBuilder cb, CriteriaQuery<?> query, List<Predicate> predicates) {
							List<Organization> orgs = us.getCurrCanReadOrg(null);
							if(orgs.size() == 0) return;
							
							Root<Person> root = (Root<Person>) query.getRoots().iterator().next();
							In<Organization> in = cb.in(root.get(Person_.org));
							for(Organization o: orgs ){
								in = in.value(o);
							}
							predicates.add(in);
						}
					}
			);
	    }

		@Override
		@Transactional(propagation = Propagation.REQUIRED)
		public void removeEntity(Object entityId) {
			us.removePersonUser(entityId.toString());
			super.removeEntity(entityId);
		}

	    
}