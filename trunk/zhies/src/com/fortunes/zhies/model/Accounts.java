package com.fortunes.zhies.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fortunes.fjdp.admin.model.Dict;

import net.fortunes.core.Model;

@Entity
public class Accounts extends Model {
	
	public enum AccountsType{
		MUST_PAY,
		MUST_GAIN
	}

	@Id @GeneratedValue
	private long id;
	
	@ManyToOne
	private Company company;
	
	@Enumerated(EnumType.STRING)
	private AccountsType accountsType;
	
	private Double amountInPlan;
	
	private Double amountDone;
	
	private boolean finished;
	
	@ManyToOne
	private Dict bankAccount;
	
	@Temporal(TemporalType.DATE)
	private Date finishDate;
	
	@ManyToOne
	private Trade trade;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public AccountsType getAccountsType() {
		return accountsType;
	}

	public void setAccountsType(AccountsType accountsType) {
		this.accountsType = accountsType;
	}

	public Double getAmountInPlan() {
		return amountInPlan;
	}

	public void setAmountInPlan(Double amountInPlan) {
		this.amountInPlan = amountInPlan;
	}

	public Double getAmountDone() {
		return amountDone;
	}

	public void setAmountDone(Double amountDone) {
		this.amountDone = amountDone;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Trade getTrade() {
		return trade;
	}

	public void setTrade(Trade trade) {
		this.trade = trade;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setBankAccount(Dict bankAccount) {
		this.bankAccount = bankAccount;
	}

	public Dict getBankAccount() {
		return bankAccount;
	}
	
	
}
