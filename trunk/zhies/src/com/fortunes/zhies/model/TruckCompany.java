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
public class TruckCompany extends Model{
	
	@Id @GeneratedValue
	private long id;
	
	private String name;//公司名
	
	private String address;//地址
	
	private String email;//电子邮件
	
	private String tel;//电话
	
	private String fax;//传真
	
	private String qq;//QQ
	
	private String linkman;//联系人
	
	private String linkmanTel;//联系人电话
	
	private String linkmanEmail;//联系人邮件
	
	
    public TruckCompany() {
    }
    
    public TruckCompany(long id) {
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
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getTel() {
		return tel;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getFax() {
		return fax;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getQq() {
		return qq;
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getLinkman() {
		return linkman;
	}
	public void setLinkmanTel(String linkmanTel) {
		this.linkmanTel = linkmanTel;
	}

	public String getLinkmanTel() {
		return linkmanTel;
	}
	public void setLinkmanEmail(String linkmanEmail) {
		this.linkmanEmail = linkmanEmail;
	}

	public String getLinkmanEmail() {
		return linkmanEmail;
	}

}
