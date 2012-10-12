package com.fortunes.zhies.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.UserService;
import com.fortunes.zhies.model.Accounts;
import com.fortunes.zhies.model.Accounts.AccountsType;
import com.fortunes.zhies.model.Trade;
import com.sun.awt.AWTUtilities.Translucency;

import net.fortunes.core.service.GenericService;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

@Component
public class AccountsService extends GenericService<Accounts> {
	
	@Resource private UserService userService;
	
	@Override
	protected DetachedCriteria getConditions(String query,
			Map<String, String> queryMap) {
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		criteria.createAlias("company", "c");
		criteria.createAlias("trade", "t");
		
		String type = queryMap.get("type");
		if(StringUtils.isNotEmpty(type)){
			criteria.add(
				Restrictions.eq("accountsType", AccountsType.valueOf(type))
			);
		}
		
		String customerId = queryMap.get("customerId");
		if(StringUtils.isNotEmpty(customerId)){
			criteria.add(
				Restrictions.eq("c.id", Long.parseLong(customerId))
			);
		}
		
		
		if(StringUtils.isNotEmpty(query)){
			criteria.add( Restrictions.ilike("c.name", query, MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.desc("t.createDate"));
		return criteria;
	}

	public void markAsGainedOrPayed(long[] checkedIds, Dict bankAccount,
			AccountsType accountsType,Date finishDate) {
		for(long id : checkedIds){
			Accounts accounts = this.get(id+"");
			accounts.setAmountDone(accounts.getAmountInPlan());
			accounts.setFinishDate(finishDate);
			accounts.setBankAccount(bankAccount);
			accounts.setFinished(true);
			this.update(accounts);
			
			Trade t = accounts.getTrade();
			t.setStatus(Trade.Status.FINISHED);
			t.setEndedDate(finishDate);
			this.getHt().update(t);
		}
		
	}

	
	
}
