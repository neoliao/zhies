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
import com.fortunes.zhies.model.Export;
import com.fortunes.zhies.service.ExportService;

@Component @Scope("prototype")
public class ExportAction extends GenericAction<Export> {
	
	private ExportService exportService;
	
	protected void setEntity(Export e) throws ParseException{
		e.setCreateDate(p("createDate"));
	}
	
	protected JSONObject toJsonObject(Export e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("createDate", e.getCreateDate());
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

}
