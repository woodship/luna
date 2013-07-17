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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;
import org.woodship.luna.db.HierarchialEntity;

import com.vaadin.data.fieldgroup.Caption;

@SuppressWarnings("serial")
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Organization  extends HierarchialEntity<Organization>{
	public Organization() {
		super();
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
