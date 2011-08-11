package com.fortunes.zhies.action;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import net.fortunes.core.ListData;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.format.CellNumberFormatter;
import org.apache.poi.ss.util.CellRangeAddress;
import org.aspectj.util.FileUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.Constants;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.UserService;
import com.fortunes.zhies.model.Business;
import com.fortunes.zhies.model.BusinessInstance;
import com.fortunes.zhies.model.Customer;
import com.fortunes.zhies.model.Export;
import com.fortunes.zhies.model.Item;
import com.fortunes.zhies.model.Trade;
import com.fortunes.zhies.service.BusinessService;
import com.fortunes.zhies.service.CustomerService;
import com.fortunes.zhies.service.TradeService;

@Component @Scope("prototype")
public class TradeAction extends GenericAction<Trade> {
	
	private TradeService tradeService;
	private UserService userService;
	private CustomerService customerService;
	private BusinessService businessService;
	
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
		record.put("reportPortDate", e.getReportPortDate());
		record.put("buyer", e.getBuyer());
		record.put("buyerName", e.getBuyer().getName());
		record.put("customer", e.getCustomer());
		record.put("customerName", e.getCustomer().getName());
		record.put("sales", e.getSales());
		record.put("salesName", e.getSales().getDisplayName());
		record.put("operator", e.getOperator());
		record.put("operatorName", e.getOperator().getName());
		record.put("loadingPort", e.getLoadingPort());
		record.put("cabNo", e.getLoadingPort());
		record.put("soNo", e.getLoadingPort());
		
		
		record.put("verificationCompany",e.getVerificationCompany());
		record.put("verificationFormNo", e.getVerificationFormNo());
		
		List<BusinessInstance> busiList = e.getBusinessInstances();
		for(BusinessInstance b : busiList){
			record.put("cost_"+b.getBusiness().getCode(), b.getSalesPrice());
		}
		
		record.put("totalSalesPrice", e.getTotalSalesPrice());
		record.put("totalActuralCost", e.getTotalActualCost());
		record.put("totalCost", e.getTotalCost());
		record.put("totalPackage", e.getTotalPackage());
		
		record.put("profit", e.getTotalSalesPrice()-e.getTotalActualCost());
		record.put("commission", e.getTotalSalesPrice()-e.getTotalCost());
		
		record.put("itemDesc", e.getItemDesc());
		record.put("itemQuantity", e.getItemQuantity());
		
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
		
		if(p("reportDateType").equals("YEARMONTH")){
			queryMap.put("monthTag", p("year")+"-"+p("month"));
		}else{
			queryMap.put("yearTag", p("yearNum"));
		}
		return super.list();
	}
	public String exportReport() throws Exception{
		queryMap.put("tradeType", p("tradeType"));
		queryMap.put("customerId", p("customer"));
		
		if(p("reportDateType").equals("YEARMONTH")){
			queryMap.put("monthTag", p("year")+"-"+p("month"));
		}else{
			queryMap.put("yearTag", p("yearNum"));
		}
		ListData<Trade> listData = getDefService().getListData(null, queryMap, 0, 0);
		File tempFile = new File("C:/reportxls");
		FileOutputStream temp = new FileOutputStream(tempFile);
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		
		String title = Constants.COMPANY_NAME+p("year")+"年"+p("month")+"月业绩报表";
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 16));
		
		HSSFCellStyle bigStyle = workbook.createCellStyle();
		HSSFFont big = workbook.createFont();
		big.setFontHeightInPoints((short)24);
		bigStyle.setFont(big);
		bigStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		HSSFCell titleCell = sheet.createRow(0).createCell(0);
		titleCell.setCellValue(title);
		titleCell.setCellStyle(bigStyle);
		
		
		int k = 0;
		HSSFRow titleRow = sheet.createRow(1);
		setCell(k++, titleRow,"业务编号");
		setCell(k++, titleRow, "创建时间");
		setCell(k++, titleRow, "报关日期");
		setCell(k++, titleRow, "买方");
		setCell(k++, titleRow, "客户");
		setCell(k++, titleRow, "货物");
		setCell(k++, titleRow, "货物数量");
		setCell(k++, titleRow, "业务员");
		setCell(k++, titleRow, "操作员");
		setCell(k++, titleRow, "港口");
		
		setCell(k++, titleRow, "核销单主");
		setCell(k++, titleRow, "核销单号");
		
		setCell(k++, titleRow,  "销售额");
		setCell(k++, titleRow,  "实际成本");
		setCell(k++, titleRow,  "成本");
		setCell(k++, titleRow,  "毛利");
		setCell(k++, titleRow,  "销售完成额");

		
		int i = 2;
		for(Trade e : listData.getList()){
			int j = 0;
			HSSFRow row = sheet.createRow(i);
			setCell(j++, row,e.getCode());
			setCell(j++, row, Tools.date2String(e.getCreateDate()));
			setCell(j++, row, Tools.date2String(((Export)e).getReportPortDate()));
			setCell(j++, row, e.getBuyer().getName());
			setCell(j++, row, e.getCustomer().getName());
			setCell(j++, row, e.getItemDesc());
			setCell(j++, row, e.getItemQuantity());
			setCell(j++, row, e.getSales().getDisplayName());
			setCell(j++, row, e.getOperator().getName());
			setCell(j++, row, ((Export)e).getLoadingPort());
			
			setCell(j++, row, ((Export)e).getVerificationCompany().getName());
			setCell(j++, row, ((Export)e).getVerificationFormNo());
			
			setCell(j++, row,  e.getTotalSalesPrice());
			setCell(j++, row,  e.getTotalActualCost());
			setCell(j++, row,  e.getTotalCost());
			setCell(j++, row, e.getTotalSalesPrice()-e.getTotalActualCost());
			setCell(j++, row,  e.getTotalSalesPrice()-e.getTotalCost());
			i++;
		}
		workbook.write(temp);
		byte[] bytes = FileUtil.readAsByteArray(tempFile);
		return renderFile(bytes, title+".xls");
		
	}
	

	
	
	public String statements() throws Exception{
		queryMap.put("customerId", p("customer"));
		
		queryMap.put("monthTag", p("year")+"-"+p("month"));
		return super.list();
	}
	
	public String exportStatements() throws Exception{
		Customer customer = customerService.get(p("customer"));
		
		queryMap.put("customerId", p("customer"));
		
		queryMap.put("monthTag", p("year")+"-"+p("month"));
		ListData<Trade> listData = getDefService().getListData(null, queryMap, 0, 0);
		File tempFile = new File("C:/statementsxls");
		FileOutputStream temp = new FileOutputStream(tempFile);
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		
		String title = p("year")+"年"+p("month")+"月"+customer.getName()+"对账单";
		
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
		
		HSSFCellStyle bigStyle = workbook.createCellStyle();
		HSSFFont big = workbook.createFont();
		big.setFontHeightInPoints((short)20);
		bigStyle.setFont(big);
		bigStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		HSSFCell titleCell = sheet.createRow(0).createCell(0);
		titleCell.setCellValue(title);
		titleCell.setCellStyle(bigStyle);
		
		
		int k = 0;
		HSSFRow titleRow = sheet.createRow(1);
		//setCell(k++, titleRow, "客户");
		setCell(k++, titleRow, "买方");
		setCell(k++, titleRow, "货物");
		setCell(k++, titleRow, "港口");
		setCell(k++, titleRow, "货柜名称");
		setCell(k++, titleRow, "SO号码");
		setCell(k++, titleRow, "箱数");

		setCell(k++, titleRow,  "报关服务费");
		setCell(k++, titleRow,  "单证制作费");
		setCell(k++, titleRow,  "产地证制作费");
		setCell(k++, titleRow,  "商检费");
		setCell(k++, titleRow,  "拖车运输费");
		setCell(k++, titleRow,  "国际运输费");
		setCell(k++, titleRow,  "港建费");
		setCell(k++, titleRow,  "其它费用");
		setCell(k++, titleRow,  "总费用");

		int i = 2;
		double totalMonth = 0.0;
		for(Trade e : listData.getList()){
			int j = 0;
			HSSFRow row = sheet.createRow(i);
			setCell(j++, row, e.getBuyer().getName());
			setCell(j++, row, e.getItemDesc());
			setCell(j++, row, e.getLoadingPort());
			setCell(j++, row, e.getCabNo());
			setCell(j++, row, e.getSoNo());
			setCell(j++, row, e.getTotalPackage());
			setCell(j++, row,  getBussinessPrice("A",e.getBusinessInstances()));
			setCell(j++, row,  getBussinessPrice("B",e.getBusinessInstances()));
			setCell(j++, row,  getBussinessPrice("C",e.getBusinessInstances()));
			setCell(j++, row,  getBussinessPrice("D",e.getBusinessInstances()));
			setCell(j++, row,  getBussinessPrice("E",e.getBusinessInstances()));
			setCell(j++, row,  getBussinessPrice("F",e.getBusinessInstances()));
			setCell(j++, row,  getBussinessPrice("G",e.getBusinessInstances()));
			setCell(j++, row,  getBussinessPrice("Z",e.getBusinessInstances()));
			totalMonth += e.getTotalSalesPrice();
			setCell(j++, row,  e.getTotalSalesPrice());

			i++;
		}
		HSSFRow row = sheet.createRow(i);
		setCell(10, row,  "总计");
		setCell(11, row,  totalMonth);
		workbook.write(temp);
		byte[] bytes = FileUtil.readAsByteArray(tempFile);
		return renderFile(bytes, title+".xls");
	}
	
	private double getBussinessPrice(String code,
			List<BusinessInstance> businessInstances) {
		for(BusinessInstance bi : businessInstances){
			if(bi.getBusiness().getCode().equals(code)){
				return bi.getSalesPrice();
			}
		}
		return 0;
	}

	public String salesPieReport() throws Exception{
		String dateTag = "";
		if(p("reportDateType").equals("YEARMONTH")){
			dateTag = p("year")+"-"+p("month");
		}else{
			dateTag = p("yearNum");
		}
		
		List<Object[]> list = tradeService.queryProfitSummary(dateTag);
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
		
		String dateTag = "";
		if(p("reportDateType").equals("YEARMONTH")){
			dateTag = p("year")+"-"+p("month");
		}else{
			dateTag = p("yearNum");
		}
		
		List<Object[]> list = tradeService.queryProfitSummaryForCustomer(dateTag);
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
	
	private void setCell(int col,HSSFRow row,String s){
		HSSFCell cell = row.createCell(col);
		cell.setCellValue(s);
	}
	

	
	private void setCell(int col,HSSFRow row,double s){
		HSSFCell cell = row.createCell(col);
		CellFormat format = CellFormat.getInstance("#.##");
		format.apply(cell);
		cell.setCellValue(s);
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

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}

	public BusinessService getBusinessService() {
		return businessService;
	}

	

}
