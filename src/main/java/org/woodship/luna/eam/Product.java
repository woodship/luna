package org.woodship.luna.eam;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.woodship.luna.base.Organization;
import org.woodship.luna.base.Person;
import org.woodship.luna.core.security.User;
import org.woodship.luna.db.IdEntity;
import org.woodship.luna.eam.enums.Classes;
import org.woodship.luna.eam.enums.Inunction;
import org.woodship.luna.eam.enums.LayDirection;
import org.woodship.luna.eam.enums.Pack;
import org.woodship.luna.eam.enums.Weld;
import org.woodship.luna.eam.enums.Winding;

import com.vaadin.data.fieldgroup.Caption;

@Entity
public class Product extends IdEntity<Product>{
	private static final long serialVersionUID = 1L;
	
	@Caption("生产日期")
	private Date produceDate;
	
	@Caption("班次")
	private Classes classes;
	
	@Caption("工号")
	@OneToOne
	private Person person;
	
	@Caption("车台号")
	private String carNum;
	
	@Caption("产品型号")
	@ManyToOne
	private InvItem produceModel;
	
	@ManyToOne
	@Caption("客户名称")
	private Customer customerNum;
	
	@Caption("重量")
	private String weight;
	
	@Caption("捻向")
	private LayDirection layDirection;
	
	@Caption("捻距")
    private String twistLength;
	
    @Caption("长度")
    private String length;
    
    @Caption("排线")
    private Winding winding;
    
    @Caption("涂油")
    private Inunction inunction;
    
    @Caption("包装")
    private Pack pack;
    
    @Caption("焊接")
    private Weld weld;
    
    @Caption("原料型号")
    private String materialModel;
    
    @NotNull
    @ManyToOne
    private Organization org;

    private java.util.Date createDate = new java.util.Date();
    
    @ManyToOne
    private User createBy;
    
	public Date getProduceDate() {
		return produceDate;
	}

	public void setProduceDate(Date produceDate) {
		this.produceDate = produceDate;
	}

	public Classes getClasses() {
		return classes;
	}

	public void setClasses(Classes classes) {
		this.classes = classes;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person workNum) {
		this.person = workNum;
		this.org = workNum.getOrg();
	}

	public String getCarNum() {
		return carNum;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	public InvItem getProduceModel() {
		return produceModel;
	}

	public void setProduceModel(InvItem produceModel) {
		this.produceModel = produceModel;
	}

	public Customer getCustomerNum() {
		return customerNum;
	}

	public void setCustomerNum(Customer customerNum) {
		this.customerNum = customerNum;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public LayDirection getLayDirection() {
		return layDirection;
	}

	public void setLayDirection(LayDirection layDirection) {
		this.layDirection = layDirection;
	}

	public String getTwistLength() {
		return twistLength;
	}

	public void setTwistLength(String twistLength) {
		this.twistLength = twistLength;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public Winding getWinding() {
		return winding;
	}

	public void setWinding(Winding winding) {
		this.winding = winding;
	}

	public Inunction getInunction() {
		return inunction;
	}

	public void setInunction(Inunction inunction) {
		this.inunction = inunction;
	}

	public Pack getPack() {
		return pack;
	}

	public void setPack(Pack pack) {
		this.pack = pack;
	}

	public String getMaterialModel() {
		return materialModel;
	}

	public void setMaterialModel(String materialModel) {
		this.materialModel = materialModel;
	}

	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization org) {
		this.org = org;
	}

	public java.util.Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(java.util.Date createDate) {
		this.createDate = createDate;
	}

	public User getCreateBy() {
		return createBy;
	}

	public void setCreateBy(User createBy) {
		this.createBy = createBy;
	}

	public Weld getWeld() {
		return weld;
	}

	public void setWeld(Weld weld) {
		this.weld = weld;
	}
    
	
    
}
