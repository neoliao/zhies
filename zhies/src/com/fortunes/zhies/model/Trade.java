package com.fortunes.zhies.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.fortunes.core.Model;

import com.fortunes.fjdp.admin.model.User;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public class Trade extends Model {
	
	public enum Status{
		CREATED,
		ASSIGNED,
		OPERATOR_SAVED,
		OPERATOR_SUBMITED,
		COST_CONFIRMED,
		FINISHED
	}
	
	@Id @GeneratedValue
	private long id;
	
	private String code;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;//创建日期
	
	@ManyToOne
	private User sales;//销售业务员
	
	@ManyToOne
	private User operator;//操作员
	
	@Enumerated(EnumType.STRING)
	private Status status;//业务状态
	
	@ManyToOne
	private Customer customer;//客户
	
	@ManyToOne
	private Buyer buyer;
	
	private String itemDesc;
	
	private String itemQuantity;
	
	@OneToMany(mappedBy = "export")
	private List<Item> items = new ArrayList<Item>();//货物
	
	@OneToMany(mappedBy = "export")
	private List<BusinessInstance> businessInstances = new ArrayList<BusinessInstance>();//包含的服务
	
	private double totalSalesPrice;
	private double totalCost;
	private double totalActualCost;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public User getSales() {
		return sales;
	}

	public void setSales(User sales) {
		this.sales = sales;
	}

	public User getOperator() {
		return operator;
	}

	public void setOperator(User operator) {
		this.operator = operator;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public List<BusinessInstance> getBusinessInstances() {
		return businessInstances;
	}

	public void setBusinessInstances(List<BusinessInstance> businessInstances) {
		this.businessInstances = businessInstances;
	}

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public String getItemQuantity() {
		return itemQuantity;
	}

	public void setItemQuantity(String itemQuantity) {
		this.itemQuantity = itemQuantity;
	}

	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

	public Buyer getBuyer() {
		return buyer;
	}

	public double getTotalSalesPrice() {
		return totalSalesPrice;
	}

	public void setTotalSalesPrice(double totalSalesPrice) {
		this.totalSalesPrice = totalSalesPrice;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public double getTotalActualCost() {
		return totalActualCost;
	}

	public void setTotalActualCost(double totalActualCost) {
		this.totalActualCost = totalActualCost;
	}
	
}
