package com.fortunes.zhies.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fortunes.fjdp.AppHelper;
import com.fortunes.zhies.model.Accounts;
import com.fortunes.zhies.model.BusinessInstance;
import com.fortunes.zhies.model.Company;
import com.fortunes.zhies.model.Trade;
import com.fortunes.zhies.model.ProduceArea;
import com.fortunes.zhies.model.Accounts.AccountsType;
import com.fortunes.zhies.service.BusinessService;
import com.fortunes.zhies.service.ProduceAreaService;

@Component @Scope("prototype")
public class ProduceAreaAction extends GenericAction<ProduceArea> {
	
	private BusinessService businessService;
	private ProduceAreaService produceAreaService;
	
	private long[] businessInstanceIds;
	private long[] companyIds;
	private double[] mustPayAmounts;
	private double[] mustGainAmounts;
	
	@Override
	public String list() throws Exception {
		queryMap.put("userId", authedUser.getId());
		return super.list();
	}
	
	/* addProducerCertificate */
	public String create() throws Exception{
		ProduceArea produceArea = new ProduceArea();
		setEntity(produceArea);
		produceAreaService.createProduceAreaInstance(produceArea,findBusinessInstance());
		
		setJsonMessage(true, produceArea.toString().equals("")?
				"新增了一条记录!" : "新增了("+produceArea+")的记录");
		return render(jo);
	}
	
	public String update() throws Exception{
		ProduceArea produceArea = this.getDefService().get(id);
		setEntity(produceArea);
		produceAreaService.updateProduceAreaInstance(produceArea,findBusinessInstance());
		
		setJsonMessage(true, produceArea.toString().equals("")?
				"修改了一条记录!" : "修改了("+produceArea+")的记录");
		return render(jo);
	}
	
	public String submitOperator() throws Exception {
		ProduceArea e = produceAreaService.get(id);
		e.setStatus(Trade.Status.OPERATOR_SUBMITED);
		produceAreaService.update(e);
		setJsonMessage(true, "操作纪录已经提交");
		return render(jo);
	}
	
	
	public String mustPay() throws Exception {
		ProduceArea e = produceAreaService.get(id);
		List<BusinessInstance> instances = e.getBusinessInstances();
		JSONArray busiInstJa = new JSONArray();
		for(BusinessInstance bi : instances){
			JSONObject busiInstJo = new JSONObject();
			Company company = produceAreaService.getMCHCompany(); 
			busiInstJo.put("id", bi.getId());
			busiInstJo.put("name", bi.getBusiness().getCode()+"-"+bi.getBusiness().getName());
			JSONObject joCompany = new JSONObject();
			if(company != null){
				joCompany.put("id", company.getId());
				joCompany.put("text", company.getName());
			}
			busiInstJo.put("company",joCompany );
			busiInstJo.put("amount", bi.getCost());
			busiInstJa.add(busiInstJo);
		}
		jo.put(DATA_KEY, busiInstJa);
		return render(jo);
	}
	
	public String mustGain() throws Exception {
		ProduceArea e = produceAreaService.get(id);
		List<BusinessInstance> instances = e.getBusinessInstances();
		JSONArray busiInstJa = new JSONArray();
		for(BusinessInstance bi : instances){
			JSONObject busiInstJo = new JSONObject();
			busiInstJo.put("id", bi.getId());
			busiInstJo.put("name", bi.getBusiness().getCode()+"-"+bi.getBusiness().getName());
			busiInstJo.put("amount", bi.getSalesPrice());
			busiInstJa.add(busiInstJo);
		}
		jo.put(DATA_KEY, busiInstJa);
		return render(jo);
	}
	
	public String confirmCost() throws Exception {
		ProduceArea e = produceAreaService.get(id);
		e.setStatus(Trade.Status.COST_CONFIRMED);
		
		List<Accounts> accountsList = new ArrayList<Accounts>();
		for(int i = 0 ;i < businessInstanceIds.length ; i++){
			Accounts pay = new Accounts();
			pay.setAccountsType(AccountsType.MUST_PAY);
			pay.setTrade((Trade)e);
			pay.setAmountInPlan(mustPayAmounts[i]);
			pay.setAmountDone(new Double(0));
			pay.setFinished(false);
			pay.setCompany(AppHelper.toCompany(companyIds[i]+""));
			accountsList.add(pay);
			
			businessService.updateBusinessInstance(businessInstanceIds[i],mustPayAmounts[i]);
			
		}
		double total = 0.0;
		for(int i = 0 ;i < mustGainAmounts.length ; i++){
			total += mustGainAmounts[i];
		}
		
		Accounts gain = new Accounts();
		gain.setAccountsType(AccountsType.MUST_GAIN);
		gain.setTrade((Trade)e);
		gain.setAmountInPlan(total);
		gain.setAmountDone(new Double(0));
		gain.setFinished(false);
		gain.setCompany(AppHelper.toCompany(e.getCustomer().getId()+""));
		accountsList.add(gain);
		
		
		produceAreaService.confirmCost(e,accountsList);
		setJsonMessage(true, "应收应付已经确认");
		return render(jo);
	}
	
	private List<BusinessInstance> findBusinessInstance(){
		List<BusinessInstance> busis = new ArrayList<BusinessInstance>();
		BusinessInstance instance = new BusinessInstance();
		instance.setBusiness(businessService.getBusinessByCoce("C"));
		instance.setCost(pDouble("cost_C"));
		instance.setSalesPrice(pDouble("salesPrice_C"));
		busis.add(instance);
		return busis;
	}

	@Override
	protected void setEntity(ProduceArea produceArea) throws Exception {
		if(produceArea.getId() == 0){
			produceArea.setCreateDate(new Date());
			produceArea.setStatus(Trade.Status.OPERATOR_SAVED);
			produceArea.setSales(authedUser);
			produceArea.setOperator(authedUser);
			produceArea.setItemDesc("产地证");
		}		
		
		produceArea.setMemo(p("memo"));
		produceArea.setBuyerName(p("buyerName"));
		produceArea.setCustomer(AppHelper.toCustomer(p("customer")));
		produceArea.setProducerCertificate(pDict("producerCertificate"));
		produceArea.setProducerNo(p("producerNo"));//产地证号
		produceArea.setPackageNumber(pInt("packageNumber"));//箱数
		produceArea.setProduceDate(pDate("produceDate"));//产地日期

	}

	@Override
	protected JSONObject toJsonObject(ProduceArea e) throws Exception {
		AppHelper record = new AppHelper();
		record.put("id", e.getId());
		record.put("memo", e.getMemo());
		record.put("code", e.getCode());
		record.put("reportPortDate", e.getReportPortDate());
		record.put("createDate", e.getCreateDate());
		record.put("customer", e.getCustomer());
		record.put("buyerName", e.getBuyerName());
		
		record.put("producerCertificate", e.getProducerCertificate());//产地证类型
		record.put("producerNo", e.getProducerNo());//产地证号
		record.put("packageNumber", e.getPackageNumber());//箱数
		record.put("produceDate", e.getProduceDate());//产地日期
		
		record.put("itemDesc", e.getItemDesc());
		record.put("itemQuantity", e.getItemQuantity());
		record.put("status", e.getStatus().name());
		record.put("sales", e.getSales());
		record.put("operator", e.getOperator());
		
		List<BusinessInstance> busiList = e.getBusinessInstances();
		for(BusinessInstance b : busiList){
			record.put("checkedBusiness_"+b.getBusiness().getCode(), "on");
			record.put("salesPrice_"+b.getBusiness().getCode(), b.getSalesPrice());
			record.put("cost_"+b.getBusiness().getCode(), b.getCost());
		}
		
		return record.getJsonObject();
	}
	
	@Override
	public GenericService<ProduceArea> getDefService() {
		return produceAreaService;
	}

	public BusinessService getBusinessService() {
		return businessService;
	}

	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}

	public ProduceAreaService getProduceAreaService() {
		return produceAreaService;
	}

	public void setProduceAreaService(ProduceAreaService produceAreaService) {
		this.produceAreaService = produceAreaService;
	}

	public long[] getBusinessInstanceIds() {
		return businessInstanceIds;
	}

	public void setBusinessInstanceIds(long[] businessInstanceIds) {
		this.businessInstanceIds = businessInstanceIds;
	}

	public long[] getCompanyIds() {
		return companyIds;
	}

	public void setCompanyIds(long[] companyIds) {
		this.companyIds = companyIds;
	}

	public double[] getMustPayAmounts() {
		return mustPayAmounts;
	}

	public void setMustPayAmounts(double[] mustPayAmounts) {
		this.mustPayAmounts = mustPayAmounts;
	}

	public double[] getMustGainAmounts() {
		return mustGainAmounts;
	}

	public void setMustGainAmounts(double[] mustGainAmounts) {
		this.mustGainAmounts = mustGainAmounts;
	}

}
