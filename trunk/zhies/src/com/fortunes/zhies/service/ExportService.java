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
	
	@Override
	protected DetachedCriteria getConditions(String query,
			Map<String, String> queryMap) {
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		criteria.createAlias("customer", "c");
		

		if(StringUtils.isNotEmpty(queryMap.get("userId"))){
			User user = userService.get(queryMap.get("userId"));
			if(!userService.ownRole(user, "operator") && 
					(userService.ownRole(user, "sales") || userService.ownRole(user, "operator"))){
				criteria.add(Restrictions.or(
					Restrictions.eq("sales", user),
					Restrictions.eq("operator", user)
				));
			}
		}
		if(StringUtils.isNotEmpty(query)){
			criteria.add(Restrictions.or(
			    Restrictions.ilike("code", query, MatchMode.ANYWHERE),
			    Restrictions.ilike("c.name", query, MatchMode.ANYWHERE)
			));
		}
		criteria.addOrder(Order.desc("createDate"));
		return criteria;
	}

	
	

	public void createExportInstance(Export export,
			List<BusinessInstance> busis) throws Exception {
		add(export);
		
		for(BusinessInstance b : busis){
			b.setExport(export);
			this.getHt().save(b);
		}
		
		export.setCode(getExportCode(export));
		
	}
	
	public void updateExportInstance(Export export, List<BusinessInstance> busis) {
		
		this.getHt().deleteAll(export.getBusinessInstances());
		for(BusinessInstance b : busis){
			b.setExport(export);
			this.getHt().save(b);
		}
		
		update(export);
		
	}
	
	private String getExportCode(Export export){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		this.getHt().refresh(export);
		return "I-"+export.getCustomer().getCode()+"-"+format.format(export.getCreateDate())+"-"+export.getId();
	}

	public void updateOperator(Export export, List<Item> items,long[] deletedItemIds) {
		if(deletedItemIds != null){
			for(long itemId : deletedItemIds){
				this.getHt().delete(new Item(itemId));
			}
		}
		for(Item i : items){
			i.setExport(export);
			this.getHt().saveOrUpdate(i);
		}
		update(export);
	}

	public void confirmCost(Export export, List<Accounts> accountsList) {
		List<BusinessInstance> busiList = export.getBusinessInstances();
		double totalSalesPrice = 0.0;
		double totalCost = 0.0;
		double totalActualCost = 0.0;
		
		for(BusinessInstance b : busiList){
			totalSalesPrice += b.getSalesPrice();
			totalCost += b.getCost();
			totalActualCost += b.getActualCost();
		}
		export.setTotalSalesPrice(totalSalesPrice);
		export.setTotalCost(totalCost);
		export.setTotalActualCost(totalActualCost);
		
		for(Accounts a : accountsList){
			this.getHt().save(a);
		}
		update(export);
		
	}




/*	public void copy(Export oldExport) throws Exception {
		Export temp = new Export();
		this.add(temp);
		System.out.println(temp.getId());
		
		for(BusinessInstance bi : oldExport.getBusinessInstances()){
			bi.setExport(temp);
			BusinessInstance newBi = new BusinessInstance();
			this.getHt().save(newBi);
			bi.setId(newBi.getId());
			this.getHt().evict(bi);
			this.getHt().merge(bi);
		}
		
		for(Item item : oldExport.getItems()){
			item.setExport(temp);
			Item newItem = new Item();
			this.getHt().save(newItem);
			item.setId(newItem.getId());
			this.getHt().evict(item);
			this.getHt().merge(item);
		}
		
		oldExport.setId(temp.getId());
		this.getHt().evict(oldExport);
		oldExport.setCreateDate(new Date());
		oldExport.setCode(getExportCode(oldExport));
		oldExport.setStatus(Status.ASSIGNED);
		this.getHt().merge(oldExport);
	
		
	}*/




	public void copy(Export fromExport, Export toExport) {
		this.getHt().deleteAll(toExport.getItems());
		
		for(Item item : fromExport.getItems()){
			item.setExport(toExport);
			Item newItem = new Item();
			this.getHt().save(newItem);
			item.setId(newItem.getId());
			this.getHt().evict(item);
			this.getHt().merge(item);
		}
		
		//retail data
		fromExport.setId(toExport.getId());
		fromExport.setCreateDate(toExport.getCreateDate());
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


	
	
}
