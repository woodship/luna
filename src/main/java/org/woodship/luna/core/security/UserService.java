package org.woodship.luna.core.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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

	DefaultPasswordService ps = new DefaultPasswordService();

	public User findByUsername(String username) {
		if (username == null) return null;
		List<User> users =  em.createQuery("SELECT o FROM User o where o.username = ?", User.class)
				.setParameter(1, username).getResultList();
		if(users .size() > 0){
			return users.get(0);
		}
		return null;
	}

	public boolean validate(String username, String password){
		User user = findByUsername(username);
		if(user != null){
			return ps.passwordsMatch(password, user.getPassword());
		}
		return false;
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
		//一。获得可管理的顶层机构，不区分机构类型
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
		
		//2.根最大RoleDataScope据获得最大目标机构，放入topCanReadOrgs
		if(!RoleDataScope.全部数据.equals(maxScope )){
			Person person = user.getPerson();
			if(person != null){
				Organization maxOrg = person.getOrgByScope(maxScope);
				if(maxOrg != null ) {
					if(type == null){
						topCanReadOrgs.add(maxOrg);
					}
				}
			}
			if(topCanReadOrgs.size() == 0){
				return topCanReadOrgs;
			}
		}
		
		//二。应用机构类别
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Organization> query = cb.createQuery(Organization.class);
		Root<Organization> from =  query.from(Organization.class);
		//1.机构范围限制(如果有全部数据权限不做限制)
		 Predicate where = null;
		if(!RoleDataScope.全部数据.equals(maxScope )){
			if(topCanReadOrgs.size() == 0){
				return new ArrayList<Organization>();
			}
			ListJoin<Organization, Organization> join = from.join(Organization_.ancestors);
			In<String> in = cb.in(join.get(Organization_.id));
			for(Organization o : topCanReadOrgs){
				in = in.value(o.getId());
			}
			where = cb.and(in);
		}else{
			where = cb.isNotNull(from.get(Organization_.id));
		}
		
		//2.应用机构类别
		if(type != null ){
			Predicate p = cb.equal(from.get(Organization_.orgType), type);
			where = cb.and(where,p);
		}
		
		query.where(where);
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
