package com.fortunes.zhies.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.UserService;
import com.fortunes.zhies.model.Export;
import com.fortunes.zhies.model.Trade;

@Component
public class TradeService extends GenericService<Trade> {
	
	@Resource private UserService userService;
	
	@Override
	protected DetachedCriteria getConditions(String query,
			Map<String, String> queryMap) {
		
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		String userId = queryMap.get("userId");
		if(StringUtils.isNotEmpty(userId)){
			User user = userService.get(userId);
			if(userService.ownRole(user, "sales")){
				criteria.add(Restrictions.eq("sales", user));
			}
		}
		if(StringUtils.isNotEmpty(queryMap.get("tradeType")) && !queryMap.get("tradeType").equals("ALL")){
			criteria.add(Restrictions.eq("class", queryMap.get("tradeType").equals("EXPORT") ? Export.class : Trade.class));
		}
		if(StringUtils.isNotEmpty(queryMap.get("customerId"))){
			criteria.add(Restrictions.eq("customer.id", Long.parseLong(queryMap.get("customerId"))));
		}
		if(StringUtils.isNotEmpty(queryMap.get("dateTag"))){
			
			criteria.add(Restrictions.between("createDate", 
					Tools.getFirstDate(queryMap.get("dateTag")), Tools.getLastDate(queryMap.get("dateTag"))));
		}
		
		
		criteria.add(Restrictions.or(
			Restrictions.eq("status", Trade.Status.COST_CONFIRMED),
			Restrictions.eq("status", Trade.Status.FINISHED)
		));
		criteria.addOrder(Order.desc("createDate"));
		return criteria;
	}
	
	public List<Object[]> queryCommissionSummary(int start, int limit, String salesId,
			String monthTag) {
		String where = "where t.status = 'FINISHED' ";
		if(StringUtils.isNotEmpty(salesId)){
			where += " and t.sales.id = '"+salesId+"'";
		}
		if(StringUtils.isNotEmpty(monthTag)){
			where += " and year(t.createDate)||'-'||month(t.createDate) = '"+monthTag+"'";
		}
		return this.getHt().find("" +
				" select year(t.createDate)||'-'||month(t.createDate),t.sales.displayName,t.sales.id,sum(totalSalesPrice),sum(totalCost),sum(totalSalesPrice-totalCost) " +
				" from Trade t " +where+
				" group by t.sales.displayName,t.sales.id,year(t.createDate)||'-'||month(t.createDate) order by year(t.createDate)||'-'||month(t.createDate) desc");
		
	}

	public int queryCommissionSummaryCount(long userId, String monthTag) {
		//this.find("select count() from Trade t group by ", objects)
		return 0;
	}

	public List<Trade> queryCommissionDetail(Date firstDate, Date lastDate) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Trade> queryCommissionDetail(String monthTag,long userId) {
		return this.find("select t from Trade t " +
				" where t.status = 'FINISHED' and year(t.createDate)||'-'||month(t.createDate) = ? and t.sales.id = ?" +
				" order by t.createDate desc", monthTag,userId);
	}

	public List<Object[]> queryProfitSummary(String monthTag) {
		String where = "where t.status = 'FINISHED'  ";
		if(StringUtils.isNotEmpty(monthTag)){
			where += " and year(t.createDate)||'-'||month(t.createDate) = '"+monthTag+"'";
		}
		return this.getHt().find("" +
				" select year(t.createDate)||'-'||month(t.createDate)," +
				" t.sales.displayName, t.sales.id," +
				//" t.operator.displayName, t.operator.id," +
				//" t.customer.name, t.customer.id," +
				//" t.buyer.name, t.buyer.id," +
				" sum(totalSalesPrice), sum(totalCost), sum(totalSalesPrice-totalActualCost) " +
				" from Trade t " +where+
				" group by " +
				" year(t.createDate)||'-'||month(t.createDate)," +
				" t.sales.displayName, t.sales.id" +
				//" t.operator.displayName, t.operator.id," +
				//" t.customer.name, t.customer.id," +
				//" t.buyer.name, t.buyer.id"  +
				"  order by year(t.createDate)||'-'||month(t.createDate) desc");
	
	}

	public List<Object[]> queryProfitSummaryForCustomer(String monthTag) {
		String where = "where t.status = 'FINISHED'  ";
		if(StringUtils.isNotEmpty(monthTag)){
			where += " and year(t.createDate)||'-'||month(t.createDate) = '"+monthTag+"'";
		}
		return this.getHt().find("" +
				" select year(t.createDate)||'-'||month(t.createDate)," +
				" t.customer.name, t.customer.id," +
				" sum(totalSalesPrice), sum(totalCost), sum(totalSalesPrice-totalActualCost) " +
				" from Trade t " +where+
				" group by " +
				" year(t.createDate)||'-'||month(t.createDate)," +
				" t.customer.name, t.customer.id" +
				"  order by year(t.createDate)||'-'||month(t.createDate) desc");
	}
	
}
