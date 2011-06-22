package com.fortunes.zhies.action;

import java.text.ParseException;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fortunes.zhies.model.Invoice;
import com.fortunes.zhies.service.InvoiceService;

@Component @Scope("prototype")
public class InvoiceAction extends GenericAction<Invoice> {
	
	private InvoiceService invoiceService;
	
	protected void setEntity(Invoice e) throws ParseException{
		e.setTitle(p("title"));
		e.setAmount(Double.parseDouble(p("amount")));
		e.setBillNo(p("billNo"));
		e.setDate(AppHelper.toDate(p("date")));
	}
	
	protected JSONObject toJsonObject(Invoice e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("title", e.getTitle());
		record.put("amount", e.getAmount());
		record.put("billNo", e.getBillNo());
		record.put("date", e.getDate());
		return record.getJsonObject();
	}
	
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<Invoice> getDefService() {
		return invoiceService;
	}
	
	public void setInvoiceService(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}

	public InvoiceService getInvoiceService() {
		return invoiceService;
	}

}
