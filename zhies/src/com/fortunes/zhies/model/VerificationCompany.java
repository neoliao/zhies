package com.fortunes.zhies.model;

import javax.persistence.Entity;

@Entity
public class VerificationCompany extends Company{
	
	
	
    public VerificationCompany() {
    }
    
    public VerificationCompany(long id) {
    	this.setId(id);
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    /*=============== setter and getter =================*/
    
	

}
