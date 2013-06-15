package org.woodship.luna.eam;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.ConstraintViolation;
import javax.validation.Path;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.LunaException;
import org.woodship.luna.core.person.Organization;
import org.woodship.luna.core.person.Organization_;
import org.woodship.luna.core.person.Person;
import org.woodship.luna.core.person.PersonService;
import org.woodship.luna.core.security.User;
import org.woodship.luna.core.security.UserService;
import org.woodship.luna.eam.enums.Inunction;
import org.woodship.luna.eam.enums.LayDirection;
import org.woodship.luna.eam.enums.Pack;
import org.woodship.luna.eam.enums.Weld;
import org.woodship.luna.eam.enums.Winding;

import com.vaadin.data.fieldgroup.Caption;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

@Service
public class ProductService implements Serializable{
	private static final long serialVersionUID = -2307602987532561520L;

	@PersistenceContext
	private  EntityManager em;

	@Autowired
	private  PersonService ps;

	@Autowired
	private InvItemService is;

	@Autowired
	private CustomerService cs;

	@Autowired
	private UserService us;


	public ProductService() {
		ConvertUtils.register(new DateConvert(), java.util.Date.class); 
	}

	@SuppressWarnings("rawtypes")
	@Transactional
	public String createProduct(Map<String,String[]> map){
		Product p = new Product();
		//创建人
		User createBy = us.findByUsername(map.get("createBy")[0]);
		p.setCreateBy(createBy);
		Person cbPerson = createBy.getPerson();
		//车间
		p.setOrg(cbPerson.getTopDepartment());
		SingularAttribute[] sas = ProductDeptFileds.getFieldsByDeptName(cbPerson.getTopDepartment().getName());
		for(SingularAttribute sa : sas){
			if(sa.equals(Product_.org)){
				continue;
			}
			String clientv = map.get(sa.getName())[0];
			if("".equals(clientv)){
				return "请填写完整，所有字段都是必填项";
			}
			Object value = clientv;
			try {
				if(sa.equals(Product_.classes)){
					value = getClasses(cbPerson.getTopDepartment(), clientv);;
				}else if(sa.getJavaType() == Person.class)
				{
					value = ps.findByWorkNum(clientv);
				}else if(sa.equals(Product_.produceModel)){
					value = is.findByModel(clientv);
				}else if(sa.equals(Product_.customerNum)){
					value =cs.findByCusNum(clientv);
				}else if(sa.equals(Product_.layDirection)){
					value = LayDirection.valueOf(clientv);
				}else if(sa.equals(Product_.winding)){
					value = Winding.valueOf(clientv);
				}else if(sa.equals(Product_.inunction)){
					value = Inunction.valueOf(clientv);
				}else if(sa.equals(Product_.pack)){
					value = Pack.valueOf(clientv);
				}else if(sa.equals(Product_.weld)){
					value = Weld.valueOf(clientv);
				}
				
				BeanUtils.setProperty(p, sa.getName(),value );
				
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}catch(javax.persistence.NoResultException e){
					String c =getCaption(sa.getName());
					return "找不到对应数据，请确认你输入的'"+c+"'是否正确";
			}
		}
		em.persist(p);
		
		return "保存成功";
	}

	public   <T extends Enum<T>>  T getEnumValue(String key, Map<String,String[]> map ,Class<T>  e){
		String value = map.get(key)[0];
		if(value != null && !"".equals(value)){
			try {
				return Enum.valueOf(e,value);
			} catch (Exception e1) {
				return null;
			}
		}
		return null;
	}

	public class DateConvert implements Converter{  
		public Object convert(Class arg0, Object arg1) {  
			String p = (String)arg1;  
			if(p== null || p.trim().length()==0){  
				return null;  
			}  
			try{  
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
				return df.parse(p.trim());  
			}  
			catch(Exception e){  
				return null;  
			}  
		}  

	}  

	private Organization getClasses(Organization dept, String calssName){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Organization> query = cb.createQuery(Organization.class);
		Root<Organization> from = query.from(Organization.class);
		Predicate pa = cb.equal(from.get(Organization_.parent), dept);
		Predicate pb = cb.equal(from.get(Organization_.name), calssName);
		query.where(cb.and(pa,pb));

		return em.createQuery(query).getSingleResult();
	}
	
	public static String getCaption(String fieldname){
		try {
			Field f = Product.class.getDeclaredField(fieldname);
			Caption cap = f.getAnnotation(Caption.class);
			String c = f.getName();
			if(cap != null ){
				c = cap.value();
			}
			return c;
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		} 
	}

}
