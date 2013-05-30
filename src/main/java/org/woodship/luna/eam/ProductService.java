package org.woodship.luna.eam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.core.person.Person;
import org.woodship.luna.core.person.PersonService;
import org.woodship.luna.core.security.User;
import org.woodship.luna.core.security.UserService;
import org.woodship.luna.eam.enums.Classes;
import org.woodship.luna.eam.enums.Inunction;
import org.woodship.luna.eam.enums.LayDirection;
import org.woodship.luna.eam.enums.Pack;
import org.woodship.luna.eam.enums.Weld;
import org.woodship.luna.eam.enums.Winding;

@Service
public class ProductService {
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
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Transactional
	public String createProduct(Map<String,String[]> map){
		Product p = new Product();
		//日期
		String date = map.get("produceDate")[0];
		try {
			p.setProduceDate(sdf.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
			return "日期格式错误";
		}
		
		//班次
		p.setClasses(getEnumValue("classes",map,Classes.class));
		
		//工号
		String workNum = map.get("workNum")[0];
		List<Person> persons = ps.findByWorkNum(workNum);
		if(persons.size() == 0){
			return "该工号不存在，请确认后再试";
		}
		p.setPerson(persons.get(0));
		
		//("车台号")
		p.setCarNum(map.get("carNum")[0]);
		
		//@Caption("产品型号")
		List<InvItem> pms = is.findByModel(map.get("produceModel")[0]);
		if(pms.size() == 0){
			return "该产品型号不存在，请确认后再试";
		}
		p.setProduceModel(pms.get(0));
		
//		@Caption("客户名称")
		List<Customer> cuses = cs.findByCusNum(map.get("customerNum")[0]);
		if(cuses.size() == 0){
			return "该产客户编号不存在，请确认后再试";
		}
		p.setCustomerNum(cuses.get(0));
		
//		@Caption("重量")
		p.setWeight(map.get("weight")[0]);
		
//		@Caption("捻向")
		p.setLayDirection(getEnumValue("layDirection",map,LayDirection.class));
		
//		@Caption("捻距")
		p.setTwistLength(map.get("twistLength")[0]);
		
//	    @Caption("长度")
		p.setLength(map.get("lenght")[0]);
	    
//	    @Caption("排线")
	    p.setWinding(getEnumValue("winding",map,Winding.class));
	    
//	    @Caption("涂油")
	    p.setInunction(getEnumValue("inunction",map,Inunction.class));
	    
//	    @Caption("包装")
	    p.setPack(getEnumValue("pack",map,Pack.class));
	    
//	    @Caption("原料型号")
	    p.setMaterialModel(map.get("materialModel")[0]);
	    
//	    @Caption("焊接")
	    p.setWeld(getEnumValue("weld",map,Weld.class));
	    
	    
	    //创建人
	    User createBy = us.findByUsername(map.get("createBy")[0]);
	    p.setCreateBy(createBy);
	    
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
	
	
}
