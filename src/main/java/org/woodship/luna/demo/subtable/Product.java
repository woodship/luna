package org.woodship.luna.demo.subtable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.woodship.luna.db.IdEntity;

import com.vaadin.data.fieldgroup.Caption;

@SuppressWarnings("serial")
@Entity
@Table( uniqueConstraints = { @UniqueConstraint(columnNames = {"name"})})
public class Product  extends IdEntity<Product>{
	@Caption("名称")
	@NotNull
	private String name;
	
	@Caption("元素标准")
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@OrderBy("element")
	private List<ElementScope> ess = new ArrayList<ElementScope>();
	
	public Product() {
	}
	public Product(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void addElementScope(ElementScope es){
		this.ess.add(es);
	}
	public List<ElementScope> getEss() {
		return ess;
	}
	public void setEss(List<ElementScope> ess) {
		this.ess = ess;
	}
	public String getScope(Element e) {
		String v = "-";
		for(ElementScope es : getEss()){
			if(e.equals(es.getElement())){
				v = es.getMin()+"~"+es.getMax();
				break;
			}
		}
		return v;
	}
	
	
}
