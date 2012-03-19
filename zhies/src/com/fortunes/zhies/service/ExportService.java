package com.fortunes.zhies.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.UserService;
import com.fortunes.zhies.model.Accounts;
import com.fortunes.zhies.model.BusinessInstance;
import com.fortunes.zhies.model.Company;
import com.fortunes.zhies.model.Export;
import com.fortunes.zhies.model.Item;
import com.fortunes.zhies.model.Trade.Status;

import net.fortunes.core.service.GenericService;

import org.apache.commons.lang.xwork.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExportService extends GenericService<Export> {
	
	@Resource private UserService userService;
	@Resource private BuyerService buyerService;
	@Resource private CodeSequenceService codeSequenceService;
	
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

	
	

	public void createExportInstance(Export export,
			List<BusinessInstance> busis) throws Exception {
		export.setCode(nextExportCode(export));
		add(export);
		
		for(BusinessInstance b : busis){
			b.setTrade(export);
			this.getHt().save(b);
		}
		
	}
	
	public void updateExportInstance(Export export, List<BusinessInstance> busis) {
		
		this.getHt().deleteAll(export.getBusinessInstances());
		for(BusinessInstance b : busis){
			b.setTrade(export);
			this.getHt().save(b);
		}
		
		update(export);
		
	}
	
	private String nextExportCode(Export export){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		long v = codeSequenceService.nextExportSequence();
		return "E-"+format.format(export.getCreateDate())+"-"+v;
	}

	public void updateOperator(Export export, List<Item> items,long[] deletedItemIds) {
		if(deletedItemIds != null){
			for(long itemId : deletedItemIds){
				this.getHt().delete(new Item(itemId));
			}
		}
		for(Item i : items){
			i.setTrade(export);
			this.getHt().saveOrUpdate(i);
		}
		update(export);
	}

	public void confirmCost(Export export, List<Accounts> accountsList) {
		List<BusinessInstance> busiList = export.getBusinessInstances();
		double totalSalesPrice = 0.0;
		double totalCost = 0.0;
		double totalActualCost = 0.0;
		int totalPackage = 0;
		
		for(BusinessInstance b : busiList){
			totalSalesPrice += b.getSalesPrice();
			totalCost += b.getCost();
			totalActualCost += b.getActualCost();
			
		}
		
		for(Item i : export.getItems()){
			totalPackage += i.getPackageQuantity();
		}
		export.setTotalSalesPrice(totalSalesPrice);
		export.setTotalCost(totalCost);
		export.setTotalActualCost(totalActualCost);
		export.setTotalPackage(totalPackage);
		export.setFinishDate(new Date());
		
		for(Accounts a : accountsList){
			this.getHt().save(a);
		}
		update(export);
		
	}

	public void copy(Export fromExport, Export toExport) {
		this.getHt().deleteAll(toExport.getItems());
		
		for(Item item : fromExport.getItems()){
			item.setTrade(toExport);
			Item newItem = new Item();
			this.getHt().save(newItem);
			item.setId(newItem.getId());
			this.getHt().evict(item);
			this.getHt().merge(item);
		}
		
		//retail data
		fromExport.setId(toExport.getId());
		fromExport.setCode(toExport.getCode());
		fromExport.setCreateDate(toExport.getCreateDate());
		fromExport.setFinishDate(toExport.getFinishDate());
		fromExport.setEndedDate(toExport.getEndedDate());
		fromExport.setItemDesc(toExport.getItemDesc());
		fromExport.setItemQuantity(toExport.getItemQuantity());
		fromExport.setCustomer(toExport.getCustomer());
		fromExport.setSales(toExport.getSales());
		fromExport.setOperator(toExport.getOperator());
		fromExport.setTotalActualCost(0.0);
		fromExport.setTotalCost(0.0);
		fromExport.setTotalSalesPrice(0.0);
		
		this.getHt().evict(fromExport);
		fromExport.setStatus(Status.OPERATOR_SAVED);
		this.getHt().merge(fromExport);
		
	}




	public Company getMCHCompany() {
		return (Company)this.getHt().find("from Company c where c.code = ?", "SZMCH").get(0);
	}


	
	
}
