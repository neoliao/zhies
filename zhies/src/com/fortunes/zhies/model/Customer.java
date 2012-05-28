package com.fortunes.zhies.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fortunes.fjdp.admin.model.User;

@Entity
public class Customer extends Company{
	
	@ManyToOne
	private User sales;
	
	@ManyToMany
	private List<User> salesAsistant = new ArrayList<User>();
	
    public Customer() {
    }
    
    public Customer(long id) {
    	this.setId(id);
    }
    
    @Override
	public String toString() {
		return "";
	}

	public void setSales(User sales) {
		this.sales = sales;
	}

	public User getSales() {
		return sales;
	}

	public void setSalesAsistant(List<User> salesAsistant) {
		this.salesAsistant = salesAsistant;
	}

	public List<User> getSalesAsistant() {
		return salesAsistant;
	}
    
    /*=============== setter and getter =================*/
    

}
