package org.woodship.luna.core.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.core.person.OrgType;
import org.woodship.luna.core.person.Organization;
import org.woodship.luna.core.person.Organization_;
import org.woodship.luna.core.person.Person;

@SuppressWarnings("serial")
@Service
public class UserService implements Serializable{

	@PersistenceContext
	private  EntityManager em;


	public User findByUsername(String username) {
		if (username == null) return null;
		List<User> users =  em.createQuery("SELECT o FROM User o where o.username = ?", User.class)
				.setParameter(1, username).getResultList();
		if(users .size() > 0){
			return users.get(0);
		}
		return null;
	}


	/**
	 * 返回当前登录用户，且该用户为受控Entity
	 * @return
	 */
	public  User getCurrentUser(){
		Object username =  SecurityUtils.getSubject().getPrincipal();
		User u =findByUsername((String) username);
		u.getRoles().iterator().next();
		return u;
	}

	/**
	 * 根据人员创建用户
	 * @param person
	 * @return
	 */
	@Transactional
	public User createUserFromPerson(Person person){
		User u = new User();
		u.setPerson(person);
		em.persist(u);
		return u;
	}

	/**
	 * @param user 必须包含ID与明码的密码
	 */
	@Transactional
	public void changePassword(User user) {
		DefaultPasswordService ps = new DefaultPasswordService();
		User u = em.find(User.class, user.getId());
		u.setPassword(ps.encryptPassword(user.getPassword()));
	}

	/**
	 * 获得当前用户有权限查看的机构列表
	 * @param user
	 * @param type指定要返回的机构类别
	 * @return
	 */
	public List<Organization> getCanReadOrg(User user, OrgType type ){

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Organization> query = cb.createQuery(Organization.class);
		Predicate where = getCanReadOrgPredicate(user, type,cb,query,false);
		query.where(where);

		return em.createQuery(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	public  Predicate getCanReadOrgPredicate(User user, OrgType type,
			CriteriaBuilder cb, CriteriaQuery<Organization> query, boolean containRoot){

		//一。获得可管理的顶层机构，不区分机构类型
		List<Organization> topCanReadOrgs = getTopCanReadOrgs(user);
		Root<Organization> from ;
		if(query.getRoots().size() > 0){
			from =(Root<Organization>) query.getRoots().iterator().next();
		}else{
			from =query.from(Organization.class);
		}
		if(topCanReadOrgs.size() == 0){
			return cb.isNull(from.get(Organization_.id));
		}

		//二。应用条件
		//1.机构范围限制(如果有全部数据权限不做限制)
		Subquery<Organization> subquery = query.subquery(Organization.class);
		Root<Organization> subfrom = subquery.from(Organization.class);
		subquery.select(subfrom);
		ListJoin<Organization, Organization> join = subfrom.join(Organization_.ancestors,JoinType.INNER);
		In<String>subin = cb.in(join.get(Organization_.id));
		for(Organization o : topCanReadOrgs){
			subin = subin.value(o.getId());
		}
		
		//2.应用机构类别
		if(type != null ){
			Predicate p = cb.equal(subfrom.get(Organization_.orgType), type);
			subquery.where(cb.and(subin,p));
		}else{
			subquery.where(subin);
		}
		//3.增加祖先节点
		if(containRoot){
			In<String> in = cb.in(from.get(Organization_.id));
			boolean hasdata = false;
			for(Organization o : topCanReadOrgs){
				Organization parento = o.getParent();
				while(parento != null){
					hasdata = true;
					in = in.value(parento.getId());
					parento = parento.getParent();
				}
			}
			if(hasdata){
				return cb.or(cb.in(from).value(subquery),in);
			}
		}
		return cb.in(from).value(subquery);
	}

	/**
	 * 获得有权看到的顶层机构，不包含子机构，不区分机构类型
	 * @param user
	 * @param type
	 * @return
	 */
	public List<Organization> getTopCanReadOrgs(User user ){
		List<Organization> topCanReadOrgs = new ArrayList<Organization>(); //不区分类型

		//1.从所有Role中取得最大RoleDataScope，自定义的直接放入topCanReadOrgs
		RoleDataScope maxScope = RoleDataScope.自定义;
		for(Role role : user.getRoles()){
			if(role.getDataScore().getLevel() > maxScope.getLevel()){
				maxScope = role.getDataScore();
			}
			if(role.getDataScore().equals(RoleDataScope.自定义)){
				//TODO 增加可管理的自定义机构到topCanReadOrgs
			}
		}

		//2.根据最大RoleDataScope据获得最大目标机构，放入topCanReadOrgs
		if(!RoleDataScope.全部数据.equals(maxScope )){
			Person person = user.getPerson();
			if(person != null){
				Organization maxOrg = person.getOrgByScope(maxScope);
				if(maxOrg != null ) {
					topCanReadOrgs.add(maxOrg);
				}
			}
			if(topCanReadOrgs.size() == 0){
				return topCanReadOrgs;
			}
		}else{
			topCanReadOrgs =  getTopOrgs();
		}

		return topCanReadOrgs;
	}



	private List<Organization> getTopOrgs() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Organization> query = cb.createQuery(Organization.class);
		Root<Organization> root = query.from(Organization.class);
		query.where(cb.isNull(root.get(Organization_.parent)));
		return em.createQuery(query).getResultList();
	}


	/**
	 * 获得当前用户有权限查看的机构列表
	 * @param type 指定要返回的机构类别
	 * @return
	 */
	public List<Organization> getCurrCanReadOrg(OrgType type ){
		User user  = getCurrentUser();
		return getCanReadOrg(user, type);
	}
}
