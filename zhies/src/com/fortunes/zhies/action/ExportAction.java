package com.fortunes.zhies.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Dict;

import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;

import org.eclipse.birt.report.presentation.aggregation.dialog.ExportReportDialogFragment;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.zhies.model.BusinessInstance;
import com.fortunes.zhies.model.CustomsBroker;
import com.fortunes.zhies.model.Export;
import com.fortunes.zhies.model.Inspection;
import com.fortunes.zhies.model.Item;
import com.fortunes.zhies.model.TruckCompany;
import com.fortunes.zhies.service.BusinessService;
import com.fortunes.zhies.service.ExportService;

@Component @Scope("prototype")
public class ExportAction extends GenericAction<Export> {
	
	private ExportService exportService;
	private BusinessService businessService;
	
	private String[] names;
	private String[] models;
	private double[] prices;
	private double[] quantitys;
	private String[] units;
	
	String[] codes = new String[]{
			"A","B","C","D","E","F"
	};
	
	@Override
	public String create() throws Exception {
		Export export = new Export();
		export.setCustomer(AppHelper.toCustomer(p("customer")));
		export.setLoadingCity(p("loadingCity"));
		export.setLoadingPort(p("loadingPort"));
		export.setDestination(p("destination"));
		export.setDestinationPort(p("destinationPort"));
		export.setCurrency(AppHelper.toDict(p("currency")));
		export.setReportPortDate(pDate("reportPortDate"));
		export.setCabNo(p("cabNo"));
		export.setCabType(p("cabType"));
		export.setVerificationCompany(AppHelper.toVerificationCompany(p("verificationCompany")));
		export.setVerificationFormNo(p("verificationFormNo"));
		
		export.setCreateDate(new Date());
		export.setStatus(Export.Status.CREATED);
		export.setSales(authedUser);
		
		List<Item> items = new ArrayList<Item>();
		for(int i = 0 ;i < names.length; i++){
			Item newItem = new Item();
			newItem.setName(names[i]);
			newItem.setModel(models[i]);
			newItem.setPrice(prices[i]);
			newItem.setQuantity(quantitys[i]);
			newItem.setUnit(units[i]);
			items.add(newItem);
		}
		
		List<BusinessInstance> busis = new ArrayList<BusinessInstance>();
		
		
		for(String code : codes){
			String checkedValue = p("checkedBusiness-"+code);
			if(checkedValue.equals("on")){
				BusinessInstance instance = new BusinessInstance();
				instance.setBusiness(businessService.getBusinessByCoce(code));
				instance.setCost(pDouble("cost-"+code));
				instance.setActualCost(pDouble("salesPrice-"+code));
				busis.add(instance);
			}
		}
		
		exportService.createExportInstance(export,items,busis);
		
		
		//jo.put(ENTITY_KEY, toJsonObject(export));
		setJsonMessage(true, export.toString().equals("")?
				"新增了一条记录!" : "新增了("+export+")的记录");
		return render(jo);
	}
	
	public String assignOperator() throws Exception {
		Export export = exportService.get(id);
		export.setStatus(Export.Status.ASSIGNED);
		export.setOperator(AppHelper.toUser(p("operator")));
		exportService.update(export);
		setJsonMessage(true, "业务操作员已经分配");
		return render(jo);
	}
	
	public String operator() throws Exception {
		Export export = exportService.get(id);
		/*export.setStatus(Export.Status.ASSIGNED);
		export.setOperator(AppHelper.toUser(p("operator")));*/
		
		//for CustomsBroker 报关
		export.setCustomsBroker(AppHelper.toCustomsBroker(p("CustomsBroker")));
		export.setLoadingPort(p("loadingPort"));//装运口岸
		export.setDestination(p("destination"));//目的地
		export.setMark(p("mark"));//唛头
		export.setContractNo(p("contractNo"));//合同号
		export.setContractDate(pDate("contractDate"));//合同日期
		export.setInvoiceNo(p("invoiceNo"));//发票号
		export.setInvoiceDate(pDate("invoiceDate"));//发票日期
		export.setTradeType(p("tradeType"));//成交方式
		export.setGrossWeight(pDouble("grossWeight"));//毛重KG
		export.setNetWeight(pDouble("netWeight"));//净重KG
		
		//for 产地证
		export.setProducerNo(p("producerNo"));//产地证号
		export.setPackageNumber(pInt("packageNumber"));//箱数
		export.setProduceDate(pDate("produceDate"));//产地日期
		
		//for inspection 商检
		export.setInspection(AppHelper.toInspection(p("inspection")));//商检行
		export.setExportPort(p("exportPort"));//出口口岸
		export.setInspectionTransType(p("inspectionTransType"));//商检运输方式
		
		//for transport
		export.setTruckCompany(AppHelper.toTruckCompany(p("truckCompany")));//拖车公司
		export.setTransportType(AppHelper.toDict(p("transportType")));//运输方式
		export.setLoadingFactory(p("loadingFactory"));//装载工厂
		export.setLoadingFactoryAddr(p("loadingFactoryAddr"));//装载工厂地址
		export.setDeliverPort(p("deliverPort"));//发货港口
		export.setDriver(p("driver"));//司机
		export.setDriverPhone(p("driverPhone"));//司机电话
		export.setTruckLicense(p("truckLicense"));//车牌号
		
		//for 国际运输
		export.setShipType(AppHelper.toDict(p("shipType")));//国际运输方式,海运,空运 
		export.setDestinitionPort(p("destinitionPort"));//目的港
		export.setVolume(pDouble("volume"));//体积
		export.setWeight(pInt("weight"));//重量
		export.setLadingBillNo(p("ladingBillNo"));//提单号
		
		
		exportService.update(export);
		setJsonMessage(true, "业务操作员已经分配");
		return render(jo);
	}
	
	
	
	public String submitToAssign() throws Exception {
		Export export = exportService.get(id);
		export.setStatus(Export.Status.SUBMITED);
		exportService.update(export);
		setJsonMessage(true, "业务纪录已经提交");
		return render(jo);
	}
	
	protected void setEntity(Export e) throws ParseException{
		/*//new create
		if(e.getId() == 0){
			e.setCreateDate(p("createDate"));
		}else{//update
			
			
		}*/
		
	}
	
	protected JSONObject toJsonObject(Export e) throws ParseException{
		AppHelper record = new AppHelper();
		record.put("id", e.getId());
		record.put("createDate", e.getCreateDate());
		record.put("customer", e.getCustomer());
		Item item = e.getItems().get(0);
		record.put("itemDesc", item.getName()+" "+item.getModel());
		record.put("status", e.getStatus().name());
		record.put("sales", e.getSales());
		record.put("operator", e.getOperator());
		return record.getJsonObject();
	}
	
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<Export> getDefService() {
		return exportService;
	}
	
	public void setExportService(ExportService exportService) {
		this.exportService = exportService;
	}

	public ExportService getExportService() {
		return exportService;
	}

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public String[] getModels() {
		return models;
	}

	public void setModels(String[] models) {
		this.models = models;
	}

	public double[] getPrices() {
		return prices;
	}

	public void setPrices(double[] prices) {
		this.prices = prices;
	}

	public double[] getQuantitys() {
		return quantitys;
	}

	public void setQuantitys(double[] quantitys) {
		this.quantitys = quantitys;
	}

	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}

	public BusinessService getBusinessService() {
		return businessService;
	}

	public void setUnits(String[] units) {
		this.units = units;
	}

	public String[] getUnits() {
		return units;
	}

}
