package org.woodship.luna.demo;

import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.woodship.luna.LunaException;
import org.woodship.luna.db.IdEntity;

import com.vaadin.data.fieldgroup.Caption;

@SuppressWarnings("serial")
@Entity
public class ElementScope extends IdEntity<ElementScope>{
	@Caption("元素")
	@NotNull
	private Element element;
	
	@Min(0)
	@Max(100)
	@Caption("最小值")
	private double min;

	@Min(0)
	@Max(100)
	@Caption("最大值")
	private double max;
	
	
	private boolean containMin = true;
	private boolean containMax = true;
	
	public ElementScope() {
	}
	public ElementScope(Element element) {
		this.element = element;
		this.min = 0.0;
		this.max = 100.0;
	}
	
	public ElementScope(Element element,double min, double max) {
		this.element = element;
		this.min = min;
		this.max = max;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public boolean isContainMin() {
		return containMin;
	}
	public void setContainMin(boolean containMin) {
		this.containMin = containMin;
	}
	public boolean isContainMax() {
		return containMax;
	}
	public void setContainMax(boolean containMax) {
		this.containMax = containMax;
	}

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}
	@Override
	public String toString() {
		return  element + "：" + min + "~" + max ;
	}
	
	public void validate(){
		if(max < min){
			throw new LunaException("元素范围的最大值不能小于最小值");
		}
	}
	
	
	
}
