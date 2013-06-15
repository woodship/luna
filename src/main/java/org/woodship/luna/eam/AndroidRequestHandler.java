package org.woodship.luna.eam;

import java.io.IOException;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.woodship.luna.core.person.Organization;
import org.woodship.luna.core.person.Person;
import org.woodship.luna.core.security.User;
import org.woodship.luna.core.security.UserService;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

@Service
public class AndroidRequestHandler implements RequestHandler{
	private static final long serialVersionUID = 1L;

	@Autowired
	ProductService ps ;

	@Autowired
	UserService us ;

	@Override
	public boolean handleRequest(VaadinSession session, VaadinRequest request,
			VaadinResponse response) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		if ("/android/login".equals(request.getPathInfo())) {
			try {
				String username = request.getParameter("userName");
				String password = request.getParameter("password");
				UsernamePasswordToken token = new UsernamePasswordToken(username, password);
				token.setRememberMe(true);
				Subject currentUser = SecurityUtils.getSubject();
				currentUser.login(token);
				User u = us.getCurrentUser();
				Person p = u.getPerson();
				if(p == null || p.getTopDepartment() == null){
					response.getWriter().append("登录成功，但无法确定您所在车间，请使用关联有人员信息的用户登录");
					return true;
				}
				Organization dept = p.getTopDepartment();
				String[] fns = ProductDeptFileds.getFiledNamesByDeptName(dept.getName());
				String fnames = fns[0];
				for(int i = 1; i < fns.length; i++){
					fnames += ","+fns[i];
				}
				response.getWriter().append("OK:"+dept.getName()+":"+fnames);
			} catch (UnknownAccountException uae) {
				response.getWriter().append("未知帐户错误");
			} catch (IncorrectCredentialsException ice) {
				response.getWriter().append("登陆密码错误");
			} catch (LockedAccountException lae) {
				response.getWriter().append("帐户已被锁定");
			} catch (ExcessiveAttemptsException eae) {
				response.getWriter().append("登陆次数超过最大次数");
			} catch (AuthenticationException ae) {
				response.getWriter().append("用户名或密码错误");
			} catch (Exception ex) {
				ex.printStackTrace();
				response.getWriter().append("服务器未知异常");
			}
			return true; // We wrote a response

		} else if ("/android/add".equals(request.getPathInfo())) {
			Map<String, String[]> map = request.getParameterMap();
			String msg = "";
			try {
				msg = ps.createProduct(map);
			}
			catch (Exception e) {
				for(Throwable t = e.getCause(); t != null; t = t.getCause()){
					if(t instanceof javax.validation.ConstraintViolationException){
						for(ConstraintViolation<?> v : ((javax.validation.ConstraintViolationException)t).getConstraintViolations()){
							String fn = v.getPropertyPath().toString();
							String caption = ProductService.getCaption(fn);
							msg += caption+v.getMessage()+"\n";
						}
					}
				}
				if("".equals(msg)){
					msg = "保存出错"+e.getMessage();
				}
				e.printStackTrace();
			}
			response.getWriter().write(msg);
			return true;
		}

		return false; // No response was written
	}

}
