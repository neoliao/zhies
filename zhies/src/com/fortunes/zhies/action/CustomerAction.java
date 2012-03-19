package com.fortunes.zhies.action;

import java.text.ParseException;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;

import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.PinYin;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fortunes.zhies.model.Customer;
import com.fortunes.zhies.service.CustomerService;

@Component @Scope("prototype")
public class CustomerAction extends GenericAction<Customer> {
	
	private CustomerService customerService;
	
	protected void setEntity(Customer e) throws ParseException{
		e.setName(p("name"));
		e.setCode(p("code"));
		e.setAddress(p("address"));
		e.setEmail(p("email"));
		e.setTel(p("tel"));
		e.setFax(p("fax"));
		e.setQq(p("qq"));
		e.setLinkman(p("linkman"));
		e.setLinkmanTel(p("linkmanTel"));
		e.setLinkmanEmail(p("linkmanEmail"));
	}
	
	protected JSONObject toJsonObject(Customer e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("name", e.getName());
		record.put("code", e.getCode());
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
	
	public String getCustomers() throws Exception{
		List<Customer> list = getDefService().findAll();
		JSONArray ja = new JSONArray();
		for(Customer c:list){
			String namePy = PinYin.toPinYinString(c.getName());
			if(StringUtils.isEmpty(query) || namePy.indexOf(getQuery().toUpperCase()) > -1
					|| c.getName().indexOf(getQuery().toUpperCase()) > -1){
				JSONObject record = new JSONObject();
				record.put("id", c.getId());
				record.put("text", c.getCode());
				record.put("pinyin", namePy);
				ja.add(record);
			}	
		}
		jo.put(DATA_KEY, ja);
		return render(jo); 
	}
	
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<Customer> getDefService() {
		return customerService;
	}
	
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

}
