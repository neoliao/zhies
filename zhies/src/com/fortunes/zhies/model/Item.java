package com.fortunes.zhies.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fortunes.fjdp.admin.model.Dict;

import net.fortunes.core.Model;

@Entity
public class Item extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	private String name;//名称
	
	private String model;//型号及描述
	
	private double price;//单价
	
	private double quantity;//数量
	
	private String unit;//单位
	
	private double packageQuantity;//箱数
	
	private double unitQuantity;//个数
	
	private double grossWeight;//毛重KG
	
	private double netWeight;//净重KG
	
	@ManyToOne
	private Export export;
	
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


	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public void setExport(Export export) {
		this.export = export;
	}

	public Export getExport() {
		return export;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnit() {
		return unit;
	}

	public double getPackageQuantity() {
		return packageQuantity;
	}

	public void setPackageQuantity(double packageQuantity) {
		this.packageQuantity = packageQuantity;
	}

	public double getUnitQuantity() {
		return unitQuantity;
	}

	public void setUnitQuantity(double unitQuantity) {
		this.unitQuantity = unitQuantity;
	}

	public double getGrossWeight() {
		return grossWeight;
	}

	public void setGrossWeight(double grossWeight) {
		this.grossWeight = grossWeight;
	}

	public double getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(double netWeight) {
		this.netWeight = netWeight;
	}
	

}
