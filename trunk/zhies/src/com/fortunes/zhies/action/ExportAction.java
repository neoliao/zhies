package com.fortunes.zhies.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.MoneyUtil;
import net.fortunes.util.PinYin;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.aspectj.util.FileUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.Constants;
import com.fortunes.fjdp.admin.service.UserService;
import com.fortunes.zhies.model.Accounts;
import com.fortunes.zhies.model.Accounts.AccountsType;
import com.fortunes.zhies.model.BusinessInstance;
import com.fortunes.zhies.model.Company;
import com.fortunes.zhies.model.Customer;
import com.fortunes.zhies.model.Export;
import com.fortunes.zhies.model.Item;
import com.fortunes.zhies.model.Trade;
import com.fortunes.zhies.service.BusinessService;
import com.fortunes.zhies.service.ExportService;
import com.fortunes.zhies.service.ItemService;

import flex.messaging.util.StringUtils;

@Component @Scope("prototype")
public class ExportAction extends GenericAction<Export> {
	
	private ExportService exportService;
	private BusinessService businessService;
	private UserService userService;
	private ItemService itemService;
	
	public static final String TEMPLATE_PATH = "/export_template.xls";
	
	
	private long[] deletedItemIds;
	private long[] itemIds;
	private String[] names;
	private String[] models;
	private double[] prices;
	private double[] quantitys;
	private String[] units;
	
	private double[] packageQuantitys;
	private double[] unitQuantitys;
	private String[] unitForQuantitys;
	private double[] grossWeights;
	private double[] netWeights;
	private String[] unitForWeights;
	
	private long[] businessInstanceIds;
	private long[] companyIds;
	private double[] mustPayAmounts;
	private double[] mustGainAmounts;
	
	String[] codes = new String[]{
			"A","B","C","D","E","F","G","Z"
	};
	
	
	@Override
	public String list() throws Exception {
		queryMap.put("userId", authedUser.getId());
		return super.list();
	}
	
	public String getExports() throws Exception {
		List<Export> list = getDefService().findAll();
		JSONArray ja = new JSONArray();
		for(Export c:list){
			
			if(StringUtils.isEmpty(query) 
					|| c.getCode().matches(".+"+query+".+")
					|| c.getCustomer().getName().startsWith(query)
					|| c.getBuyer().getName().startsWith(query)
					|| c.getItemDesc().startsWith(query)
					|| c.getItemQuantity().startsWith(query)){
				JSONObject record = new JSONObject();
				record.put("id", c.getId());
				record.put("text", c.getCode()+" - "+c.getCustomer().getName()+" - "+
						c.getItemDesc()+" - "+c.getItemQuantity());
				record.put("code", c.getCode());
				record.put("customerName", c.getCustomer().getName());
				//record.put("buyerName", c.getBuyer().getName());
				record.put("itemDesc", c.getItemDesc());
				record.put("itemQuantity", c.getItemQuantity());
				ja.add(record);
			}	
			
			
		}
		jo.put(DATA_KEY, ja);
		return render(jo); 
	}
	
	@Override
	public String create() throws Exception {
		Export export = new Export();
		List<BusinessInstance> busis = new ArrayList<BusinessInstance>();
		setEntity(export,busis);
		
		exportService.createExportInstance(export,busis);
		
		setJsonMessage(true, export.toString().equals("")?
				"新增了一条记录!" : "新增了("+export+")的记录");
		return render(jo);
	}
	
	@Override
	public String update() throws Exception {
		Export export = exportService.get(id);
		List<BusinessInstance> busis = new ArrayList<BusinessInstance>();
		
		setEntity(export, busis);
		
		exportService.updateExportInstance(export,busis);
		setJsonMessage(true, export.toString().equals("")?
				"更新了一条记录!" : "更新了("+export+")的记录");
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
	
	public String loadOperator() throws Exception {
		return super.edit();
		
	}
	
	public String firstLoadOperator() throws Exception {
		Export e = exportService.get(id);
		AppHelper record = new AppHelper();
		record.put("id", e.getId());
		record.put("code", e.getCode());
		record.put("reportPortDate", e.getReportPortDate());
		record.put("createDate", e.getCreateDate());
		record.put("buyer", e.getBuyer());
		record.put("loadingCity", e.getLoadingCity());
		record.put("loadingPort", e.getLoadingPort());
		record.put("loadingPortCopy", e.getLoadingPort());
		record.put("destination", e.getDestination());
		record.put("destinationPort", e.getDestinationPort());
		record.put("destinationPortCopy", e.getDestinationPort());
		record.put("currency", e.getCurrency());
		record.put("cabNo", e.getCabNo());
		record.put("cabType", e.getCabType());
		jo.put(DATA_KEY, record.getJsonObject());
		jo.put(SUCCESS_KEY, true);
		return render(jo);
	}
	
	public String operator() throws Exception {
		Export export = exportService.get(id);
		export.setStatus(Export.Status.OPERATOR_SAVED);
		
		//for CustomsBroker 报关
		export.setBuyer(AppHelper.toBuyer(p("buyer")));
		export.setCustomsBroker(AppHelper.toCustomsBroker(p("customsBroker")));//报关行
		
		export.setLoadingCity(p("loadingCity"));//装运口岸
		export.setLoadingPort(p("loadingPort"));
		export.setDestination(p("destination"));//目的地
		export.setDestinationPort(p("destinationPort"));
		export.setCurrency(AppHelper.toDict(p("currency")));
		export.setCabNo(p("cabNo"));
		export.setCabType(p("cabType"));
		export.setVerificationCompany(AppHelper.toVerificationCompany(p("verificationCompany")));
		export.setVerificationFormNo(p("verificationFormNo"));
		
		export.setMark(p("mark"));//唛头
		export.setContractNo(p("contractNo"));//合同号
		export.setContractDate(pDate("contractDate"));//合同日期
		export.setInvoiceNo(p("invoiceNo"));//发票号
		export.setInvoiceDate(pDate("invoiceDate"));//发票日期
		export.setTradeType(p("tradeType"));//成交方式
		export.setSignCity(p("signCity"));
		export.setPayCondition(p("payCondition"));
		export.setMemos(p("memos"));
		export.setTaxMemos(p("taxMemos"));
		export.setItemsCity(p("itemsCity"));
		
		export.setGrossWeight(pDouble("grossWeight"));//毛重KG
		export.setNetWeight(pDouble("netWeight"));//净重KG
		
		export.setStoragePeriod(p("storagePeriod"));
		export.setStorageVehicle(p("storageVehicle"));
		export.setPackageAndModel(p("packageAndModel"));
		
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
		if(p("shipType").equals("ship")){
			export.setShipCompany(AppHelper.toShipCompany(p("shipCompany")));
		}else{
			export.setAirCompany(AppHelper.toAirCompany(p("airCompany")));
		}
		export.setVolume(pDouble("volume"));//体积
		export.setWeight(pInt("weight"));//重量
		export.setLadingBillNo(p("ladingBillNo"));//提单号
		
		
		List<Item> items = new ArrayList<Item>();
		for(int i = 0 ;i < names.length; i++){
			Item newItem = new Item();
			if(itemIds[i] != 0){
				newItem.setId(itemIds[i]);
			}
			newItem.setName(names[i]);
			newItem.setModel(models[i]);
			newItem.setPrice(prices[i]);
			newItem.setQuantity(quantitys[i]);
			newItem.setUnit(units[i]);
			newItem.setUnitQuantity(getUnitQuantitys()[i]);
			newItem.setUnitForQuantity(getUnitForQuantitys()[i]);
			newItem.setPackageQuantity(getPackageQuantitys()[i]);
			newItem.setGrossWeight(getGrossWeights()[i]);
			newItem.setNetWeight(getNetWeights()[i]);
			newItem.setUnitForWeight(getUnitForWeights()[i]);
			items.add(newItem);
		}
		
		exportService.updateOperator(export,items,deletedItemIds);
		setJsonMessage(true, "操作已经记录");
		return render(jo);
	}
	
	public String submitOperator() throws Exception {
		Export export = exportService.get(id);
		export.setStatus(Export.Status.OPERATOR_SUBMITED);
		exportService.update(export);
		setJsonMessage(true, "操作纪录已经提交");
		return render(jo);
	}
	
	public String copy() throws Exception {
		Export toExport = exportService.get(id);
		Export fromExport = exportService.get(p("fromExport"));

		exportService.copy(fromExport,toExport);
		
		setJsonMessage(true, "复制成功");
		return render(jo);
	}
	
	public String mustPay() throws Exception {
		Export export = exportService.get(id);
		List<BusinessInstance> instances = export.getBusinessInstances();
		JSONArray busiInstJa = new JSONArray();
		for(BusinessInstance bi : instances){
			JSONObject busiInstJo = new JSONObject();
			Company company = null;
			//String name = "";
			if(bi.getBusiness().getCode().equals("A")){
				company = export.getCustomsBroker();
			}else if(bi.getBusiness().getCode().equals("B")){
				company = export.getVerificationCompany();
			}else if(bi.getBusiness().getCode().equals("C")){
				continue;
			}else if(bi.getBusiness().getCode().equals("D")){
				company = export.getInspection();
			}else if(bi.getBusiness().getCode().equals("E")){
				company = export.getTruckCompany();
			}else if(bi.getBusiness().getCode().equals("F")){
				if(export.getShipCompany() != null){
					company = export.getShipCompany();
				}else{
					company = export.getAirCompany();
				}
			}else if(bi.getBusiness().getCode().equals("G")){
				company = export.getCustomsBroker();
			}else if(bi.getBusiness().getCode().equals("Z")){
				continue;
			}  
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
		Export export = exportService.get(id);
		List<BusinessInstance> instances = export.getBusinessInstances();
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
		Export export = exportService.get(id);
		export.setStatus(Export.Status.COST_CONFIRMED);
		
		List<Accounts> accountsList = new ArrayList<Accounts>();
		for(int i = 0 ;i < businessInstanceIds.length ; i++){
			Accounts pay = new Accounts();
			pay.setAccountsType(AccountsType.MUST_PAY);
			pay.setTrade((Trade)export);
			pay.setAmountInPlan(mustPayAmounts[i]);
			pay.setAmountDone(new Double(0));
			pay.setFinished(false);
			pay.setCompany(AppHelper.toCompany(companyIds[i]+""));
			accountsList.add(pay);
			
			BusinessInstance bi = businessService.getBusinessInstance(businessInstanceIds[i]);
			bi.setActualCost(mustPayAmounts[i]);
			businessService.updateInstance(bi);
		}
		double total = 0.0;
		for(int i = 0 ;i < mustGainAmounts.length ; i++){
			total += mustGainAmounts[i];
		}
		
		Accounts gain = new Accounts();
		gain.setAccountsType(AccountsType.MUST_GAIN);
		gain.setTrade((Trade)export);
		gain.setAmountInPlan(total);
		gain.setAmountDone(new Double(0));
		gain.setFinished(false);
		gain.setCompany(AppHelper.toCompany(export.getCustomer().getId()+""));
		accountsList.add(gain);
		
		
		exportService.confirmCost(export,accountsList);
		setJsonMessage(true, "应收应付已经确认");
		return render(jo);
	}
	
	public String exportFilePoi() throws Exception{
		Export export = exportService.get(id);
		File tempFile = new File("C:/tempxls2xls");
		FileOutputStream temp = new FileOutputStream(tempFile);
		InputStream ins = ExportAction.class.getResourceAsStream(TEMPLATE_PATH);
		//org.apache.poi.ss.usermodel.Workbook book = WorkbookFactory.create(ins);
		HSSFWorkbook copy = new HSSFWorkbook(ins);
		
		HSSFCellStyle leftBorder = copy.createCellStyle();
		leftBorder.setBorderLeft(CellStyle.BORDER_THIN);
		
		HSSFCellStyle rightBorder = copy.createCellStyle();
		rightBorder.setBorderRight(CellStyle.BORDER_THIN);
		
		HSSFCellStyle leftAndRightBorder = copy.createCellStyle();
		leftAndRightBorder.setBorderLeft(CellStyle.BORDER_THIN);
		leftAndRightBorder.setBorderRight(CellStyle.BORDER_THIN);
		
		HSSFCellStyle rightAndTopBorder = copy.createCellStyle();
		rightAndTopBorder.setBorderTop(CellStyle.BORDER_THIN);
		rightAndTopBorder.setBorderRight(CellStyle.BORDER_THIN);
		
		HSSFCellStyle leftAndTopBorder = copy.createCellStyle();
		leftAndTopBorder.setBorderTop(CellStyle.BORDER_THIN);
		leftAndTopBorder.setBorderLeft(CellStyle.BORDER_THIN);		
		
		//==================== sheet 1 =========================
		HSSFSheet sheet = copy.getSheetAt(0);
		setCell(sheet,1, 3,Constants.COMPANY_NAME);
		setCell(sheet,2, 5,Constants.COMPANY_ADDR);
		setCell(sheet,2, 8,Constants.COMPANY_TEL);
		setCell(sheet,4, 8,Constants.COMPANY_FAX);
		setCell(sheet,8, 4,export.getContractNo());
		setCell(sheet,8, 6,AppHelper.toDateString(export.getContractDate()));
		setCell(sheet,8, 8,export.getSignCity());
		setCell(sheet,1, 10,export.getBuyer().getName());
		setCell(sheet,2, 12,export.getBuyer().getAddress());
		setCell(sheet,2, 15,export.getBuyer().getTel());
		setCell(sheet,4, 15,export.getBuyer().getFax());

		List<Item> items = export.getItems();
		double totalMoney  = 0.0;//总金额
		double totalQuantity  = 0;//总合同数量
		int totalUnitQuantity  = 0;//总个数
		int totalPackage  = 0;//总箱数
		double totalGrossWeight  = 0.0;//总毛重
		double totalNetWeight  = 0.0;//总净重
		String itemDesc = export.getItemDesc();
		int i = 0;
		for(Item item:items){
			double total = item.getPrice()*item.getQuantity();
			totalMoney  += total;
			totalQuantity  += item.getQuantity();
			totalUnitQuantity += item.getUnitQuantity();
			totalPackage  += item.getPackageQuantity();
			totalGrossWeight  += item.getGrossWeight();
			totalNetWeight  += item.getNetWeight();
			setCell(sheet,1, 22+i,item.getName(),leftBorder); 
			setCell(sheet,5, 22+i,item.getQuantity(),leftBorder);
			setCell(sheet,6, 22+i,item.getUnit(),rightBorder);
			setCell(sheet,7, 22+i,export.getCurrency().getText(),leftBorder);
			setCell(sheet,8, 22+i,item.getPrice(),rightBorder);
			setCell(sheet,9, 22+i,export.getCurrency().getText(),leftBorder);
			setCell(sheet,10, 22+i,total,rightBorder);
			i++;
		}
		
		setCell(sheet,9, 41,export.getTradeType());
		
		setCell(sheet,10, 43,totalMoney);
		setCell(sheet,9, 43,export.getCurrency().getText());
	
		setCell(sheet,3, 44,export.getCurrency().getDescription());
		setCell(sheet,4, 44,MoneyUtil.toChinese(totalMoney+""));
		setCell(sheet,3, 46,export.getMark());
		setCell(sheet,3, 50,export.getLoadingCity());
		setCell(sheet,5, 50,export.getDestination());
		setCell(sheet,6, 60,Constants.COMPANY_NAME);
		
		//==================== sheet 2 =========================
		HSSFSheet sheet2 = copy.getSheetAt(1);
		setCell(sheet2,3, 7,Constants.COMPANY_NAME);
		setCell(sheet2,3, 8,Constants.COMPANY_NAME_EN);
		setCell(sheet2,3, 9,export.getBuyer().getName());
		
		setCell(sheet2,9, 8,export.getInvoiceNo());
		setCell(sheet2,9, 10,AppHelper.toDateString(export.getInvoiceDate()));
		
		
		
		i = 0;
		for(Item item:items){
			double total = item.getPrice()*item.getQuantity();
			setCell(sheet2,3, 15+i,item.getName()); 
			setCell(sheet2,6, 15+i,item.getQuantity(),leftBorder);
			setCell(sheet2,7, 15+i,item.getUnit(),rightBorder);
			setCell(sheet2,8, 15+i,export.getCurrency().getText(),leftBorder);
			setCell(sheet2,9, 15+i,item.getPrice(),rightBorder);
			setCell(sheet2,10, 15+i,export.getCurrency().getText(),leftBorder);
			setCell(sheet2,11, 15+i,total,rightBorder);
			i++;
		}
		setCell(sheet2,6, 45,export.getTradeType(),leftBorder);
		setCell(sheet2,11, 45,totalMoney,rightBorder);
		
		
		//==================== sheet 3 =========================
		HSSFSheet sheet3 = copy.getSheetAt(2);
		setCell(sheet3,3, 8,Constants.COMPANY_NAME);
		setCell(sheet3,3, 9,Constants.COMPANY_NAME_EN);
		setCell(sheet3,3, 10,export.getBuyer().getName());
		setCell(sheet3,9, 8,AppHelper.toDateString(export.getInvoiceDate()));
		setCell(sheet3,9, 9,export.getInvoiceNo());
		setCell(sheet3,9, 10,export.getContractNo());
		setCell(sheet3,9, 11,export.getPayCondition());
		
		i = 0;
		for(Item item:items){
			
			setCell(sheet3,3, 15+i,item.getName()); 
			setCell(sheet3,6, 15+i,item.getPackageQuantity(),leftBorder);
			setCell(sheet3,7, 15+i,"箱",rightBorder);
			setCell(sheet3,8, 15+i,item.getUnitQuantity(),leftBorder);
			setCell(sheet3,9, 15+i,item.getUnitForQuantity(),rightBorder);
			setCell(sheet3,10, 15+i,item.getGrossWeight(),leftBorder);
			setCell(sheet3,11, 15+i,item.getUnitForWeight(),rightBorder);
			setCell(sheet3,12, 15+i,item.getNetWeight(),leftBorder);
			setCell(sheet3,13, 15+i,item.getUnitForWeight(),rightBorder);
			i++;
		}
		setCell(sheet3,6, 40,totalPackage,leftAndTopBorder);
		setCell(sheet3,7, 40,"箱",rightAndTopBorder);
		setCell(sheet3,8, 40,totalUnitQuantity,leftAndTopBorder);
		setCell(sheet3,9, 40,items.get(0).getUnitForQuantity(),rightAndTopBorder);
		setCell(sheet3,10, 40,totalGrossWeight,leftAndTopBorder);
		setCell(sheet3,11, 40,items.get(0).getUnitForWeight(),rightAndTopBorder);
		setCell(sheet3,12, 40,totalNetWeight,leftAndTopBorder);
		setCell(sheet3,13, 40,items.get(0).getUnitForWeight(),rightAndTopBorder);

		
		//==================== sheet 4 =========================
		HSSFSheet sheet4 = copy.getSheetAt(3);
		setCell(sheet4,1, 3,export.getLoadingPort(),leftBorder);
		setCell(sheet4,1, 5,Constants.COMPANY_NAME,leftBorder);
		setCell(sheet4,1, 7,Constants.COMPANY_CUSTOME_CODE,leftBorder);
		setCell(sheet4,11, 7,export.getPayCondition());
		setCell(sheet4,11, 9,export.getItemsCity());
		setCell(sheet4,4, 9,export.getDestination());
		setCell(sheet4,7, 9,export.getDestinationPort());
		setCell(sheet4,4, 11,export.getTradeType());
		
		setCell(sheet4,1, 13,export.getContractNo(),leftBorder);
		setCell(sheet4,4, 13,totalPackage,leftBorder);
		setCell(sheet4,8, 13,totalGrossWeight);
		setCell(sheet4,11, 13,totalNetWeight);
		
		setCell(sheet4,1, 17,export.getMemos(),leftAndRightBorder);
		setCell(sheet4,1, 61,export.getOperator().getEmployee().getName()
				+" "+export.getOperator().getEmployee().getMobile(),leftAndRightBorder);
		
		i = 0;
		for(Item item:items){
			double total = item.getPrice()*item.getQuantity();
			setCell(sheet4,3, 20+i,item.getName()); 
			setCell(sheet4,3, 21+i,item.getModel()); 

			setCell(sheet4,6, 20+i,item.getQuantity());
			setCell(sheet4,7, 20+i,item.getUnit());
			setCell(sheet4,10, 20+i,export.getDestination());
			setCell(sheet4,11, 20+i,item.getPrice());
			setCell(sheet4,12, 20+i,total,rightBorder);
			i += 2;
		}
		
		//==================== sheet 5 =========================
		HSSFSheet sheet5 = copy.getSheetAt(4);
		setCell(sheet5,2, 10,itemDesc);
		setCell(sheet5,2, 12,export.getItemsCity());
		setCell(sheet5,2, 14,export.getPackageAndModel());
		setCell(sheet5,2, 16,totalPackage);
		setCell(sheet5,2, 18,totalGrossWeight);
		setCell(sheet5,2, 20,totalMoney);
		setCell(sheet5,3, 16,"箱");
		setCell(sheet5,3, 18,items.get(0).getUnit());
		setCell(sheet5,3, 20,export.getCurrency().getText());
		
		setCell(sheet5,2, 22,export.getStorageVehicle());
		setCell(sheet5,2, 24,export.getLoadingPort());
		setCell(sheet5,2, 26,export.getStoragePeriod());
		setCell(sheet5,2, 32,Constants.COMPANY_ADDR);
		setCell(sheet5,2, 34,Constants.COMPANY_TEL);
		setCell(sheet5,2, 36,Constants.COMPANY_FAX);
		setCell(sheet5,6, 36,Tools.getDateString());
		
		copy.write(temp);		
		
		byte[] bytes = FileUtil.readAsByteArray(tempFile);
		return renderFile(bytes, export.getCode()+"-出口单证.xls");
		
	}
	
	private void setCell(HSSFSheet sheet,int col,int row,String s){
		HSSFCell cell = sheet.getRow(row).createCell(col);
		cell.setCellValue(s);
	}
	
	private void setCell(HSSFSheet sheet,int col,int row,double s){
		HSSFCell cell = sheet.getRow(row).createCell(col);
		cell.setCellValue(s);
	}
	
	private void setCell(HSSFSheet sheet,int col,int row,String s,HSSFCellStyle f){
		HSSFCell cell = sheet.getRow(row).createCell(col);
		cell.setCellValue(s);
		cell.setCellStyle(f);
	}
	
	private void setCell(HSSFSheet sheet,int col,int row,double s,HSSFCellStyle f){
		HSSFCell cell = sheet.getRow(row).createCell(col);
		cell.setCellValue(s);
		cell.setCellStyle(f);
	}
	
	/*public String exportFile(){
		try {
			File tempFile = new File("C:/tempxls2xls"); 
			InputStream ins = ExportAction.class.getResourceAsStream(TEMPLATE_PATH);
			Workbook workbook = Workbook.getWorkbook(ins);
			WritableWorkbook copy = Workbook.createWorkbook(tempFile, workbook);
			
			Export export = exportService.get(id);
			
			WritableCellFormat leftBorder = new WritableCellFormat();
			leftBorder.setBorder(Border.LEFT, BorderLineStyle.THIN);
			
			WritableCellFormat rightBorder = new WritableCellFormat();
			rightBorder.setBorder(Border.RIGHT, BorderLineStyle.THIN);
			
			WritableCellFormat leftAndRightBorder = new WritableCellFormat();
			leftAndRightBorder.setBorder(Border.LEFT, BorderLineStyle.THIN);
			leftAndRightBorder.setBorder(Border.RIGHT, BorderLineStyle.THIN);
			
			WritableCellFormat rightAndTopBorder = new WritableCellFormat();
			rightAndTopBorder.setBorder(Border.TOP, BorderLineStyle.THIN);
			rightAndTopBorder.setBorder(Border.RIGHT, BorderLineStyle.THIN);
			
			WritableCellFormat leftAndTopBorder = new WritableCellFormat();
			leftAndTopBorder.setBorder(Border.TOP, BorderLineStyle.THIN);
			leftAndTopBorder.setBorder(Border.LEFT, BorderLineStyle.THIN);
			
			
			
			//==================== sheet 1 =========================
			WritableSheet sheet = copy.getSheet(0);
			sheet.addCell(new jxl.write.Label(1, 3,Constants.COMPANY_NAME));
			sheet.addCell(new jxl.write.Label(2, 5,Constants.COMPANY_ADDR));
			sheet.addCell(new jxl.write.Label(2, 8,Constants.COMPANY_TEL));
			sheet.addCell(new jxl.write.Label(4, 8,Constants.COMPANY_FAX));
			sheet.addCell(new jxl.write.Label(8, 4,export.getContractNo()));
			sheet.addCell(new jxl.write.Label(8, 6,AppHelper.toDateString(export.getContractDate())));
			sheet.addCell(new jxl.write.Label(8, 8,export.getSignCity()));
			sheet.addCell(new jxl.write.Label(1, 10,export.getBuyer().getName()));
			sheet.addCell(new jxl.write.Label(2, 12,export.getBuyer().getAddress()));
			sheet.addCell(new jxl.write.Label(2, 15,export.getBuyer().getTel()));
			sheet.addCell(new jxl.write.Label(4, 15,export.getBuyer().getFax()));

			List<Item> items = export.getItems();
			double totalMoney  = 0.0;//总金额
			double totalQuantity  = 0;//总合同数量
			int totalUnitQuantity  = 0;//总个数
			int totalPackage  = 0;//总箱数
			double totalGrossWeight  = 0.0;//总毛重
			double totalNetWeight  = 0.0;//总净重
			String itemDesc = export.getItemDesc();
			int i = 0;
			for(Item item:items){
				double total = item.getPrice()*item.getQuantity();
				totalMoney  += total;
				totalQuantity  += item.getQuantity();
				totalUnitQuantity += item.getUnitQuantity();
				totalPackage  += item.getPackageQuantity();
				totalGrossWeight  += item.getGrossWeight();
				totalNetWeight  += item.getNetWeight();
				sheet.addCell(new jxl.write.Label(1, 22+i,item.getName(),leftBorder)); 
				sheet.addCell(new jxl.write.Number(5, 22+i,item.getQuantity(),leftBorder));
				sheet.addCell(new jxl.write.Label(6, 22+i,item.getUnit(),rightBorder));
				sheet.addCell(new jxl.write.Label(7, 22+i,export.getCurrency().getText(),leftBorder));
				sheet.addCell(new jxl.write.Number(8, 22+i,item.getPrice(),rightBorder));
				sheet.addCell(new jxl.write.Label(9, 22+i,export.getCurrency().getText(),leftBorder));
				sheet.addCell(new jxl.write.Number(10, 22+i,total,rightBorder));
				i++;
			}
			
			
			
			sheet.addCell(new jxl.write.Label(9, 41,export.getTradeType()));
			
			sheet.addCell(new jxl.write.Number(10, 43,totalMoney));
			sheet.addCell(new jxl.write.Label(9, 43,export.getCurrency().getText()));
			
			sheet.addCell(new jxl.write.Label(4, 44,MoneyUtil.toChinese(totalMoney+"")));
			sheet.addCell(new jxl.write.Label(3, 46,export.getMark()));
			sheet.addCell(new jxl.write.Label(3, 50,export.getLoadingCity()));
			sheet.addCell(new jxl.write.Label(5, 50,export.getDestination()));
			sheet.addCell(new jxl.write.Label(6, 60,Constants.COMPANY_NAME));
			
			
			//==================== sheet 2 =========================
			WritableSheet sheet2 = copy.getSheet(1);
			sheet2.addCell(new jxl.write.Label(3, 7,Constants.COMPANY_NAME));
			sheet2.addCell(new jxl.write.Label(3, 8,Constants.COMPANY_NAME_EN));
			sheet2.addCell(new jxl.write.Label(3, 9,export.getBuyer().getName()));
			
			sheet2.addCell(new jxl.write.Label(9, 8,export.getInvoiceNo()));
			sheet2.addCell(new jxl.write.Label(9, 10,AppHelper.toDateString(export.getInvoiceDate())));
			
			
			
			i = 0;
			for(Item item:items){
				double total = item.getPrice()*item.getQuantity();
				sheet2.addCell(new Label(3, 15+i,item.getName())); 
				sheet2.addCell(new jxl.write.Number(6, 15+i,item.getQuantity(),leftBorder));
				sheet2.addCell(new jxl.write.Label(7, 15+i,item.getUnit(),rightBorder));
				sheet2.addCell(new jxl.write.Label(8, 15+i,export.getCurrency().getText(),leftBorder));
				sheet2.addCell(new jxl.write.Number(9, 15+i,item.getPrice(),rightBorder));
				sheet2.addCell(new jxl.write.Label(10, 15+i,export.getCurrency().getText(),leftBorder));
				sheet2.addCell(new jxl.write.Number(11, 15+i,total,rightBorder));
				i++;
			}
			sheet2.addCell(new jxl.write.Label(6, 45,export.getTradeType(),leftBorder));
			sheet2.addCell(new jxl.write.Number(11, 45,totalMoney,rightBorder));
			
			
			//==================== sheet 3 =========================
			WritableSheet sheet3 = copy.getSheet(2);
			sheet3.addCell(new jxl.write.Label(3, 8,Constants.COMPANY_NAME));
			sheet3.addCell(new jxl.write.Label(3, 9,Constants.COMPANY_NAME_EN));
			sheet3.addCell(new jxl.write.Label(3, 10,export.getBuyer().getName()));
			sheet3.addCell(new jxl.write.Label(9, 8,AppHelper.toDateString(export.getInvoiceDate())));
			sheet3.addCell(new jxl.write.Label(9, 9,export.getInvoiceNo()));
			sheet3.addCell(new jxl.write.Label(9, 10,export.getContractNo()));
			sheet3.addCell(new jxl.write.Label(9, 11,export.getPayCondition()));
			
			i = 0;
			for(Item item:items){
				
				sheet3.addCell(new Label(3, 15+i,item.getName())); 
				sheet3.addCell(new jxl.write.Number(6, 15+i,item.getPackageQuantity(),leftBorder));
				sheet3.addCell(new jxl.write.Label(7, 15+i,"箱",rightBorder));
				sheet3.addCell(new jxl.write.Number(8, 15+i,item.getUnitQuantity(),leftBorder));
				sheet3.addCell(new jxl.write.Label(9, 15+i,item.getUnitForQuantity(),rightBorder));
				sheet3.addCell(new jxl.write.Number(10, 15+i,item.getGrossWeight(),leftBorder));
				sheet3.addCell(new jxl.write.Label(11, 15+i,item.getUnitForWeight(),rightBorder));
				sheet3.addCell(new jxl.write.Number(12, 15+i,item.getNetWeight(),leftBorder));
				sheet3.addCell(new jxl.write.Label(13, 15+i,item.getUnitForWeight(),rightBorder));
				i++;
			}
			sheet3.addCell(new jxl.write.Number(6, 40,totalPackage,leftAndTopBorder));
			sheet3.addCell(new jxl.write.Label(7, 40,"箱",rightAndTopBorder));
			sheet3.addCell(new jxl.write.Number(8, 40,totalUnitQuantity,leftAndTopBorder));
			sheet3.addCell(new jxl.write.Label(9, 40,items.get(0).getUnitForQuantity(),rightAndTopBorder));
			sheet3.addCell(new jxl.write.Number(10, 40,totalGrossWeight,leftAndTopBorder));
			sheet3.addCell(new jxl.write.Label(11, 40,items.get(0).getUnitForWeight(),rightAndTopBorder));
			sheet3.addCell(new jxl.write.Number(12, 40,totalNetWeight,leftAndTopBorder));
			sheet3.addCell(new jxl.write.Label(13, 40,items.get(0).getUnitForWeight(),rightAndTopBorder));

			
			//==================== sheet 4 =========================
			WritableSheet sheet4 = copy.getSheet(3);
			sheet4.addCell(new jxl.write.Label(1, 3,export.getLoadingPort(),leftBorder));
			sheet4.addCell(new jxl.write.Label(1, 5,Constants.COMPANY_NAME,leftBorder));
			sheet4.addCell(new jxl.write.Label(1, 7,Constants.COMPANY_CUSTOME_CODE,leftBorder));
			sheet4.addCell(new jxl.write.Label(11, 7,export.getPayCondition()));
			sheet4.addCell(new jxl.write.Label(11, 9,export.getItemsCity()));
			sheet4.addCell(new jxl.write.Label(4, 9,export.getDestination()));
			sheet4.addCell(new jxl.write.Label(7, 9,export.getDestinationPort()));
			sheet4.addCell(new jxl.write.Label(4, 11,export.getTradeType()));
			
			sheet4.addCell(new jxl.write.Label(1, 13,export.getContractNo(),leftBorder));
			sheet4.addCell(new jxl.write.Number(4, 13,totalPackage,leftBorder));
			sheet4.addCell(new jxl.write.Number(8, 13,totalGrossWeight));
			sheet4.addCell(new jxl.write.Number(11, 13,totalNetWeight));
			
			sheet4.addCell(new jxl.write.Label(1, 17,export.getMemos(),leftAndRightBorder));
			sheet4.addCell(new jxl.write.Label(1, 61,export.getOperator().getEmployee().getName()
					+" "+export.getOperator().getEmployee().getMobile(),leftAndRightBorder));
			
			i = 0;
			for(Item item:items){
				double total = item.getPrice()*item.getQuantity();
				sheet4.addCell(new Label(3, 20+i,item.getName())); 
				sheet4.addCell(new Label(3, 21+i,item.getModel())); 

				sheet4.addCell(new jxl.write.Number(6, 20+i,item.getQuantity()));
				sheet4.addCell(new jxl.write.Label(7, 20+i,item.getUnit()));
				sheet4.addCell(new jxl.write.Label(10, 20+i,export.getDestination()));
				sheet4.addCell(new jxl.write.Number(11, 20+i,item.getPrice()));
				sheet4.addCell(new jxl.write.Number(12, 20+i,total,rightBorder));
				i += 2;
			}
			
			//==================== sheet 5 =========================
			WritableSheet sheet5 = copy.getSheet(4);
			sheet5.addCell(new jxl.write.Label(2, 10,itemDesc));
			sheet5.addCell(new jxl.write.Label(2, 12,export.getItemsCity()));
			sheet5.addCell(new jxl.write.Label(2, 14,export.getPackageAndModel()));
			sheet5.addCell(new jxl.write.Number(2, 16,totalPackage));
			sheet5.addCell(new jxl.write.Number(2, 18,totalGrossWeight));
			sheet5.addCell(new jxl.write.Number(2, 20,totalMoney));
			sheet5.addCell(new jxl.write.Label(3, 16,"箱"));
			sheet5.addCell(new jxl.write.Label(3, 18,items.get(0).getUnit()));
			sheet5.addCell(new jxl.write.Label(3, 20,export.getCurrency().getText()));
			
			sheet5.addCell(new jxl.write.Label(2, 22,export.getStorageVehicle()));
			sheet5.addCell(new jxl.write.Label(2, 24,export.getLoadingPort()));
			sheet5.addCell(new jxl.write.Label(2, 26,export.getStoragePeriod()));
			sheet5.addCell(new jxl.write.Label(2, 32,Constants.COMPANY_ADDR));
			sheet5.addCell(new jxl.write.Label(2, 34,Constants.COMPANY_TEL));
			sheet5.addCell(new jxl.write.Label(2, 36,Constants.COMPANY_FAX));
			sheet5.addCell(new jxl.write.Label(6, 36,Tools.getDateString()));
			
			
			copy.write(); 
			//copy.close();
			byte[] bytes = FileUtil.readAsByteArray(tempFile);
			return renderFile(bytes, export.getCode()+"-出口单证.xls","application/vnd.ms-excel");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}*/
	
	public static void main(String[] args) throws Exception {

	}
	
	
	
	protected void setEntity(Export e) throws ParseException{
		
	}
	
	protected void setEntity(Export export,
			List<BusinessInstance> busis) throws ParseException{
		boolean create = (export.getId() == 0) ;
		
		if(create){
			export.setCreateDate(new Date());
			export.setStatus(Export.Status.CREATED);
			export.setSales(authedUser);
		}
		
		export.setBuyer(AppHelper.toBuyer(p("buyer")));
		export.setCustomer(AppHelper.toCustomer(p("customer")));
		export.setLoadingCity(p("loadingCity"));
		export.setLoadingPort(p("loadingPort"));
		export.setDestination(p("destination"));
		export.setDestinationPort(p("destinationPort"));
		export.setCurrency(AppHelper.toDict(p("currency")));
		export.setReportPortDate(pDate("reportPortDate"));
		export.setCabNo(p("cabNo"));
		export.setCabType(p("cabType"));
		export.setItemDesc(p("itemDesc"));
		export.setItemQuantity(p("itemQuantity"));
		
		for(String code : codes){
			String checkedValue = p("checkedBusiness_"+code);
			if(checkedValue.equals("on")){
				BusinessInstance instance = new BusinessInstance();
				instance.setBusiness(businessService.getBusinessByCoce(code));
				instance.setCost(pDouble("cost_"+code));
				instance.setSalesPrice(pDouble("salesPrice_"+code));
				busis.add(instance);
			}
		}
	}
	
	protected JSONObject toJsonObject(Export e) throws ParseException{
		AppHelper record = new AppHelper();
		record.put("id", e.getId());
		record.put("code", e.getCode());
		record.put("reportPortDate", e.getReportPortDate());
		record.put("createDate", e.getCreateDate());
		record.put("customer", e.getCustomer());
		record.put("buyer", e.getBuyer());
		record.put("loadingCity", e.getLoadingCity());
		record.put("loadingPort", e.getLoadingPort());
		record.put("loadingPortCopy", e.getLoadingPort());
		record.put("destination", e.getDestination());
		record.put("destinationPort", e.getDestinationPort());
		record.put("destinationPortCopy", e.getDestinationPort());
		record.put("currency", e.getCurrency());
		record.put("cabNo", e.getCabNo());
		record.put("cabType", e.getCabType());
		record.put("verificationCompany", e.getVerificationCompany());
		record.put("verificationFormNo", e.getVerificationFormNo());
		
		record.put("customsBroker", e.getCustomsBroker());
		record.put("loadingCity", e.getLoadingCity());//出口地
		record.put("loadingPort", e.getLoadingPort());//装运口岸/出口口岸
		record.put("destination", e.getDestination());//目的地
		record.put("destinationPort", e.getDestinationPort());//目的港
		record.put("mark", e.getMark());//唛头
		record.put("contractNo", e.getContractNo());//合同号
		record.put("contractDate", e.getContractDate());//合同日期
		record.put("invoiceNo", e.getInvoiceNo());//发票号
		record.put("invoiceDate", e.getInvoiceDate());//发票日期
		record.put("tradeType", e.getTradeType());//成交方式
		record.put("signCity", e.getSignCity());
		record.put("payCondition", e.getPayCondition());
		record.put("memos", e.getMemos());
		record.put("taxMemos", e.getTaxMemos());
		record.put("itemsCity", e.getItemsCity());
		record.put("storagePeriod", e.getStoragePeriod());
		record.put("storageVehicle", e.getStorageVehicle());
		record.put("packageAndModel", e.getPackageAndModel());
		record.put("grossWeight", e.getGrossWeight());//毛重KG
		record.put("netWeight", e.getNetWeight());//净重KG
		
		//for 产地证
		record.put("producerNo", e.getProducerNo());//产地证号
		record.put("packageNumber", e.getPackageNumber());//箱数
		record.put("produceDate", e.getProduceDate());//产地日期
		
		//for inspection 商检
		record.put("inspection", e.getInspection());//商检行
		record.put("exportPort", e.getExportPort());//出口口岸
		record.put("inspectionTransType", e.getInspectionTransType());//商检运输方式
		
		//for transport
		record.put("truckCompany", e.getTruckCompany());//拖车公司
		record.put("transportType", e.getTransportType());//运输方式
		record.put("loadingFactory", e.getLoadingFactory());//装载工厂
		record.put("loadingFactoryAddr", e.getLoadingFactoryAddr());//装载工厂地址
		record.put("deliverPort", e.getDeliverPort());//发货港口
		record.put("driver", e.getDriver());//司机
		record.put("driverPhone", e.getDriverPhone());//司机电话
		record.put("truckLicense", e.getTruckLicense());//车牌号
		
		//for 国际运输
		record.put("shipType", e.getShipType());//国际运输方式,海运,空运 
		record.put("volume", e.getVolume());//体积
		record.put("weight", e.getWeight());//重量
		record.put("ladingBillNo", e.getLadingBillNo());//提单号
		record.put("shipCompany", e.getShipCompany());
		record.put("airCompany", e.getAirCompany());
		
		List<BusinessInstance> busiList = e.getBusinessInstances();
		for(BusinessInstance b : busiList){
			record.put("checkedBusiness_"+b.getBusiness().getCode(), "on");
			record.put("salesPrice_"+b.getBusiness().getCode(), b.getSalesPrice());
			record.put("cost_"+b.getBusiness().getCode(), b.getCost());
		}
		
		List<Item> items = e.getItems();
		JSONArray itemsArray = new JSONArray();
		for(Item i:items){
			JSONObject itemJson = new JSONObject();
			itemJson.put("itemId", i.getId());
			itemJson.put("name", i.getName());
			itemJson.put("model", i.getModel());
			itemJson.put("price", i.getPrice());
			itemJson.put("quantity", i.getQuantity());
			itemJson.put("unit", i.getUnit());
			itemJson.put("packageQuantity", i.getPackageQuantity());
			itemJson.put("unitQuantity", i.getUnitQuantity());
			itemJson.put("unitForQuantity", i.getUnitForQuantity());
			itemJson.put("grossWeight", i.getGrossWeight());
			itemJson.put("netWeight", i.getNetWeight());
			itemJson.put("unitForWeight", i.getUnitForWeight());
			itemsArray.add(itemJson);
		}
		JSONObject itemsRoot = new JSONObject();
		itemsRoot.put("root", itemsArray);
		record.put("itemsData", itemsRoot);
		
		record.put("itemDesc", e.getItemDesc());
		record.put("itemQuantity", e.getItemQuantity());
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

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public ItemService getItemService() {
		return itemService;
	}

	public void setItemIds(long[] itemIds) {
		this.itemIds = itemIds;
	}

	public long[] getItemIds() {
		return itemIds;
	}

	public void setDeletedItemIds(long[] deletedItemIds) {
		this.deletedItemIds = deletedItemIds;
	}

	public long[] getDeletedItemIds() {
		return deletedItemIds;
	}

	public void setNetWeights(double[] netWeights) {
		this.netWeights = netWeights;
	}

	public double[] getNetWeights() {
		return netWeights;
	}

	public void setGrossWeights(double[] grossWeights) {
		this.grossWeights = grossWeights;
	}

	public double[] getGrossWeights() {
		return grossWeights;
	}

	public void setUnitQuantitys(double[] unitQuantitys) {
		this.unitQuantitys = unitQuantitys;
	}

	public double[] getUnitQuantitys() {
		return unitQuantitys;
	}

	public void setPackageQuantitys(double[] packageQuantitys) {
		this.packageQuantitys = packageQuantitys;
	}

	public double[] getPackageQuantitys() {
		return packageQuantitys;
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

	public void setBusinessInstanceIds(long[] businessInstanceIds) {
		this.businessInstanceIds = businessInstanceIds;
	}

	public long[] getBusinessInstanceIds() {
		return businessInstanceIds;
	}

	public void setUnitForQuantitys(String[] unitForQuantitys) {
		this.unitForQuantitys = unitForQuantitys;
	}

	public String[] getUnitForQuantitys() {
		return unitForQuantitys;
	}

	public void setUnitForWeights(String[] unitForWeights) {
		this.unitForWeights = unitForWeights;
	}

	public String[] getUnitForWeights() {
		return unitForWeights;
	}

}
