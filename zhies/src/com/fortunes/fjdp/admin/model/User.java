package com.fortunes.fjdp.admin.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import net.fortunes.core.Model;

@Entity @Table(name = "TUser")
public class User extends Model implements org.jbpm.api.identity.User{
	
	@Id 
	@GeneratedValue
	private long id;
	
	private String name;
	
	private String displayName; 
	
	private String password;
	
	private boolean locked; 
	
	private boolean passwordChanged;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Role role; 
	
	@OneToOne
	private Employee employee;
	
	@Embedded
	private LoginSession loginSession = new LoginSession();

    public User() {
    }
    
    public User(long id) {
    	this.id = id;
    }

    public User(String name,String password) {
        this.name = name;
        this.password = password;
    }
    
    public User( String name,String password,String displayName) {
        this.name = name;
        this.password = password;
        this.displayName = displayName;
    }
    
    //================= jbpm4 Group impl ====================
    
	@Override
	public String getId() {
		return String.valueOf(this.id);
	}
    
    @Override
	public String getBusinessEmail() {
		return this.getEmployee().getEmail();
	}

	@Override
	public String getFamilyName() {
		return this.displayName;
	}

	@Override
	public String getGivenName() {
		return this.displayName;
	}
	
	//================= setter and getter ====================
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Role getRole() {
		return role;
	}
	
	public String toString() {
		return "用户:"+name;
	}

	public void setLoginSession(LoginSession loginSession) {
		this.loginSession = loginSession;
	}

	public LoginSession getLoginSession() {
		return loginSession;
	}

	public void setPasswordChanged(boolean passwordChanged) {
		this.passwordChanged = passwordChanged;
	}

	public boolean isPasswordChanged() {
		return passwordChanged;
	}

}


