package com.fortunes.fjdp.admin.service;

import java.util.List;
import java.util.Map;

import net.fortunes.core.log.annotation.LoggerClass;
import net.fortunes.core.service.GenericService;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.model.Employee;

@Component
@LoggerClass
public class EmployeeService extends GenericService<Employee> {
	
	
	@Override
	protected DetachedCriteria getConditions(String query,Map<String, String> queryMap) {
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		if(query !=  null){
			criteria.add(Restrictions.or(
					Restrictions.ilike("name", query, MatchMode.ANYWHERE), 
					Restrictions.ilike("code", query, MatchMode.START)
			));
		}
		return criteria;
	}

	@SuppressWarnings("unchecked")
	public List<Employee> getEmployeesUnAssign() {
		return this.find(
				"select e from Employee as e left join e.user as u where u.id is null");
	}
}
