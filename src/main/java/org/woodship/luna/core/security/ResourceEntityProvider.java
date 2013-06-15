package org.woodship.luna.core.security;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;
import org.woodship.luna.db.TransactionalEntityProvider;

import com.vaadin.addon.jpacontainer.util.DefaultQueryModifierDelegate;

@Component
public class ResourceEntityProvider  extends TransactionalEntityProvider<Resource> {
	
	    public ResourceEntityProvider() {
	        super(Resource.class);
	      //按权限进行过虑
			setQueryModifierDelegate(
					new DefaultQueryModifierDelegate () {
						@SuppressWarnings("unchecked")
						@Override
						public void filtersWillBeAdded(
								CriteriaBuilder criteriaBuilder,
								CriteriaQuery<?> query,
								List<Predicate> predicates) {
							//管理员不加过虑条件
							if(SecurityUtils.getSubject().hasRole(Role.SUPER_ADMIN_ROLE_NAME)){
								return;
							}
							//非管理员按角色过虑菜单
							query.distinct(true);//过虑重复记录
							Root<Resource> root = (Root<Resource>) query.getRoots().iterator().next();//此处不能直接创建必须从query中取
							SetJoin<Resource,Role> rjoin = root.join(Resource_.roles);
							SetJoin<Role,User> ujoin = rjoin.join(Role_.users);
							Predicate p = criteriaBuilder.equal(ujoin.get(User_.username), SecurityUtils.getSubject().getPrincipal());
							predicates.add(p);
						}
					}
			);
	    }
	    
}