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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.woodship.luna.db.IdEntity;

import com.vaadin.data.fieldgroup.Caption;

@SuppressWarnings("serial")
@Entity
public class Person extends IdEntity<Person>{

    @Size(min = 1, max = 24)
    @Caption("姓名")
    private String trueName;
    private String street;
    private String city;
    @Caption("工号")
    private String worknum;
    @Caption("电话")
    private String phoneNumber;
    @NotNull
    @ManyToOne
    @Caption("部门")
    private Department department;
    
    public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWorknum() {
		return worknum;
	}

	public void setWorknum(String worknum) {
		this.worknum = worknum;
	}

	public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department group) {
        department = group;
    }
}
