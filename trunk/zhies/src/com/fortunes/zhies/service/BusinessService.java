package com.fortunes.zhies.service;

import com.fortunes.zhies.model.Business;
import net.fortunes.core.service.GenericService;
import org.springframework.stereotype.Component;

@Component
public class BusinessService extends GenericService<Business> {

	public Business getBusinessByCoce(String code) {
		return findUnique("from Business b where b.code = ?", code);
	}
	
}
