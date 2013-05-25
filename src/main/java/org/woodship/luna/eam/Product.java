package org.woodship.luna.eam;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.woodship.luna.base.Organization;
import org.woodship.luna.db.IdEntity;
import org.woodship.luna.eam.enums.Classes;
import org.woodship.luna.eam.enums.Inunction;
import org.woodship.luna.eam.enums.LayDirection;
import org.woodship.luna.eam.enums.Pack;
import org.woodship.luna.eam.enums.Winding;

import com.vaadin.data.fieldgroup.Caption;

@Entity
public class Product extends IdEntity<Product>{
	private static final long serialVersionUID = 1L;
	
	@Caption("生产日期")
	private String produceDate;
	
	@Caption("班次")
	private Classes classes;
	
	@Caption("工号")
	private String workNum;
	
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
    private String lenght;
    
    @Caption("排线")
    private Winding winding;
    
    @Caption("涂油")
    private Inunction inunction;
    
    @Caption("包装")
    private Pack pack;
    
    @Caption("原料型号")
    private String materialModel;
    
    @NotNull
    @ManyToOne
    @Caption("部门")
    private Organization org;

	public String getProduceDate() {
		return produceDate;
	}

	public void setProduceDate(String produceDate) {
		this.produceDate = produceDate;
	}

	public Classes getClasses() {
		return classes;
	}

	public void setClasses(Classes classes) {
		this.classes = classes;
	}

	public String getWorkNum() {
		return workNum;
	}

	public void setWorkNum(String workNum) {
		this.workNum = workNum;
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

	public String getLenght() {
		return lenght;
	}

	public void setLenght(String lenght) {
		this.lenght = lenght;
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
    
    
}
