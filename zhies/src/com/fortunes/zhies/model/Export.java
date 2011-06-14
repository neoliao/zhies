package com.fortunes.zhies.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.User;

import net.fortunes.core.Model;

@Entity
public class Export extends Model{
	
	enum Status{
		CREATED,
		ASSIGNED,
		FINISHED
	}
	
	@Id @GeneratedValue
	private long id;
	
	private String createDate;//创建日期
	
	@ManyToOne
	private User sales;//销售业务员
	
	@ManyToOne
	private User operator;//操作员
	
	@Enumerated(EnumType.STRING)
	private Status status;//业务状态
	
	@ManyToOne
	private User currentOperator;//当前处理人
	
	@ManyToOne
	private Customer customer;//客户
	
	@OneToMany
	private List<Item> items;//货物
	
	//for CustomsBroker
	@ManyToOne
	private CustomsBroker customsBroker;
	
	private String loadingPort;//装运口岸
	
	private String destination;//目的地
	
	private String mark;//唛头
	
	private String contractNo;//合同号
	
	@Temporal(TemporalType.DATE)
	private Date contractDate;//合同日期
	
	private String invoiceNo;//发票号
	
	@Temporal(TemporalType.DATE)
	private Date invoiceDate;//发票日期
	
	private String tradeType;//成交方式
	
	private double grossWeight;//毛重KG
	
	private double netWeight;//净重KG
	
	//for inspection
	@ManyToOne
	private Inspection inspection;
	
	@ManyToOne
	private TruckCompany truckCompany;
	
	@ManyToOne
	private ShipCompany shipCompany;
	
	@ManyToOne
	private AirCompany airCompany;
	
	
    public Export() {
    }
    
    public Export(long id) {
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
	
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getCreateDate() {
		return createDate;
	}

}
