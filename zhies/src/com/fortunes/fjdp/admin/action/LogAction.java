package com.fortunes.fjdp.admin.action;

import java.text.ParseException;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Log;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.LogService;

import net.fortunes.core.ListData;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component @Scope("prototype")
public class LogAction extends GenericAction<Log> {
	
	@Resource private LogService logService;
	
//	@Override
//	public String list() throws Exception{
//		User user = (User)getSessionMap().get(AppHelper.AUTHED_USER);
//		Role role = user.getRole();
//			
//		queryMap.put("roles", role.getName());
//		queryMap.put("userDisplayName", user.getDisplayName());
//		
//		ListData<Log> listData = getDefService().getListData(query, queryMap, start, limit);
//		JSONArray ja = new JSONArray();
//		for(Log entity:listData.getList()){
//			ja.add(toJsonObject(entity));
//		}
//		jo.put(DATA_KEY, ja);
//		jo.put(TOTAL_COUNT_KEY, listData.getTotal());
//		return render(jo);  
//	}
	
	protected void setEntity(Log log) throws ParseException{

	}
	
	protected JSONObject toJsonObject(Log log) throws ParseException{	
		AdminHelper record = new AdminHelper();
		record.put("id", log.getId());		
		record.put("opType", log.getOpType());
		record.put("opUser", log.getOpUser());
		record.put("createTime", log.getCreateTime());
		record.put("contents", log.getContents());
		return record.getJsonObject();
	}
	
	//================== setter and getter ===================
	
	@Override
	public GenericService<Log> getDefService() {
		return logService;
	}
	
	
}
