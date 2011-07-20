package com.fortunes.zhies.model;

import javax.persistence.Entity;

@Entity
public class TruckCompany extends Company{
	
	
	
    public TruckCompany() {
    }
    
    public TruckCompany(long id) {
    	this.setId(id);
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    /*=============== setter and getter =================*/
    

}
