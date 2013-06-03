
package org.woodship.luna;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

public class LunaConverterFactory extends DefaultConverterFactory {
	private static final long serialVersionUID = -341211982525158128L;

	@SuppressWarnings("unchecked")
	@Override
    protected <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> findConverter(
            Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
        if (presentationType == String.class && modelType == Date.class) {
        	return (Converter<PRESENTATION, MODEL>) new DateConverter<Date>(Date.class,"yyyy-MM-dd");
        }else if (presentationType == String.class && modelType == java.sql.Date.class) {
        	return (Converter<PRESENTATION, MODEL>) new DateConverter<java.sql.Date>(java.sql.Date.class,"yyyy-MM-dd");
        }else if (presentationType == String.class && modelType == java.sql.Time.class) {
        	return (Converter<PRESENTATION, MODEL>) new DateConverter<java.sql.Time>(java.sql.Time.class,"yyyy-MM-dd hh:mm");
        }

        return super.findConverter(presentationType, modelType);
    }
}

class DateConverter<T extends Date> implements Converter<String, T>{
	private static final long serialVersionUID = -8155851984989945135L;
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	private Class<T> clazz;
	private String pattern;
	public DateConverter(Class<T> clazz,String pattern) {
		this.clazz = clazz;
		if(pattern ==null ){
			this.pattern = "yyyy-MM-dd" ;
		}else{
			this.pattern = pattern;
		}
		sdf.applyPattern(this.pattern);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T convertToModel(String value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		try {
			return (T) sdf.parse(value);
		} catch (ParseException e) {
			throw new ConversionException("字符串\""+sdf+"\"转换成\""+pattern+"\"格式失败");
		}
	}

	@Override
	public String convertToPresentation(Date value, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return sdf.format(value);
	}

	@Override
	public Class<T> getModelType() {
		return clazz;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
	
}
