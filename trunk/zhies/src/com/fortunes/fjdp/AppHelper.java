package com.fortunes.fjdp;

import net.sf.json.JSONObject;

import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.zhies.model.AirCompany;
import com.fortunes.zhies.model.Buyer;
import com.fortunes.zhies.model.Company;
import com.fortunes.zhies.model.Customer;
import com.fortunes.zhies.model.CustomsBroker;
import com.fortunes.zhies.model.Inspection;
import com.fortunes.zhies.model.ShipCompany;
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
	
	public JSONObject put(String key,Buyer c){
		JSONObject jo = new JSONObject();
		if(c != null){
			jo.put("id", c.getId());
			jo.put("text", c.getName());
			jsonObject.put(key, jo);
		}
		return jo;
	}
	
	public JSONObject put(String key, VerificationCompany c) {
		JSONObject jo = new JSONObject();
		if(c != null){
			jo.put("id", c.getId());
			jo.put("text", c.getName());
			jsonObject.put(key, jo);
		}
		return jo;
		
	}
	
	public JSONObject put(String key, AirCompany c) {
		JSONObject jo = new JSONObject();
		if(c != null){
			jo.put("id", c.getId());
			jo.put("text", c.getName());
			jsonObject.put(key, jo);
		}
		return jo;
		
	}
	
	public JSONObject put(String key, ShipCompany c) {
		JSONObject jo = new JSONObject();
		if(c != null){
			jo.put("id", c.getId());
			jo.put("text", c.getName());
			jsonObject.put(key, jo);
		}
		return jo;
		
	}
	
	public JSONObject put(String key, TruckCompany c) {
		JSONObject jo = new JSONObject();
		if(c != null){
			jo.put("id", c.getId());
			jo.put("text", c.getName());
			jsonObject.put(key, jo);
		}
		return jo;
		
	}
	
	public JSONObject put(String key, Inspection c) {
		JSONObject jo = new JSONObject();
		if(c != null){
			jo.put("id", c.getId());
			jo.put("text", c.getName());
			jsonObject.put(key, jo);
		}
		return jo;
		
	}
	
	public JSONObject put(String key, CustomsBroker c) {
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
	
	public static Buyer toBuyer(String id){
		return (id == null || id.equals("")) ? null : new Buyer(Long.parseLong(id));
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
	
	public static ShipCompany toShipCompany(String id){
		return (id == null || id.equals("")) ? null : new ShipCompany(Long.parseLong(id));
	}
	
	public static AirCompany toAirCompany(String id){
		return (id == null || id.equals("")) ? null : new AirCompany(Long.parseLong(id));
	}

	public static VerificationCompany toVerificationCompany(String id) {
		return (id == null || id.equals("")) ? null : new VerificationCompany(Long.parseLong(id));
	}

	public static Company toCompany(String id) {
		return (id == null || id.equals("")) ? null : new Company(Long.parseLong(id));
	}


	

}
