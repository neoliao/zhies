package com.fortunes.zhies.action;

import java.util.List;

import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.UserService;
import com.fortunes.zhies.model.BusinessInstance;
import com.fortunes.zhies.model.Export;
import com.fortunes.zhies.model.Item;
import com.fortunes.zhies.model.Trade;
import com.fortunes.zhies.service.TradeService;

@Component @Scope("prototype")
public class TradeAction extends GenericAction<Trade> {
	
	private TradeService tradeService;
	private UserService userService;
	
	@Override
	protected void setEntity(Trade entity) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected JSONObject toJsonObject(Trade e) throws Exception {
		AppHelper record = new AppHelper();
		record.put("id", e.getId());
		record.put("code", e.getCode());
		record.put("createDate", e.getCreateDate());
		record.put("reportPortDate", ((Export)e).getReportPortDate());
		record.put("buyer", e.getBuyer());
		record.put("buyerName", e.getBuyer().getName());
		record.put("customer", e.getCustomer());
		record.put("customerName", e.getCustomer().getName());
		record.put("sales", e.getSales());
		record.put("salesName", e.getSales().getDisplayName());
		record.put("operator", e.getOperator());
		record.put("operatorName", e.getOperator().getName());
		record.put("loadingPort", ((Export)e).getLoadingPort());
		
		record.put("verificationCompany", ((Export)e).getVerificationCompany());
		record.put("verificationFormNo", ((Export)e).getVerificationFormNo());
		
		double totalSalesPrice = 0.0;
		double totalCost = 0.0;
		List<BusinessInstance> busiList = e.getBusinessInstances();
		for(BusinessInstance b : busiList){
			totalSalesPrice += b.getSalesPrice();
			totalCost += b.getCost();
		}
		
		record.put("totalSalesPrice", e.getTotalSalesPrice());
		record.put("totalActuralCost", e.getTotalActualCost());
		record.put("totalCost", e.getTotalCost());
		record.put("profit", e.getTotalSalesPrice()-e.getTotalActualCost());
		record.put("commission", e.getTotalSalesPrice()-e.getTotalCost());
		
		record.put("itemDesc", e.getItemDesc());
		return record.getJsonObject();
	}
	
	public String commissionSummary() throws Exception{
		String userId = authedUser.getId();
		if(userService.ownRole(authedUser, "manager")){
			userId = p("salesId");
		}
		String monthString = p("year")+'-'+p("month");
		List<Object[]> list = tradeService.queryCommissionSummary(start,limit,userId,"");
		JSONArray jsonArray = new JSONArray();
		for(Object[] oa : list){
			JSONObject json = new JSONObject();
			json.put("month", oa[0]);
			json.put("salesName", oa[1]);
			json.put("salesId", oa[2]);
			
			json.put("totalSalesPrice", oa[3]);
			json.put("totalCost", oa[4]);
			json.put("commission", oa[5]);
			
			jsonArray.add(json);
		}
		jo.put(DATA_KEY, jsonArray);
		//jo.put(TOTAL_COUNT_KEY, count);
		return render(jo);
	}
	
	public String commissionDetail() throws Exception{
		String monthTag = p("monthTag");
		long userId = Long.parseLong(p("userId"));
		List<Trade> list = tradeService.queryCommissionDetail(monthTag,userId);
		JSONArray jsonArray = new JSONArray();
		for(Trade e : list){
			jsonArray.add(toJsonObject(e));
		}
		jo.put(DATA_KEY, jsonArray);
		return render(jo);
	}
	
	public String report() throws Exception{
		queryMap.put("tradeType", p("tradeType"));
		queryMap.put("customerId", p("customer"));
		
		queryMap.put("dateTag", p("year")+"-"+p("month"));
		return super.list();
	}
	
	public String salesPieReport() throws Exception{
		
		String monthTag = p("year")+"-"+p("month");
		
		List<Object[]> list = tradeService.queryProfitSummary(monthTag);
		JSONArray jsonArray = new JSONArray();
		for(Object[] oa : list){
			JSONObject json = new JSONObject();
			int i = 0;
			json.put("month", oa[i++]);
			json.put("salesName", oa[i++]);
			json.put("salesId", oa[i++]);
			/*json.put("operatorName", oa[i++]);
			json.put("operatorId", oa[i++]);
			json.put("customerName", oa[i++]);
			json.put("customerId", oa[i++]);
			json.put("buyerName", oa[i++]);
			json.put("buyerId", oa[i++]);*/
			
			json.put("totalSalesPrice", oa[i++]);
			json.put("totalCost", oa[i++]);
			json.put("totalProfit",oa[i++]);
			
			jsonArray.add(json);
		}
		jo.put(DATA_KEY, jsonArray);
		return render(jo);
	}
	
public String customerPieReport() throws Exception{
		
		String monthTag = p("year")+"-"+p("month");
		
		List<Object[]> list = tradeService.queryProfitSummaryForCustomer(monthTag);
		JSONArray jsonArray = new JSONArray();
		for(Object[] oa : list){
			JSONObject json = new JSONObject();
			int i = 0;
			json.put("month", oa[i++]);
			json.put("customerName", oa[i++]);
			json.put("customerId", oa[i++]);
			
			json.put("totalSalesPrice", oa[i++]);
			json.put("totalCost", oa[i++]);
			json.put("totalProfit",oa[i++]);
			
			jsonArray.add(json);
		}
		jo.put(DATA_KEY, jsonArray);
		return render(jo);
	}
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<Trade> getDefService() {
		return tradeService;
	}
	
	public void setTradeService(TradeService tradeService) {
		this.tradeService = tradeService;
	}

	public TradeService getTradeService() {
		return tradeService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}

	

}
