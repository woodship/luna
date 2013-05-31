package org.woodship.luna.db;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.woodship.luna.HomeView;
import org.woodship.luna.core.person.OrgType;
import org.woodship.luna.core.person.Organization;
import org.woodship.luna.core.person.OrganizationView;
import org.woodship.luna.core.person.Person;
import org.woodship.luna.core.person.PersonView;
import org.woodship.luna.core.security.ApplicationView;
import org.woodship.luna.core.security.Resource;
import org.woodship.luna.core.security.ResourceService;
import org.woodship.luna.core.security.ResourceType;
import org.woodship.luna.core.security.Role;
import org.woodship.luna.core.security.RoleDataScore;
import org.woodship.luna.core.security.RoleView;
import org.woodship.luna.core.security.User;
import org.woodship.luna.core.security.UserView;
import org.woodship.luna.eam.Customer;
import org.woodship.luna.eam.CustomerView;
import org.woodship.luna.eam.InvItem;
import org.woodship.luna.eam.InvItemView;
import org.woodship.luna.eam.Product;
import org.woodship.luna.eam.ProductView;
import org.woodship.luna.eam.enums.Classes;
import org.woodship.luna.util.Utils;


@Component
public class InitData{

	@PersistenceContext
	private  EntityManager em;
	
	@Autowired
	private ResourceService resSer;
	
	private DefaultPasswordService ps = new DefaultPasswordService();
	
	private Person p1;
	
	private User userAdmin;
	@Transactional
	public void init(){
		//有数据则不再初始化
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		cq.select(cb.count(cq.from(User.class)));
		long size = em.createQuery(cq).getSingleResult();
		if(size>0) return;
		
		//增加人员机构用户数据
		createOrgAndPerson();
		
		//增加资源
		createResource();
		
		//增加业务数据
		createBusinessData();
	}
	

	private void createBusinessData() {

		//菜单
		Resource bus = new Resource("BUSI_MODULE", "业务管理", ResourceType.MODULE);
		em.persist(bus);
		resSer.createCUDApp("型号维护", bus,InvItemView.NAME, InvItemView.class);
		resSer.createCUDApp("客户维护", bus,CustomerView.NAME, CustomerView.class);
		Resource resPro = resSer.createCUDApp("产品管理", bus,ProductView.NAME, ProductView.class);
		resSer.createAction(ProductView.EXCEL_ACTION_KEY, "导出EXCEL", resPro);
		
		//型号
		InvItem ia = new InvItem("WG7893",1000f,null);
		em.persist(ia);
		for(int i = 1000; i<1200 ; i++){
			InvItem ib = new InvItem("XX"+i,1500f,null);
			em.persist(ib);
		}
		//客户
		Customer ca = new Customer("CKY11", "开元物业", null);
		Customer cb = new Customer("CTHYY", "天华制造", null);
		em.persist(ca);
		em.persist(cb);
		
		//产品
		Product pro = new Product();
		pro.setProduceDate(new Date());
		pro.setClasses(Classes.乙);
		pro.setCarNum("CAR111");
		pro.setCreateBy(userAdmin);
		pro.setCustomerNum(ca);
		pro.setPerson(p1);
		em.persist(pro);
	}


	final static String[] groupsNames = { "甲班","乙班", "丙班" };
	final static String[] officeNames = { "拉丝车间","镀锌车间", "绞线车间"};
	final static String[] fnames = { "赵", "钱", "孙", "李",
			"周","吴","郑","王","冯","陈","褚",
			"卫","蒋","沈"};
	final static String[] lnames = { "万全", "心社", "彭勇", "建国",
			"定之", "洁敏", "正", "长赋", "焕成", "伏瞻",
			"卫", "继伟", "振华", "益民", "名照" };
	final static String cities[] = { "北京", "上海", "深圳",
			"广州", "杭州", "南京", "沈阳", "成都", "哈尔滨",
			"大连", "西安", "郑州", "洛阳" };
	final static String streets[] = { "4215 Blandit Av.", "452-8121 Sem Ave",
			"279-4475 Tellus Road", "4062 Libero. Av.", "7081 Pede. Ave",
			"6800 Aliquet St.", "P.O. Box 298, 9401 Mauris St.",
			"161-7279 Augue Ave", "P.O. Box 496, 1390 Sagittis. Rd.",
			"448-8295 Mi Avenue", "6419 Non Av.", "659-2538 Elementum Street",
			"2205 Quis St.", "252-5213 Tincidunt St.",
			"P.O. Box 175, 4049 Adipiscing Rd.", "3217 Nam Ave",
			"P.O. Box 859, 7661 Auctor St.", "2873 Nonummy Av.",
			"7342 Mi, Avenue", "539-3914 Dignissim. Rd.",
			"539-3675 Magna Avenue", "Ap #357-5640 Pharetra Avenue",
			"416-2983 Posuere Rd.", "141-1287 Adipiscing Avenue",
			"Ap #781-3145 Gravida St.", "6897 Suscipit Rd.",
			"8336 Purus Avenue", "2603 Bibendum. Av.", "2870 Vestibulum St.",
			"Ap #722 Aenean Avenue", "446-968 Augue Ave",
			"1141 Ultricies Street", "Ap #992-5769 Nunc Street",
			"6690 Porttitor Avenue", "Ap #105-1700 Risus Street",
			"P.O. Box 532, 3225 Lacus. Avenue", "736 Metus Street",
			"414-1417 Fringilla Street", "Ap #183-928 Scelerisque Road",
			"561-9262 Iaculis Avenue" };

	public  void createOrgAndPerson() {
		
		Random r = new Random(0);
		Organization orgRoot = new Organization();
		orgRoot.setName("恒星集团");
		orgRoot.setOrgType(OrgType.单位);
		em.persist(orgRoot);
		for (String o : officeNames) {
			Organization geoGroup = new Organization();
			geoGroup.setName(o);
			geoGroup.setParent(orgRoot);
			geoGroup.setOrgType(OrgType.顶级部门);
			for (String g : groupsNames) {
				Organization group = new Organization();
				group.setName(g);
				group.setOrgType(OrgType.班组);
				em.persist(group);
				Set<Person> gPersons = new HashSet<Person>();
				
				int amount = r.nextInt(15) + 1;
				for (int i = 0; i < amount; i++) {
					Person p = new Person();
					p.setTrueName(fnames[r.nextInt(fnames.length)]+lnames[r.nextInt(lnames.length)]);
					p.setIdCard(cities[r.nextInt(cities.length)]);
					p.setPhoneNumber("+358 02 555 " + r.nextInt(10) + r.nextInt(10)
							+ r.nextInt(10) + r.nextInt(10));
					int n = r.nextInt(100000);
					if (n < 10000) {
						n += 10000;
					}
					p.setWorkNum("" + n);
					p.setStreet(streets[r.nextInt(streets.length)]);
					p.setOrg(group);
					gPersons.add(p);
					em.persist(p);
					
				}
				//用于普通用户
				if(p1 == null){
					p1 = new Person();
					p1.setTrueName("张长江");
					p1.setWorkNum("user");
					p1.setOrg(group);
					em.persist(p1);
				}
			   
					
				group.setParent(geoGroup);
				group.setPersons(gPersons);
				em.persist(group);
			}
			em.persist(geoGroup);
		}

	}
	
	

	private void createResource(){
		Resource home = new Resource(HomeView.KEY,"主页", ResourceType.APPLICATION, null,  HomeView.NAME, HomeView.class);
		em.persist(home);
		
		//增加系统管理模块
		Resource sys = new Resource("SYSTEM_MODULE", "系统管理", ResourceType.MODULE);
		em.persist(sys);
		resSer.createApp("应用管理",  sys, ApplicationView.NAME, ApplicationView.class);
		resSer.createCUDApp("用户管理",  sys, UserView.NAME, UserView.class);
		resSer.createCUDApp("角色管理",  sys, RoleView.NAME, RoleView.class);
		
		//增加基础应用模块
		Resource base = new Resource("BASE_MODULE", "基础应用", ResourceType.MODULE);
		em.persist(base);
		resSer.createCUDApp("机构管理", base,OrganizationView.NAME, OrganizationView.class);
		Resource resPerson = resSer.createCUDApp("人员管理", base,PersonView.NAME, PersonView.class);
		
		
		//增加管理员
		String pw =  ps.encryptPassword(User.DEFAULT_PASSWORD);
		userAdmin = new User(User.SUPER_ADMIN_USERNAME,pw,"管理员");
		userAdmin.setSysUser(true);
		em.persist(userAdmin);
		
		//增加一个普通用户
		User u1 = new User();
		u1.setPerson(p1);
		u1.setPassword(pw);
		em.persist(u1);
		
		Role radmin = new Role(Role.SUPER_ADMIN_ROLE_NAME);
		radmin.setSysRole(true);
		radmin.addUser(userAdmin);
		radmin.setDataScore(RoleDataScore.全部数据);
		em.persist(radmin);
		
		Resource padd = resSer.getResByKey(Utils.getAddActionId(PersonView.class));
		Role ruser = new Role("部门管理员");
		ruser.setDataScore(RoleDataScore.本部门);
		ruser.addResource(home);
		ruser.addResource(resPerson);
		ruser.addResource(base);
		ruser.addResource(padd);
		ruser.addUser(u1);
		em.persist(ruser);
	}
	


}
