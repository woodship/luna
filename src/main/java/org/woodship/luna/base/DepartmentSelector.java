/**
 * Copyright 2009-2013 Oy Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.woodship.luna.base;

import org.woodship.luna.db.ContainerUtils;

import ru.xpoft.vaadin.SpringApplicationContext;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;

/**
 * A custom field that allows selection of a department.
 */
public class DepartmentSelector extends CustomField<Department> {
	ContainerUtils conu;
	
    private ComboBox geographicalDepartment = new ComboBox();
    private ComboBox department = new ComboBox();

    private JPAContainer<Department> container;
    private JPAContainer<Department> geoContainer;

    public  DepartmentSelector() {
    	setImmediate(false);
    	//conu = SpringApplicationContext.getContainerUtils();
        container = conu.createJPABatchableContainer(Department.class);
        geoContainer = conu.createJPABatchableContainer(Department.class);
        geoContainer.setAutoCommit(false);
        container.setAutoCommit(false);
        setCaption("Department");
        // Only list "roots" which are in our example geographical super
        // departments
        geoContainer.addContainerFilter(new IsNull("parent"));
        geographicalDepartment.setContainerDataSource(geoContainer);
        geographicalDepartment.setItemCaptionPropertyId("name");
        geographicalDepartment.setImmediate(true);

        container.setApplyFiltersImmediately(false);
        filterDepartments(null);
        department.setContainerDataSource(container);
        department.setItemCaptionPropertyId("name");

        geographicalDepartment.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(
                    com.vaadin.data.Property.ValueChangeEvent event) {
                /*
                 * Modify filtering of the department combobox
                 */
                EntityItem<Department> item = geoContainer
                        .getItem(geographicalDepartment.getValue());
                Department entity = item.getEntity();
                filterDepartments(entity);
            }
        });
        department.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(
                    com.vaadin.data.Property.ValueChangeEvent event) {
                /*
                 * Modify the actual value of the custom field.
                 */
                if (department.getValue() == null) {
                    setValue(null, false);
                } else {
                    Department entity = container
                            .getItem(department.getValue()).getEntity();
                    setValue(entity, false);
                }
            }
        });
    }

    @Override
    protected Component initContent() {
        CssLayout cssLayout = new CssLayout();
        cssLayout.addComponent(geographicalDepartment);
        cssLayout.addComponent(department);
        return cssLayout;
    }

    /**
     * Modify available options based on the "geo department" select.
     * 
     * @param currentGeoDepartment
     */
    private void filterDepartments(Department currentGeoDepartment) {
        if (currentGeoDepartment == null) {
            department.setValue(null);
            department.setEnabled(false);
        } else {
            container.removeAllContainerFilters();
            container.addContainerFilter(new Equal("parent",
                    currentGeoDepartment));
            container.applyFilters();
            department.setValue(null);
            department.setEnabled(true);
        }
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
        super.setPropertyDataSource(newDataSource);
        setDepartment((Department) newDataSource.getValue());
    }

    @Override
    public void setValue(Department newValue) throws ReadOnlyException,
            Converter.ConversionException {
        super.setValue(newValue);
        setDepartment(newValue);
    }

    private void setDepartment(Department department) {
        geographicalDepartment.setValue(department != null ? department
                .getParent().getId() : null);
        this.department
                .setValue(department != null ? department.getId() : null);
    }

    @Override
    public Class<? extends Department> getType() {
        return Department.class;
    }

}
