package com.fortunes.zhies.action;

import java.text.ParseException;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fortunes.zhies.model.Business;
import com.fortunes.zhies.service.BusinessService;

@Component @Scope("prototype")
public class BusinessAction extends GenericAction<Business> {
	
	private BusinessService businessService;
	
	protected void setEntity(Business e) throws ParseException{
		e.setCode(p("code"));
		e.setName(p("name"));
	}
	
	protected JSONObject toJsonObject(Business e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("code", e.getCode());
		record.put("name", e.getName());
		return record.getJsonObject();
	}
	
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<Business> getDefService() {
		return businessService;
	}
	
	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}

	public BusinessService getBusinessService() {
		return businessService;
	}

}
