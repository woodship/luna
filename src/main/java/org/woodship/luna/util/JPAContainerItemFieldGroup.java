package org.woodship.luna.util;

import java.lang.reflect.Method;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

/**
 * @author 老崔
 * 参考 {@link BeanFieldGroup}，仅支持japcontainerItem
 * 增加必填标识生成功能，字符串类字段值设置为""
 */
public class JPAContainerItemFieldGroup<T> extends FieldGroup {
	private static final long serialVersionUID = 1L;

    private Class<T> beanType;

    private static Boolean beanValidationImplementationAvailable = null;
    private List<java.lang.reflect.Field> fields = null;
    public JPAContainerItemFieldGroup(Class<T> beanType) {
    	this.setFieldFactory(new EntityFieldGroupFieldFactory());
        this.beanType = beanType;
        this.fields = super.getFieldsInDeclareOrder(beanType);
    }

    @Override
    protected Class<?> getPropertyType(Object propertyId) {
        if (getItemDataSource() != null) {
            return super.getPropertyType(propertyId);
        } else {
            // Data source not set so we need to figure out the type manually
            /*
             * toString should never really be needed as propertyId should be of
             * form "fieldName" or "fieldName.subField[.subField2]" but the
             * method declaration comes from parent.
             */
            java.lang.reflect.Field f;
            try {
                f = getField(beanType, propertyId.toString());
                return f.getType();
            } catch (SecurityException e) {
                throw new BindException("Cannot determine type of propertyId '"
                        + propertyId + "'.", e);
            } catch (NoSuchFieldException e) {
                throw new BindException("Cannot determine type of propertyId '"
                        + propertyId + "'. The propertyId was not found in "
                        + beanType.getName(), e);
            }
        }
    }

    private static java.lang.reflect.Field getField(Class<?> cls,
            String propertyId) throws SecurityException, NoSuchFieldException {
        if (propertyId.contains(".")) {
            String[] parts = propertyId.split("\\.", 2);
            // Get the type of the field in the "cls" class
            java.lang.reflect.Field field1 = getField(cls, parts[0]);
            // Find the rest from the sub type
            return getField(field1.getType(), parts[1]);
        } else {
            try {
                // Try to find the field directly in the given class
                java.lang.reflect.Field field1 = cls
                        .getDeclaredField(propertyId);
                return field1;
            } catch (NoSuchFieldException e) {
                // Try super classes until we reach Object
                Class<?> superClass = cls.getSuperclass();
                if (superClass != Object.class) {
                    return getField(superClass, propertyId);
                } else {
                    throw e;
                }
            }
        }
    }

    @Override
    public void setItemDataSource(Item item) {
        if (!(item instanceof JPAContainerItem)) {
            throw new RuntimeException(getClass().getSimpleName()
                    + " only supports JPAContainerItem as item data source");
        }
        super.setItemDataSource(item);
    }

    @SuppressWarnings("unchecked")
	@Override
    public JPAContainerItem<T> getItemDataSource() {
        return (JPAContainerItem<T>) super.getItemDataSource();
    }

    private void ensureNestedPropertyAdded(Object propertyId) {
        if (getItemDataSource() != null) {
            // The data source is set so the property must be found in the item.
            // If it is not we try to add it.
            try {
                getItemProperty(propertyId);
            } catch (BindException e) {
                // Not found, try to add a nested property;
                // BeanItem property ids are always strings so this is safe
               e.printStackTrace();
            }
        }
    }

    @Override
    public void bind(Field field, Object propertyId) {
        ensureNestedPropertyAdded(propertyId);
        super.bind(field, propertyId);
    }

    @Override
    public Field<?> buildAndBind(String caption, Object propertyId)
            throws BindException {
        ensureNestedPropertyAdded(propertyId);
        return super.buildAndBind(caption, propertyId);
    }

    @Override
    protected void configureField(Field<?> field) {
        super.configureField(field);
        String pid = getPropertyId(field).toString();
    	
		for(java.lang.reflect.Field f : fields){
			if(f.getName().equals(pid)){
		        //设置字段必填标识*
		        NotNull notNullAnnotation = f.getAnnotation(NotNull.class);
		        Size sizeAnnotation = f.getAnnotation(Size.class);
		        NotEmpty ne = f.getAnnotation(NotEmpty.class);
		        if (notNullAnnotation != null 
		        		|| (sizeAnnotation != null && sizeAnnotation.min()>0)
		        		|| ne != null
		        		) {
		           field.setCaption(field.getCaption()+"*");
		        }
				break;
			}
		}
        // Add Bean validators if there are annotations
        if (isBeanValidationImplementationAvailable()) {
        	field.removeAllValidators();
            BeanValidator validator = new BeanValidator(beanType,pid);
            field.addValidator(validator);
//            field.setRequired(required)
            if (field.getLocale() != null) {
                validator.setLocale(field.getLocale());
            }
        }
        
        //文本类字段null 设置为""
		if (field instanceof TextField) {
            ((TextField) field).setNullRepresentation("");
        }else if ( field instanceof TextArea) {
			((TextArea) field).setNullRepresentation("");
		}
	
        
     
    }

    /**
     * Checks whether a bean validation implementation (e.g. Hibernate Validator
     * or Apache Bean Validation) is available.
     * 
     * TODO move this method to some more generic location
     * 
     * @return true if a JSR-303 bean validation implementation is available
     */
    protected static boolean isBeanValidationImplementationAvailable() {
        if (beanValidationImplementationAvailable != null) {
            return beanValidationImplementationAvailable;
        }
        try {
            Class<?> validationClass = Class
                    .forName("javax.validation.Validation");
            Method buildFactoryMethod = validationClass
                    .getMethod("buildDefaultValidatorFactory");
            Object factory = buildFactoryMethod.invoke(null);
            beanValidationImplementationAvailable = (factory != null);
        } catch (Exception e) {
            // no bean validation implementation available
            beanValidationImplementationAvailable = false;
        }
        return beanValidationImplementationAvailable;
    }
}
