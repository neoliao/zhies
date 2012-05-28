package com.fortunes.zhies.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.UserService;
import com.fortunes.zhies.model.Customer;
import net.fortunes.core.service.GenericService;

import org.apache.commons.lang.xwork.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

@Component
public class CustomerService extends GenericService<Customer> {
	
	@Resource private UserService userService;
	
	@Override
	protected DetachedCriteria getConditions(String query,
			Map<String, String> queryMap) {
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		if(StringUtils.isNotEmpty(query)){
			criteria.add(Restrictions.or(
			    Restrictions.ilike("name", query, MatchMode.ANYWHERE),
			    Restrictions.ilike("linkman", query, MatchMode.ANYWHERE)
			));
		}
		return criteria;
	}
	
	public List<Customer> findMyCustomer(User authedUser){
		if(userService.ownRole(authedUser, "manager")||userService.ownRole(authedUser, "financials")){
			return this.find("from Customer t");
		}else{
			return this.find("select distinct t from Customer t where " +
					" t.sales = ? or ? in elements(t.salesAsistant) ",authedUser,authedUser);
			
		}
	}
	
}
