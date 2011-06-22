package com.fortunes.zhies.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import net.fortunes.core.Model;

@Entity
public class Business extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	@Column(unique=true)
	private String code;//服务代码
	
	private String name;//服务名称
	
	
    public Business() {
    }
    
    public Business(long id) {
    	this.id = id;
    }
    
    public Business(String code, String name) {
		this.code = code;
		this.name = name;
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

}
