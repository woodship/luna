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

import java.util.Set;

import javax.persistence.Entity;
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

	@Caption("机构名称")
	@NotEmpty
    private String name;
	
	@Caption("机构类型")
	@NotNull
	private OrgType orgType;

    @OneToMany(mappedBy = "org")
    private Set<Person> persons;

    private boolean leaf = true;

    @Caption("上级机构")
    @ManyToOne
    private Organization parent;

    
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

    public Organization getParent() {
        return parent;
    }

    public void setParent(Organization parent) {
    	parent.setLeaf(false);
        this.parent = parent;
    }


    @Transient
    public String getHierarchicalName() {
        if (parent != null && parent.orgType != OrgType.单位) {
            return parent.toString() + " /" + name;
        }
        return name;
    }

    @Override
    public String toString() {
        return getHierarchicalName();
    }

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public OrgType getOrgType() {
		return orgType;
	}

	public void setOrgType(OrgType orgType) {
		this.orgType = orgType;
	}


}
