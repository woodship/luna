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

import org.woodship.luna.db.HierarchialEntity;

@SuppressWarnings("serial")
@Entity
public class Organization  extends HierarchialEntity<Organization>{

    private String name;

    @OneToMany(mappedBy = "org")
    private Set<Person> persons;

    @Transient
    private Boolean leaf;

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
        this.parent = parent;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Transient
    public String getHierarchicalName() {
        if (parent != null) {
            return parent.toString() + " : " + name;
        }
        return name;
    }

    @Override
    public String toString() {
        return getHierarchicalName();
    }

}
