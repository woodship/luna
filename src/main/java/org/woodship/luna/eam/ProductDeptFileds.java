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
		,Product_.person2
		,Product_.person3
		,Product_.score
		,Product_.score2
		,Product_.score3
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
		,Product_.jiaoMiLength
		,Product_.jieMiLength
	};
	@SuppressWarnings("rawtypes")
	public static String[] getFiledNamesByDeptName(String deptName){
		SingularAttribute[] s = getFieldsByDeptName(deptName);
		String[] strs = new String[s.length];
		for(int i=0; i < s.length; i++){
			strs[i]  = s[i].getName();
		}
		return strs;
	}
	
	@SuppressWarnings("rawtypes")
	public static SingularAttribute[] getFieldsByDeptName(String deptName){
		SingularAttribute[] s = new SingularAttribute[]{};
		if(Product.DU_XIN_DEPT_NAME.equals(deptName)){
			s = DU_XIN;
		}else if(Product.JIAO_XIAN_DEPT_NAME.equals(deptName)){
			s = JIAO_XIAN;
		}else if(Product.LA_SI_DEPT_NAME.equals(deptName)){
			s = LA_SI;
		}
		return s;
	}
	
}
