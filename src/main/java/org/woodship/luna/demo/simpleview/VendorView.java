package org.woodship.luna.demo.simpleview;

import org.springframework.context.annotation.Scope;
import org.woodship.luna.util.SimpleEntityView;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
public class VendorView extends SimpleEntityView<Vendor> {

	public VendorView() {
		super(Vendor.class);
	}

}
