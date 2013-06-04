package org.woodship.luna.core.person;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.woodship.luna.core.security.User;
import org.woodship.luna.core.security.UserService;
import org.woodship.luna.db.TransactionalEntityProvider;

import com.vaadin.addon.jpacontainer.util.DefaultQueryModifierDelegate;

@Component
public class OrganizationEntityProvider  extends TransactionalEntityProvider<Organization> {
		@Autowired
		UserService us;
		
		public OrganizationEntityProvider() {
	        super(Organization.class);
	        setQueryModifierDelegate(
					new DefaultQueryModifierDelegate () {
						private static final long serialVersionUID = 5267750453795364093L;
						@SuppressWarnings("unchecked")
						@Override
						public void filtersWillBeAdded( CriteriaBuilder cb, CriteriaQuery<?> query, List<Predicate> predicates) {
							User user = us.getCurrentUser();
							Predicate p = us.getCanReadOrgPredicate(user, null, cb, (CriteriaQuery<Organization>) query,true);
							predicates.add(p);
						}
					}
			);
	    }
	    
}