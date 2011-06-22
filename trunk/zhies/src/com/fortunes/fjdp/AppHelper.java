package com.fortunes.fjdp;

import net.sf.json.JSONObject;

import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.zhies.model.Customer;
import com.fortunes.zhies.model.CustomsBroker;
import com.fortunes.zhies.model.Inspection;
import com.fortunes.zhies.model.TruckCompany;
import com.fortunes.zhies.model.VerificationCompany;

public class AppHelper extends AdminHelper {
	
	public JSONObject put(String key,Customer c){
		JSONObject jo = new JSONObject();
		if(c != null){
			jo.put("id", c.getId());
			jo.put("text", c.getName());
			jsonObject.put(key, jo);
		}
		return jo;
	}
	
	public static Customer toCustomer(String id){
		return (id == null || id.equals("")) ? null : new Customer(Long.parseLong(id));
	}
	
	public static CustomsBroker toCustomsBroker(String id){
		return (id == null || id.equals("")) ? null : new CustomsBroker(Long.parseLong(id));
	}
	
	public static Inspection toInspection(String id){
		return (id == null || id.equals("")) ? null : new Inspection(Long.parseLong(id));
	}
	
	public static TruckCompany toTruckCompany(String id){
		return (id == null || id.equals("")) ? null : new TruckCompany(Long.parseLong(id));
	}

	public static VerificationCompany toVerificationCompany(String id) {
		return (id == null || id.equals("")) ? null : new VerificationCompany(Long.parseLong(id));
	}

}
