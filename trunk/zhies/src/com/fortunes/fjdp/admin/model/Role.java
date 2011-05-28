package com.fortunes.fjdp.admin.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import net.fortunes.core.Model;

import org.jbpm.api.identity.Group;


@Entity
public class Role extends Model implements Group{
	
	public static final String SYSTEM_ROLE = "system";

	@Id 
	@GeneratedValue
	private long dbId;
	
	@Column(nullable=false, unique=true)
	private String name;
	
	private String roleType;
	
	@OneToMany(mappedBy = "role")
	private List<User> users = new ArrayList<User>();
	
	@ManyToMany
	private List<Privilege> privileges = new ArrayList<Privilege>();
	
	private String description;

    public Role() {
    }
    
    public Role(long dbId) {
    	this.dbId = dbId;
    }
    
    public Role(String name,String roleType) {
    	this.name = name;
    	this.roleType = roleType;
    }  
    
    @Override
    public String toString() {
    	return "角色:"+name;
    }
    
    //================= jbpm4 Group impl ====================
    @Override
	public String getId() {
		return this.name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getType() {
		return this.getRoleType();
	}
	
	//================= setter and getter ====================	
	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getRoleType() {
		return roleType;
	}

	public long getDbId() {
		return dbId;
	}

	public void setDbId(long dbId) {
		this.dbId = dbId;
	}		
}
