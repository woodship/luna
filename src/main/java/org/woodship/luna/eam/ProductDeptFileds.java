package org.woodship.luna.eam;

import javax.persistence.metamodel.SingularAttribute;

/**
 *各车间要显示的字段
 */
public class ProductDeptFileds {
	@SuppressWarnings("rawtypes")
	public static final SingularAttribute[] LA_SI = {
		Product_.org
		,Product_.produceDate
		,Product_.classes
		,Product_.person
		,Product_.carNum
		,Product_.produceModel
		,Product_.customerNum
		,Product_.weight
		,Product_.length
		,Product_.productNum
		,Product_.materialModel
	};
	@SuppressWarnings("rawtypes")
	public static final SingularAttribute[] DU_XIN = {
		Product_.org
		,Product_.produceDate
		,Product_.classes
		,Product_.person
		,Product_.carNum
		,Product_.produceModel
		,Product_.customerNum
		,Product_.weight
		,Product_.length
		,Product_.productNum
		,Product_.inunction
	};
	@SuppressWarnings("rawtypes")
	public static final SingularAttribute[] JIAO_XIAN = {
		Product_.org
		,Product_.produceDate
		,Product_.classes
		,Product_.person
		,Product_.carNum
		,Product_.produceModel
		,Product_.customerNum
		,Product_.weight
		
		,Product_.layDirection
		,Product_.twistLength
		
		,Product_.length
		,Product_.productNum
		
		,Product_.winding
		
		,Product_.inunction
		,Product_.pack
		,Product_.weld
	};
	
	public static String[] getLaSiFileds(){
		String[] strs = new String[LA_SI.length];
		for(int i=0; i < LA_SI.length; i++){
			strs[i]  = LA_SI[i].getName();
		}
		return strs;
	}
	public static String[] getDU_XINFileds(){
		String[] strs = new String[DU_XIN.length];
		for(int i=0; i < DU_XIN.length; i++){
			strs[i]  = DU_XIN[i].getName();
		}
		return strs;
	}
	public static String[] getJIAO_XIANFileds(){
		String[] strs = new String[JIAO_XIAN.length];
		for(int i=0; i < JIAO_XIAN.length; i++){
			strs[i]  = JIAO_XIAN[i].getName();
		}
		return strs;
	}
}
