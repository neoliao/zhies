package com.fortunes.zhies.model;

import javax.persistence.Entity;

@Entity
public class ShipCompany extends Company{
	
	
	
    public ShipCompany() {
    }
    
    public ShipCompany(long id) {
    	this.setId(id);
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    /*=============== setter and getter =================*/
    
	

}
