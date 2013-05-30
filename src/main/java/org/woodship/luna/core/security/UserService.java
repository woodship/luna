package org.woodship.luna.core.security;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.base.Person;

@Service
public class UserService {
	
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
}
