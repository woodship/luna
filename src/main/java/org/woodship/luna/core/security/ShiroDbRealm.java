package org.woodship.luna.core.security;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * 自实现用户与权限查询.
 */
public class ShiroDbRealm extends AuthorizingRealm {
	@PersistenceContext
	private  EntityManager em;
	
	@Autowired
	private  UserService userser;

	@Autowired
	private  RoleService roleser;
	
	@Autowired
	private  ResourceService resser;

	public ShiroDbRealm() {
		super();
		
		//设置加密算法
		setCredentialsMatcher(new HashedCredentialsMatcher() {
			@Override
			public boolean doCredentialsMatch(AuthenticationToken authenticationToken,
					AuthenticationInfo info) {
				DefaultPasswordService ps = new DefaultPasswordService();
				String password = authenticationToken.getCredentials() instanceof char[] ? 
						String.valueOf((char[]) authenticationToken.getCredentials()) : 
							String.valueOf(authenticationToken.getCredentials());
						return ps.passwordsMatch(password, String.valueOf(info.getCredentials()));
			}
		});

	}

	/**
	 * 认证回调函数, 登录时调用.
	 */
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		User user =  userser.findByUsername(token.getUsername());
		if (user != null) {
			return new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(),getName());
		} else {
			return null;
		}
	}

	/**
	 * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
	 */
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String username =  (String) principals.fromRealm(getName()).iterator().next();
		User user = userser.findByUsername(username);
		List<Role> userRoles = roleser.findRoleByUsername(username);
		if (user != null) {
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			boolean isAdmin = false;
			for(Role role : userRoles){
				if(Role.SUPER_ADMIN_ROLE_NAME.equals(role.getName())){
					isAdmin = true;
					break;
				}
			}
			if(isAdmin){
				//管理员设置所有权限				
				List<Role> roles = roleser.findAll();
				List<Resource> ress = resser.findAll();
				for (Role group : roles) {
					//基于role的权限信息
					info.addRole(group.getName());
				}
				for(Resource res : ress){
					//基于Permission的权限信息
					info.addStringPermission(res.getResKey());
				}
			}else{
			//非管理员设置角色权限
				for (Role group : userRoles) {
					//基于Permission的权限信息
					info.addStringPermissions(group.toPermissionNames());
					//基于role的权限信息
					info.addRole(group.getName());
				}
			}
			
			return info;
		} else {
			return null;
		}
	}

	/**
	 * 更新用户授权信息缓存.
	 */
	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

	/**
	 * 清除所有用户授权信息缓存.
	 */
	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}



	/**
	 * 自定义Authentication对象，使得Subject除了携带用户的登录名外还可以携带更多信息.
	 */
	public static class ShiroUser implements Serializable {

		private static final long serialVersionUID = -1748602382963711884L;
		private String loginName;
		private String name;
		private String id;


		public ShiroUser(String loginName, String name, String id) {
			this.loginName = loginName;
			this.name = name;
			this.id = id;
		}


		public String getLoginName() {
			return loginName;
		}


		public void setLoginName(String loginName) {
			this.loginName = loginName;
		}


		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}


		public String getId() {
			return id;
		}


		public void setId(String id) {
			this.id = id;
		}




	}
}
