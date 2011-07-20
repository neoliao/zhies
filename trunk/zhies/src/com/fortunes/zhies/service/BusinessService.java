package com.fortunes.zhies.service;

import com.fortunes.zhies.model.Business;
import com.fortunes.zhies.model.BusinessInstance;

import net.fortunes.core.service.GenericService;
import org.springframework.stereotype.Component;

@Component
public class BusinessService extends GenericService<Business> {

	public Business getBusinessByCoce(String code) {
		return findUnique("from Business b where b.code = ?", code);
	}
	
	public BusinessInstance getBusinessInstance(long instanceId){
		return (BusinessInstance) getHt().get(BusinessInstance.class, instanceId);
	}

	public void updateInstance(BusinessInstance bi) {
		getHt().update(bi);
	}
	
}
