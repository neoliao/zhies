package com.fortunes.zhies.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import net.fortunes.core.Model;

@Entity
public class BusinessInstance extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	@ManyToOne
	private Business business;//服务种类
	
	private double actualCost;
	private double salesPrice;
	private double cost;
	
	@ManyToOne
	private Trade trade;
	
	
    public BusinessInstance() {
    }
    
    public BusinessInstance(long id) {
    	this.id = id;
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    /*=============== setter and getter =================*/
    
    public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public Business getBusiness() {
		return business;
	}

	public void setBusiness(Business business) {
		this.business = business;
	}

	public double getActualCost() {
		return actualCost;
	}

	public void setActualCost(double actualCost) {
		this.actualCost = actualCost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getCost() {
		return cost;
	}

	public void setSalesPrice(double salesPrice) {
		this.salesPrice = salesPrice;
	}

	public double getSalesPrice() {
		return salesPrice;
	}

	public void setTrade(Trade trade) {
		this.trade = trade;
	}

	public Trade getTrade() {
		return trade;
	}
	


}
