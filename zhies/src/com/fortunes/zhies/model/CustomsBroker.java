package com.fortunes.zhies.model;

import javax.persistence.Entity;

@Entity
public class CustomsBroker extends Company{
	
	
	
    public CustomsBroker() {
    }
    
    public CustomsBroker(long id) {
    	this.setId(id);
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    /*=============== setter and getter =================*/
    
	

}
