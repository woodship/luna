package org.woodship.luna.demo.subtable;

public enum Element {
	C(0.95),Si(0.95),Mn(0.95),P(1),S(1),
	Cr(0.98),Ni(0.99),Mo(0.99),V(0.70),W(0.85),Cu(0.90);
	
	/**
	 * 烧得率
	 */
	private double yield;
	
	private Element(double yield){
		this.yield = yield;
	}
	private Element(){
		this.yield = 1;
	}
	public double getYield() {
		return yield;
	}
	public void setYield(double yield) {
		this.yield = yield;
	}
	
	
}
