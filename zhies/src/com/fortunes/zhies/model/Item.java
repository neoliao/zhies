package com.fortunes.zhies.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fortunes.fjdp.admin.model.Dict;

import net.fortunes.core.Model;

@Entity
public class Item extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	private String name;//名称
	
	private String model;//型号及描述
	
	private Dict currency;//币种
	
	private double price;//单价
	
	private int quantity;//数量
	
	private Dict unit;//单位
	
    public Item() {
    }
    
    public Item(long id) {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Dict getCurrency() {
		return currency;
	}

	public void setCurrency(Dict currency) {
		this.currency = currency;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Dict getUnit() {
		return unit;
	}

	public void setUnit(Dict unit) {
		this.unit = unit;
	}
	

}
