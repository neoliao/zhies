package com.fortunes.zhies.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.MoneyUtil;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
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
import com.fortunes.zhies.model.Import;
import com.fortunes.zhies.model.Item;
import com.fortunes.zhies.model.Trade;
import com.fortunes.zhies.service.BusinessService;
import com.fortunes.zhies.service.ImportService;
import com.fortunes.zhies.service.ItemService;


@Component @Scope("prototype")
public class ImportAction extends GenericAction<Import> {
	
	private ImportService importService;
	private BusinessService businessService;
	private UserService userService;
	private ItemService itemService;
	
	public static final String TEMPLATE_PATH = "/import_template.xls";
	
	
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
			"A","B","D","E","F","G","Z"
	};
	
	
	@Override
	public String list() throws Exception {
		queryMap.put("userId", authedUser.getId());
		return super.list();
	}
	
	public String getImports() throws Exception {
		List<Import> list = getDefService().findAll();
		JSONArray ja = new JSONArray();
		for(Import c:list){
			
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
		Import imports = new Import();
		List<BusinessInstance> busis = new ArrayList<BusinessInstance>();
		setEntity(imports,busis);
		
		importService.createImportInstance(imports,busis);
		
		setJsonMessage(true, imports.toString().equals("")?
				"新增了一条记录!" : "新增了("+imports+")的记录");
		return render(jo);
	}
	
	@Override
	public String update() throws Exception {
		Import imports = importService.get(id);
		List<BusinessInstance> busis = new ArrayList<BusinessInstance>();
		
		setEntity(imports, busis);
		
		importService.updateImportInstance(imports,busis);
		setJsonMessage(true, imports.toString().equals("")?
				"更新了一条记录!" : "更新了("+imports+")的记录");
		return render(jo);
	}
	
	
	public String assignOperator() throws Exception {
		Import imports = importService.get(id);
		imports.setStatus(Import.Status.ASSIGNED);
		imports.setOperator(AppHelper.toUser(p("operator")));
		importService.update(imports);
		setJsonMessage(true, "业务操作员已经分配");
		return render(jo);
	}
	
	public String loadOperator() throws Exception {
		return super.edit();
		
	}
	
	public String firstLoadOperator() throws Exception {
		Import e = importService.get(id);
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
		Import imports = importService.get(id);
		imports.setStatus(Import.Status.OPERATOR_SAVED);
		
		//代交关税
		imports.setValueAddedTax(pDouble("valueAddedTax"));
		imports.setConsumeTax(pDouble("consumeTax"));
		imports.setDelayFee(pDouble("delayFee"));
		
		//for CustomsBroker 报关
		imports.setBuyer(AppHelper.toBuyer(p("buyer")));
		imports.setCustomsBroker(AppHelper.toCustomsBroker(p("customsBroker")));//报关行
		
		imports.setLoadingCity(p("loadingCity"));//装运口岸
		imports.setLoadingPort(p("loadingPort"));
		imports.setDestination(p("destination"));//目的地
		imports.setDestinationPort(p("destinationPort"));
		imports.setCurrency(AppHelper.toDict(p("currency")));
		imports.setCabNo(p("cabNo"));
		imports.setCabType(p("cabType"));
		imports.setSoNo(p("soNo"));
		imports.setVerificationCompany(AppHelper.toVerificationCompany(p("verificationCompany")));
		imports.setVerificationFormNo(p("verificationFormNo"));
		
		imports.setMark(p("mark"));//唛头
		imports.setContractNo(p("contractNo"));//合同号
		imports.setContractDate(pDate("contractDate"));//合同日期
		imports.setInvoiceNo(p("invoiceNo"));//发票号
		imports.setInvoiceDate(pDate("invoiceDate"));//发票日期
		imports.setTradeType(p("tradeType"));//成交方式
		imports.setSignCity(p("signCity"));
		imports.setPayCondition(p("payCondition"));
		imports.setMemos(p("memos"));
		imports.setTaxMemos(p("taxMemos"));
		imports.setItemsCity(p("itemsCity"));
		
		imports.setGrossWeight(pDouble("grossWeight"));//毛重KG
		imports.setNetWeight(pDouble("netWeight"));//净重KG
		
		imports.setStoragePeriod(p("storagePeriod"));
		imports.setStorageVehicle(p("storageVehicle"));
		imports.setPackageAndModel(p("packageAndModel"));
		
		//for 产地证
		imports.setProducerNo(p("producerNo"));//产地证号
		imports.setPackageNumber(pInt("packageNumber"));//箱数
		imports.setProduceDate(pDate("produceDate"));//产地日期
		
		//for inspection 商检
		imports.setInspection(AppHelper.toInspection(p("inspection")));//商检行
		imports.setExportPort(p("exportPort"));//出口口岸
		imports.setInspectionTransType(p("inspectionTransType"));//商检运输方式
		
		//for transport
		imports.setTruckCompany(AppHelper.toTruckCompany(p("truckCompany")));//拖车公司
		imports.setTransportType(AppHelper.toDict(p("transportType")));//运输方式
		imports.setLoadingFactory(p("loadingFactory"));//装载工厂
		imports.setLoadingFactoryAddr(p("loadingFactoryAddr"));//装载工厂地址
		imports.setDeliverPort(p("deliverPort"));//发货港口
		imports.setDriver(p("driver"));//司机
		imports.setDriverPhone(p("driverPhone"));//司机电话
		imports.setTruckLicense(p("truckLicense"));//车牌号
		
		//for 国际运输
		if(p("shipType").equals("ship")){
			imports.setShipCompany(AppHelper.toShipCompany(p("shipCompany")));
		}else{
			imports.setAirCompany(AppHelper.toAirCompany(p("airCompany")));
		}
		imports.setVolume(pDouble("volume"));//体积
		imports.setWeight(pDouble("weight"));//重量
		imports.setLadingBillNo(p("ladingBillNo"));//提单号
		
		
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
		
		importService.updateOperator(imports,items,deletedItemIds);
		setJsonMessage(true, "操作已经记录");
		return render(jo);
	}
	
	public String submitOperator() throws Exception {
		Import imports = importService.get(id);
		imports.setStatus(Import.Status.OPERATOR_SUBMITED);
		importService.update(imports);
		setJsonMessage(true, "操作纪录已经提交");
		return render(jo);
	}
	
	public String copy() throws Exception {
		Import toImport = importService.get(id);
		Import fromImport = importService.get(p("fromImport"));

		importService.copy(fromImport,toImport);
		
		setJsonMessage(true, "复制成功");
		return render(jo);
	}
	
	public String mustPay() throws Exception {
		Import imports = importService.get(id);
		List<BusinessInstance> instances = imports.getBusinessInstances();
		JSONArray busiInstJa = new JSONArray();
		for(BusinessInstance bi : instances){
			JSONObject busiInstJo = new JSONObject();
			Company company = null;
			//String name = "";
			if(bi.getBusiness().getCode().equals("A")){
				company = imports.getCustomsBroker();
			}else if(bi.getBusiness().getCode().equals("B")){
				company = imports.getVerificationCompany();
			}else if(bi.getBusiness().getCode().equals("C")){
				continue;
			}else if(bi.getBusiness().getCode().equals("D")){
				company = imports.getInspection();
			}else if(bi.getBusiness().getCode().equals("E")){
				company = imports.getTruckCompany();
			}else if(bi.getBusiness().getCode().equals("F")){
				if(imports.getShipCompany() != null){
					company = imports.getShipCompany();
				}else{
					company = imports.getAirCompany();
				}
			}else if(bi.getBusiness().getCode().equals("G")){
				company = imports.getCustomsBroker();
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
		Import imports = importService.get(id);
		List<BusinessInstance> instances = imports.getBusinessInstances();
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
		Import imports = importService.get(id);
		imports.setStatus(Import.Status.COST_CONFIRMED);
		
		List<Accounts> accountsList = new ArrayList<Accounts>();
		for(int i = 0 ;i < businessInstanceIds.length ; i++){
			Accounts pay = new Accounts();
			pay.setAccountsType(AccountsType.MUST_PAY);
			pay.setTrade((Trade)imports);
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
		gain.setTrade((Trade)imports);
		gain.setAmountInPlan(total);
		gain.setAmountDone(new Double(0));
		gain.setFinished(false);
		gain.setCompany(AppHelper.toCompany(imports.getCustomer().getId()+""));
		accountsList.add(gain);
		
		
		importService.confirmCost(imports,accountsList);
		setJsonMessage(true, "应收应付已经确认");
		return render(jo);
	}
	
	public String exportFilePoi() throws Exception{
		Import imports = importService.get(id);
		File tempFile = new File("C:/importtempxls");
		FileOutputStream temp = new FileOutputStream(tempFile);
		InputStream ins = ImportAction.class.getResourceAsStream(TEMPLATE_PATH);
		HSSFWorkbook copy = new HSSFWorkbook(ins);
		
		HSSFCellStyle bigStyle = copy.createCellStyle();
		HSSFFont big = copy.createFont();
		big.setFontHeightInPoints((short)20);
		bigStyle.setFont(big);
		bigStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		HSSFCellStyle topBorder = copy.createCellStyle();
		topBorder.setBorderTop(CellStyle.BORDER_THIN);
		
		HSSFCellStyle bottomBorder = copy.createCellStyle();
		bottomBorder.setBorderBottom(CellStyle.BORDER_THIN);
		
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
		setCell(sheet,0, 0,imports.getBuyer().getName(),bigStyle);
		setCell(sheet,2, 1,imports.getBuyer().getAddress());
		setCell(sheet,2, 2,imports.getBuyer().getTel(),bottomBorder);
		setCell(sheet,8, 2,imports.getBuyer().getFax(),bottomBorder);
		setCell(sheet,2, 3,Constants.COMPANY_NAME_EN);
		setCell(sheet,2, 4,Constants.COMPANY_ADDR_EN);
		setCell(sheet,2, 5,Constants.COMPANY_TEL);
		setCell(sheet,8, 5,Constants.COMPANY_FAX);
		setCell(sheet,2, 6,AppHelper.toDateString(imports.getContractDate()));
		setCell(sheet,8, 6,imports.getContractNo());
		

		List<Item> items = imports.getItems();
		double totalMoney  = 0.0;//总金额
		double totalQuantity  = 0;//总合同数量
		int totalUnitQuantity  = 0;//总个数
		int totalPackage  = 0;//总箱数
		double totalGrossWeight  = 0.0;//总毛重
		double totalNetWeight  = 0.0;//总净重
		String itemDesc = imports.getItemDesc();
		int i = 0;
		for(Item item:items){
			double total = item.getPrice()*item.getQuantity();
			totalMoney  += total;
			totalQuantity  += item.getQuantity();
			totalUnitQuantity += item.getUnitQuantity();
			totalPackage  += item.getPackageQuantity();
			totalGrossWeight  += item.getGrossWeight();
			totalNetWeight  += item.getNetWeight();
			setCell(sheet,1, 9+i,item.getName(),leftBorder); 
			setCell(sheet,4, 9+i,item.getQuantity(),leftBorder);
			setCell(sheet,5, 9+i,item.getUnitForQuantity(),rightBorder);
			setCell(sheet,6, 9+i,item.getGrossWeight());
			setCell(sheet,7, 9+i,item.getUnitForWeight());
			setCell(sheet,8, 9+i,item.getNetWeight());
			setCell(sheet,9, 9+i,item.getUnitForWeight());
			i++;
		}
		setCell(sheet,6, 9+i,totalGrossWeight);
		setCell(sheet,7, 9+i,items.get(0).getUnitForWeight());
		setCell(sheet,8, 9+i,totalNetWeight);
		setCell(sheet,9, 9+i,items.get(0).getUnitForWeight());
		
		
		//==================== sheet 2 =========================
		HSSFSheet sheet2 = copy.getSheetAt(1);
		setCell(sheet2,0, 0,imports.getBuyer().getName(),bigStyle);
		setCell(sheet2,2, 1,imports.getBuyer().getAddress());
		setCell(sheet2,2, 2,imports.getBuyer().getTel(),bottomBorder);
		setCell(sheet2,8, 2,imports.getBuyer().getFax(),bottomBorder);
		setCell(sheet2,2, 3,Constants.COMPANY_NAME_EN);
		setCell(sheet2,2, 4,Constants.COMPANY_ADDR_EN);
		setCell(sheet2,2, 5,Constants.COMPANY_TEL);
		setCell(sheet2,8, 5,Constants.COMPANY_FAX);
		setCell(sheet2,2, 6,AppHelper.toDateString(imports.getContractDate()));
		setCell(sheet2,8, 6,imports.getContractNo());
		
		
		i = 0;
		for(Item item:items){
			double total = item.getPrice()*item.getQuantity();
			setCell(sheet2,1, 9+i,item.getName(),leftBorder); 
			setCell(sheet2,4, 9+i,item.getQuantity(),leftBorder);
			setCell(sheet2,5, 9+i,item.getUnitForQuantity(),rightBorder);
			setCell(sheet2,6, 9+i,item.getPrice());
			setCell(sheet2,7, 9+i,imports.getCurrency().getText());
			setCell(sheet2,8, 9+i,total);
			setCell(sheet2,7, 9+i,imports.getCurrency().getText());
			i++;
		}

		setCell(sheet2,8, 9+i,totalMoney);
		setCell(sheet2,9, 9+i,imports.getCurrency().getText());
		
		
		/*//==================== sheet 3 =========================
		HSSFSheet sheet3 = copy.getSheetAt(2);
		setCell(sheet3,3, 8,Constants.COMPANY_NAME);
		setCell(sheet3,3, 9,Constants.COMPANY_NAME_EN);
		setCell(sheet3,3, 10,imports.getBuyer().getName());
		setCell(sheet3,9, 8,AppHelper.toDateString(imports.getInvoiceDate()));
		setCell(sheet3,9, 9,imports.getInvoiceNo());
		setCell(sheet3,9, 10,imports.getContractNo());
		setCell(sheet3,9, 11,imports.getPayCondition());
		
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
		setCell(sheet4,1, 3,imports.getLoadingPort(),leftBorder);
		setCell(sheet4,1, 5,Constants.COMPANY_NAME,leftBorder);
		setCell(sheet4,1, 7,Constants.COMPANY_CUSTOME_CODE,leftBorder);
		setCell(sheet4,11, 7,imports.getPayCondition());
		setCell(sheet4,11, 9,imports.getItemsCity());
		setCell(sheet4,4, 9,imports.getDestination());
		setCell(sheet4,7, 9,imports.getDestinationPort());
		setCell(sheet4,4, 11,imports.getTradeType());
		
		setCell(sheet4,1, 13,imports.getContractNo(),leftBorder);
		setCell(sheet4,4, 13,totalPackage,leftBorder);
		setCell(sheet4,8, 13,totalGrossWeight);
		setCell(sheet4,11, 13,totalNetWeight);
		
		setCell(sheet4,1, 17,imports.getMemos(),leftAndRightBorder);
		setCell(sheet4,1, 61,imports.getOperator().getEmployee().getName()
				+" "+imports.getOperator().getEmployee().getMobile(),leftAndRightBorder);
		
		i = 0;
		for(Item item:items){
			double total = item.getPrice()*item.getQuantity();
			setCell(sheet4,3, 20+i,item.getName()); 
			setCell(sheet4,3, 21+i,item.getModel()); 

			setCell(sheet4,6, 20+i,item.getQuantity());
			setCell(sheet4,7, 20+i,item.getUnit());
			setCell(sheet4,10, 20+i,imports.getDestination());
			setCell(sheet4,11, 20+i,item.getPrice());
			setCell(sheet4,12, 20+i,total,rightBorder);
			i += 2;
		}
		
		//==================== sheet 5 =========================
		HSSFSheet sheet5 = copy.getSheetAt(4);
		setCell(sheet5,2, 10,itemDesc);
		setCell(sheet5,2, 12,imports.getItemsCity());
		setCell(sheet5,2, 14,imports.getPackageAndModel());
		setCell(sheet5,2, 16,totalPackage);
		setCell(sheet5,2, 18,totalGrossWeight);
		setCell(sheet5,2, 20,totalMoney);
		setCell(sheet5,3, 16,"箱");
		setCell(sheet5,3, 18,items.get(0).getUnit());
		setCell(sheet5,3, 20,imports.getCurrency().getText());
		
		setCell(sheet5,2, 22,imports.getStorageVehicle());
		setCell(sheet5,2, 24,imports.getLoadingPort());
		setCell(sheet5,2, 26,imports.getStoragePeriod());
		setCell(sheet5,2, 32,Constants.COMPANY_ADDR);
		setCell(sheet5,2, 34,Constants.COMPANY_TEL);
		setCell(sheet5,2, 36,Constants.COMPANY_FAX);
		setCell(sheet5,6, 36,Tools.getDateString());*/
		
		copy.write(temp);		
		
		byte[] bytes = FileUtil.readAsByteArray(tempFile);
		return renderFile(bytes, imports.getCode()+"-出口单证.xls");
		
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
	
	
	
	protected void setEntity(Import e) throws ParseException{
		
	}
	
	protected void setEntity(Import imports,
			List<BusinessInstance> busis) throws ParseException{
		boolean create = (imports.getId() == 0) ;
		
		if(create){
			imports.setCreateDate(new Date());
			imports.setStatus(Import.Status.CREATED);
			imports.setSales(authedUser);
		}
		
		imports.setBuyer(AppHelper.toBuyer(p("buyer")));
		imports.setCustomer(AppHelper.toCustomer(p("customer")));
		imports.setSoNo(p("soNo"));
		imports.setLoadingCity(p("loadingCity"));
		imports.setLoadingPort(p("loadingPort"));
		imports.setDestination(p("destination"));
		imports.setDestinationPort(p("destinationPort"));
		imports.setCurrency(AppHelper.toDict(p("currency")));
		imports.setReportPortDate(pDate("reportPortDate"));
		imports.setCabNo(p("cabNo"));
		imports.setCabType(p("cabType"));
		imports.setItemDesc(p("itemDesc"));
		imports.setItemQuantity(p("itemQuantity"));
		
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
	
	protected JSONObject toJsonObject(Import e) throws ParseException{
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
		record.put("soNo", e.getSoNo());
		
		
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
		record.put("volume", e.getVolume());//体积
		record.put("weight", e.getWeight());//重量
		record.put("ladingBillNo", e.getLadingBillNo());//提单号
		record.put("shipCompany", e.getShipCompany());
		record.put("airCompany", e.getAirCompany());
		
		//代交关税
		record.put("valueAddedTax", e.getValueAddedTax());
		record.put("consumeTax", e.getConsumeTax());
		record.put("delayFee", e.getDelayFee());
		
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
	public GenericService<Import> getDefService() {
		return importService;
	}
	
	public void setImportService(ImportService importService) {
		this.importService = importService;
	}

	public ImportService getImportService() {
		return importService;
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
