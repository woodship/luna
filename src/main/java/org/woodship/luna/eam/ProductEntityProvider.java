package org.woodship.luna.eam;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.woodship.luna.core.person.Organization;
import org.woodship.luna.core.security.UserService;
import org.woodship.luna.db.TransactionalEntityProvider;

import com.vaadin.addon.jpacontainer.util.DefaultQueryModifierDelegate;

@Component
public class ProductEntityProvider  extends TransactionalEntityProvider<Product> {
	@Autowired
	UserService us;
	
	    @SuppressWarnings("serial")
		public ProductEntityProvider() {
	        super(Product.class);
	        setQueryModifierDelegate(
					new DefaultQueryModifierDelegate () {
						@SuppressWarnings("unchecked")
						@Override
						public void filtersWillBeAdded( CriteriaBuilder cb, CriteriaQuery<?> query, List<Predicate> predicates) {
							Root<Product> root = (Root<Product>) query.getRoots().iterator().next();
							List<Organization> orgs = us.getCurrCanReadOrg(null);
							if(orgs.size() == 0) {
								//无权限
								predicates.add(cb.isNull(root));
							}else{
								In<Organization> in = cb.in(root.get(Product_.org));
								for(Organization o: orgs ){
									in = in.value(o);
								}
								predicates.add(in);
							}
						}
					}
			);
	    }

	    
}