package com.fortunes.zhies.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import net.fortunes.core.Model;

@Entity
public class Item extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	private String name;//名称
	
	private String productCode;//商品编号
	
	private String model;//型号及描述
	
	private double price;//单价
	
	private double quantity;//数量
	
	private double totalPrice;//总价
	
	private String unit;//合同单位
	
	private double packageQuantity;//箱数
	
	private double unitQuantity;//数量
	
	private String unitForQuantity;//个,包
	
	private double grossWeight;//毛重KG
	
	private double netWeight;//净重KG
	
	private String unitForWeight;
	
	
	@ManyToOne
	private Trade trade;
	
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

	public void setUnitForQuantity(String unitForQuantity) {
		this.unitForQuantity = unitForQuantity;
	}

	public String getUnitForQuantity() {
		return unitForQuantity;
	}

	public void setUnitForWeight(String unitForWeight) {
		this.unitForWeight = unitForWeight;
	}

	public String getUnitForWeight() {
		return unitForWeight;
	}

	public void setTrade(Trade trade) {
		this.trade = trade;
	}

	public Trade getTrade() {
		return trade;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductCode() {
		return productCode;
	}

}
