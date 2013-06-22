package org.woodship.luna.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import com.vaadin.data.validator.BeanValidator;

public class LunaBeanValidator extends BeanValidator {
	private static final long serialVersionUID = 6751914717037053677L;
	
	private Class<?> beanClass;
	private String propertyName;
	private String caption ;

	public LunaBeanValidator(Class<?> beanClass, String propertyName) {
		super(beanClass, propertyName);
		this.beanClass = beanClass;
		this.propertyName = propertyName;
		this.caption = Utils.getCaption(beanClass, propertyName);
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		  Set<?> violations = getJavaxBeanValidator().validateValue(beanClass,
	                propertyName, value);
	        if (violations.size() > 0) {
	            List<String> exceptions = new ArrayList<String>();
	            for (Object v : violations) {
	                final ConstraintViolation<?> violation = (ConstraintViolation<?>) v;
	                String msg = getJavaxBeanValidatorFactory()
	                        .getMessageInterpolator().interpolate(
	                                violation.getMessageTemplate(),
	                                new SimpleContext(value, violation
	                                        .getConstraintDescriptor()), super.getLocale());
	                exceptions.add(msg);
	            }
	            StringBuilder b = new StringBuilder();
	            for (int i = 0; i < exceptions.size(); i++) {
	                if (i != 0) {
	                    b.append("<br/>");
	                }
	                b.append(caption).append(" : ").append(exceptions.get(i));
	            }
	            throw new InvalidValueException(b.toString());
	        }
	}

	
}
