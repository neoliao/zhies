package com.fortunes.zhies.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.fortunes.core.service.GenericService;

import org.apache.commons.lang.xwork.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.UserService;
import com.fortunes.zhies.model.Accounts;
import com.fortunes.zhies.model.BusinessInstance;
import com.fortunes.zhies.model.Import;
import com.fortunes.zhies.model.Item;
import com.fortunes.zhies.model.Trade.Status;

@Component
public class ImportService extends GenericService<Import> {
	
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

	
	

	public void createImportInstance(Import imports,
			List<BusinessInstance> busis) throws Exception {
		
		imports.setCode(nextImportCode(imports));
		add(imports);
		
		for(BusinessInstance b : busis){
			b.setTrade(imports);
			this.getHt().save(b);
		}
		
		
		
	}
	
	public void updateImportInstance(Import imports, List<BusinessInstance> busis) {
		
		this.getHt().deleteAll(imports.getBusinessInstances());
		for(BusinessInstance b : busis){
			b.setTrade(imports);
			this.getHt().save(b);
		}
		
		update(imports);
		
	}
	
	private String nextImportCode(Import imports){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		long v = codeSequenceService.nextImportSequence();
		return "I-"+format.format(imports.getCreateDate())+"-"+v;
	}

	public void updateOperator(Import imports, List<Item> items,long[] deletedItemIds) {
		if(deletedItemIds != null){
			for(long itemId : deletedItemIds){
				this.getHt().delete(new Item(itemId));
			}
		}
		for(Item i : items){
			i.setTrade(imports);
			this.getHt().saveOrUpdate(i);
		}
		update(imports);
	}

	public void confirmCost(Import imports, List<Accounts> accountsList) {
		List<BusinessInstance> busiList = imports.getBusinessInstances();
		double totalSalesPrice = 0.0;
		double totalCost = 0.0;
		double totalActualCost = 0.0;
		int totalPackage = 0;
		
		for(BusinessInstance b : busiList){
			totalSalesPrice += b.getSalesPrice();
			totalCost += b.getCost();
			totalActualCost += b.getActualCost();
		}
		for(Item i : imports.getItems()){
			totalPackage += i.getPackageQuantity();
		}
		imports.setTotalSalesPrice(totalSalesPrice);
		imports.setTotalCost(totalCost);
		imports.setTotalActualCost(totalActualCost);
		imports.setTotalPackage(totalPackage);
		imports.setFinishDate(new Date());
		
		for(Accounts a : accountsList){
			this.getHt().save(a);
		}
		update(imports);
		
	}

	public void copy(Import fromImport, Import toImport) {
		this.getHt().deleteAll(toImport.getItems());
		
		for(Item item : fromImport.getItems()){
			item.setTrade(toImport);
			Item newItem = new Item();
			this.getHt().save(newItem);
			item.setId(newItem.getId());
			this.getHt().evict(item);
			this.getHt().merge(item);
		}
		
		//retail data
		fromImport.setId(toImport.getId());
		fromImport.setCode(toImport.getCode());
		fromImport.setCreateDate(toImport.getCreateDate());
		fromImport.setFinishDate(toImport.getFinishDate());
		fromImport.setEndedDate(toImport.getEndedDate());
		fromImport.setItemDesc(toImport.getItemDesc());
		fromImport.setItemQuantity(toImport.getItemQuantity());
		fromImport.setCustomer(toImport.getCustomer());
		fromImport.setSales(toImport.getSales());
		fromImport.setOperator(toImport.getOperator());
		fromImport.setTotalActualCost(0.0);
		fromImport.setTotalCost(0.0);
		fromImport.setTotalSalesPrice(0.0);
		
		this.getHt().evict(fromImport);
		fromImport.setStatus(Status.OPERATOR_SAVED);
		this.getHt().merge(fromImport);
		
	}


	
	
}
