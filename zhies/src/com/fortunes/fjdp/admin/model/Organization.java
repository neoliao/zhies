package com.fortunes.fjdp.admin.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import net.fortunes.core.Model;

@Entity
public class Organization extends Model{
	
	@Id 
	@GeneratedValue
	private long id;
	private String name;
	private String code;
	private String tel;
	private String address;
	private String shortName;
	private String fullName;
	private boolean leaf = true;
	
	@ManyToOne
	private Dict type;

	@ManyToOne
	private Organization parent;
	
	@OneToMany(mappedBy = "parent")
	private List<Organization> children = new ArrayList<Organization>();
	
	@OneToMany(mappedBy = "organization")
	private List<Employee> employees = new ArrayList<Employee>();
	
    public Organization() {
    }
    
    public Organization(long id) {
    	this.id = id;
    }
    
    public Organization(Organization parent,String name,String shortName) {
    	this.parent = parent;
    	this.name = name;
    	this.shortName = shortName;
    }
    
    @Override
    public String toString() {
    	return "组织机构:"+shortName;
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Dict getType() {
		return type;
	}

	public void setType(Dict type) {
		this.type = type;
	}

	public Organization getParent() {
		return parent;
	}

	public void setParent(Organization parent) {
		this.parent = parent;
	}

	public List<Organization> getChildren() {
		return children;
	}

	public void setChildren(List<Organization> children) {
		this.children = children;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public boolean isLeaf() {
		return leaf;
	}


}


