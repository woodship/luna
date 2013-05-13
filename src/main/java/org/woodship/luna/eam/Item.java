package org.woodship.luna.eam;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.woodship.luna.db.IdEntity;
import com.vaadin.data.fieldgroup.Caption;

@Entity
@Table(name = "Item_")
public class Item extends IdEntity<Item>{
	private static final long serialVersionUID = 1L;

	@Caption("名称")
	private String name;
	
	@Caption("型号")
	private String model;
	
	@Caption("颜色")
	private String color;
	
	@Caption("重量")
	private String weight;
	
	@Caption("大小")
	private String size;
	
	@Caption("备注")
	private String remark;
	
}
