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
import com.fortunes.zhies.model.TruckCompany;
import com.fortunes.zhies.service.TruckCompanyService;

@Component @Scope("prototype")
public class TruckCompanyAction extends GenericAction<TruckCompany> {
	
	private TruckCompanyService truckCompanyService;
	
	protected void setEntity(TruckCompany e) throws ParseException{
		e.setName(p("name"));
		e.setAddress(p("address"));
		e.setEmail(p("email"));
		e.setTel(p("tel"));
		e.setFax(p("fax"));
		e.setQq(p("qq"));
		e.setLinkman(p("linkman"));
		e.setLinkmanTel(p("linkmanTel"));
		e.setLinkmanEmail(p("linkmanEmail"));
	}
	
	protected JSONObject toJsonObject(TruckCompany e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("name", e.getName());
		record.put("address", e.getAddress());
		record.put("email", e.getEmail());
		record.put("tel", e.getTel());
		record.put("fax", e.getFax());
		record.put("qq", e.getQq());
		record.put("linkman", e.getLinkman());
		record.put("linkmanTel", e.getLinkmanTel());
		record.put("linkmanEmail", e.getLinkmanEmail());
		return record.getJsonObject();
	}
	
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<TruckCompany> getDefService() {
		return truckCompanyService;
	}
	
	public void setTruckCompanyService(TruckCompanyService truckCompanyService) {
		this.truckCompanyService = truckCompanyService;
	}

	public TruckCompanyService getTruckCompanyService() {
		return truckCompanyService;
	}

}