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
package org.woodship.luna.core.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.woodship.luna.db.HierarchialEntity;

import com.vaadin.data.fieldgroup.Caption;

@SuppressWarnings("serial")
@Entity
public class Organization  extends HierarchialEntity<Organization>{
	public Organization() {
	}
	
	public Organization(Organization parent) {
		this.setParent(parent);
	}

	@Caption("机构名称")
	@NotEmpty
    private String name;
	
	@Caption("机构类型")
	@NotNull
	private OrgType orgType;

    @OneToMany(mappedBy = "org")
    private Set<Person> persons;
    
    @Caption("上级机构")
    @ManyToOne
    private Organization parent;
    
	 @ManyToMany(fetch=FetchType.EAGER)
	private List<Organization> ancestors = new ArrayList<Organization>();
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Person> getPersons() {
        return persons;
    }

    public void setPersons(Set<Person> persons) {
        this.persons = persons;
    }

    @Transient
    public String getHierarchicalName() {
        if (getParent() != null && getParent().orgType != OrgType.单位) {
            return getParent().toString() + " /" + name;
        }
        return name;
    }

    @Override
    public String toString() {
        return getHierarchicalName();
    }


	public OrgType getOrgType() {
		return orgType;
	}

	public void setOrgType(OrgType orgType) {
		this.orgType = orgType;
	}

	@Override
	public Organization getParent() {
		return parent;
	}

	@Override
	public void setParent(Organization parent) {
		this.parent = parent;
		super.setParent(parent);
	}

	@Override
	public List<Organization> getAncestors() {
		return ancestors;
	}

	@Override
	public void setAncestors(List<Organization> ancestors) {
		this.ancestors = ancestors;
	}

	

}
