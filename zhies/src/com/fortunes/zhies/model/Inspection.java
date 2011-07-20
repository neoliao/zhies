package com.fortunes.zhies.model;

import javax.persistence.Entity;

@Entity
public class Inspection extends Company{
	
	
	
    public Inspection() {
    }
    
    public Inspection(long id) {
    	this.setId(id);
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    /*=============== setter and getter =================*/
    
	

}
