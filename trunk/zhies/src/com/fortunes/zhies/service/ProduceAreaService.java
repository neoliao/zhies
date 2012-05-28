package com.fortunes.zhies.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.xwork.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import net.fortunes.core.service.GenericService;

import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.UserService;
import com.fortunes.zhies.model.Accounts;
import com.fortunes.zhies.model.BusinessInstance;
import com.fortunes.zhies.model.Company;
import com.fortunes.zhies.model.Export;
import com.fortunes.zhies.model.Item;
import com.fortunes.zhies.model.ProduceArea;

@Component
public class ProduceAreaService extends GenericService<ProduceArea> {
	
	@Resource private CodeSequenceService codeSequenceService;
	@Resource private UserService userService;
	
	@Override
	protected DetachedCriteria getConditions(String query,
			Map<String, String> queryMap) {
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		
		if(StringUtils.isNotEmpty(queryMap.get("userId"))){
			User user = userService.get(queryMap.get("userId"));
			if(!(userService.ownRole(user, "manager") || userService.ownRole(user, "financials"))){
				criteria.add(Restrictions.or(
					Restrictions.eq("sales", user),
					Restrictions.eq("operator", user)
				));
			}
		}
		if(StringUtils.isNotEmpty(query)){
			criteria.createAlias("customer", "c");
			criteria.createAlias("loadingPort", "p",DetachedCriteria.LEFT_JOIN);
			
			criteria.add(Restrictions.or(
				Restrictions.or(
				    Restrictions.ilike("itemDesc", query, MatchMode.ANYWHERE),
				    Restrictions.ilike("p.text", query, MatchMode.ANYWHERE)
				),
				Restrictions.or(
				    Restrictions.ilike("code", query, MatchMode.ANYWHERE),
				    Restrictions.ilike("c.name", query, MatchMode.ANYWHERE)
				)
			));
		}
		criteria.addOrder(Order.desc("createDate"));
		return criteria;
	}

	public void createProduceAreaInstance(ProduceArea produceArea,
			List<BusinessInstance> busis) throws Exception {
		produceArea.setCode(nextProduceAraCode(produceArea));
		add(produceArea);
		
		for(BusinessInstance b : busis){
			b.setTrade(produceArea);
			this.getHt().save(b);
		}
		
	}

	public void updateProduceAreaInstance(ProduceArea produceArea,
			List<BusinessInstance> busis) {
		this.getHt().deleteAll(produceArea.getBusinessInstances());
		for(BusinessInstance b : busis){
			b.setTrade(produceArea);
			this.getHt().save(b);
		}
		
		update(produceArea);
		
	}
	
	private String nextProduceAraCode(ProduceArea produceArea) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		long v = codeSequenceService.nextProduceAreaSequence();
		return "P-"+format.format(produceArea.getCreateDate())+"-"+v;
	}

	public Company getMCHCompany() {
		return (Company)this.getHt().find("from Company c where c.code = ?", "SZMCH").get(0);
	}
	
	public void confirmCost(ProduceArea e, List<Accounts> accountsList) {
		List<BusinessInstance> busiList = e.getBusinessInstances();
		double totalSalesPrice = 0.0;
		double totalCost = 0.0;
		double totalActualCost = 0.0;
		int totalPackage = 0;
		
		for(BusinessInstance b : busiList){
			totalSalesPrice += b.getSalesPrice();
			totalCost += b.getCost();
			totalActualCost += b.getActualCost();
			
		}
		
		for(Item i : e.getItems()){
			totalPackage += i.getPackageQuantity();
		}
		e.setTotalSalesPrice(totalSalesPrice);
		e.setTotalCost(totalCost);
		e.setTotalActualCost(totalActualCost);
		e.setTotalPackage(totalPackage);
		e.setFinishDate(new Date());
		
		for(Accounts a : accountsList){
			this.getHt().save(a);
		}
		update(e);
		
	}

}
