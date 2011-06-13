package com.fortunes.zhies.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fortunes.fjdp.admin.model.Dict;
import net.fortunes.core.Model;

@Entity
public class Business extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	private String code;//服务代码
	
	private String name;//服务名称
	
	private String cost;//成本价格
	
	
    public Business() {
    }
    
    public Business(long id) {
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
	
	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getCost() {
		return cost;
	}

}
