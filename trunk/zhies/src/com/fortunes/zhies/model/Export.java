package com.fortunes.zhies.model;

import javax.persistence.Entity;

@Entity
public class Export extends Trade{
		
	
    public Export() {
    }
    
    public Export(long id) {
    	setId(id);
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    /*=============== setter and getter =================*/

}
