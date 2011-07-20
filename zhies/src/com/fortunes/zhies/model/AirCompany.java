package com.fortunes.zhies.model;

import javax.persistence.Entity;

@Entity
public class AirCompany extends Company{
	
	
	
	
    public AirCompany() {
    }
    
    public AirCompany(long id) {
    	setId(id);
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    /*=============== setter and getter =================*/

}
