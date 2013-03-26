/**
 * DISCLAIMER
 * 
 * The quality of the code is such that you should not copy any of it as best
 * practice how to build Vaadin applications.
 * 
 * @author jouni@vaadin.com
 * 
 */

package org.woodship.luna.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

public class MyConverterFactory extends DefaultConverterFactory {
    @Override
    protected <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> findConverter(
            Class<PRESENTATION> presentationType, Class<MODEL> modelType) {

        if (presentationType == String.class && modelType == Calendar.class) {
            return (Converter<PRESENTATION, MODEL>) new Converter<String, Calendar>() {

                @Override
                public Calendar convertToModel(String value, Locale locale)
                        throws com.vaadin.data.util.converter.Converter.ConversionException {
                    return new GregorianCalendar();
                }

                @Override
                public String convertToPresentation(Calendar value,
                        Locale locale)
                        throws com.vaadin.data.util.converter.Converter.ConversionException {
                    SimpleDateFormat df = new SimpleDateFormat();
                    df.applyPattern("mm/dd/yyyy hh:mm aa");
                    return df.format(value.getTime());
                }

                @Override
                public Class<Calendar> getModelType() {
                    return Calendar.class;
                }

                @Override
                public Class<String> getPresentationType() {
                    return String.class;
                }

            };
        }

        return super.findConverter(presentationType, modelType);
    }
}
