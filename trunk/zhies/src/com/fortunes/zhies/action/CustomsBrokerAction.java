package com.fortunes.zhies.action;

import java.text.ParseException;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.PinYin;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.zhies.model.Customer;
import com.fortunes.zhies.model.CustomsBroker;
import com.fortunes.zhies.service.CustomsBrokerService;

@Component @Scope("prototype")
public class CustomsBrokerAction extends GenericAction<CustomsBroker> {
	
	private CustomsBrokerService customsBrokerService;
	
	protected void setEntity(CustomsBroker e) throws ParseException{
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
	
	protected JSONObject toJsonObject(CustomsBroker e) throws ParseException{
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
	
	public String getCustomsBrokers() throws Exception{
		List<CustomsBroker> list = getDefService().findAll();
		JSONArray ja = new JSONArray();
		for(CustomsBroker c:list){
			String namePy = PinYin.toPinYinString(c.getName());
			if(namePy.startsWith(getQuery().toUpperCase())
					|| c.getName().startsWith(getQuery())){
				JSONObject record = new JSONObject();
				record.put("id", c.getId());
				record.put("text", c.getName());
				record.put("pinyin", namePy);
				ja.add(record);
			}	
		}
		jo.put(DATA_KEY, ja);
		return render(jo); 
	}
	
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<CustomsBroker> getDefService() {
		return customsBrokerService;
	}
	
	public void setCustomsBrokerService(CustomsBrokerService customsBrokerService) {
		this.customsBrokerService = customsBrokerService;
	}

	public CustomsBrokerService getCustomsBrokerService() {
		return customsBrokerService;
	}

}
