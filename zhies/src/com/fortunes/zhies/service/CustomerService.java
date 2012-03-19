package com.fortunes.zhies.service;

import java.util.Map;

import com.fortunes.fjdp.admin.model.User;
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
	
}
