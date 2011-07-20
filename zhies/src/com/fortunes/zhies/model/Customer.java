package com.fortunes.zhies.model;

import javax.persistence.Entity;

@Entity
public class Customer extends Company{
	
	
	
	
	
    public Customer() {
    }
    
    public Customer(long id) {
    	this.setId(id);
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    /*=============== setter and getter =================*/
    

}
