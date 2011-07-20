package com.fortunes.zhies.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.fortunes.core.Model;

@Entity
public class Invoice extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	private String title;//发票抬头
	
	private double amount;//金额
	
	private String billNo;//发票号
	
	@Temporal(TemporalType.DATE)
	private Date date;//日期
	
	
    public Invoice() {
    }
    
    public Invoice(long id) {
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
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getAmount() {
		return amount;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getBillNo() {
		return billNo;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

}
