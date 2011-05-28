package com.fortunes.fjdp.admin.action;

import java.util.List;

import javax.annotation.Resource;

import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.UserService;

@Component @Scope("prototype")
public class UserAction extends GenericAction<User> {
	
	@Resource private UserService userService;
	
	protected void setEntity(User user){
		user.setName(p("userName"));
		user.setDisplayName(p("userDisplayName"));
		user.setEmployee(AdminHelper.toEmployee(p("employee")));
		//修改时不改变密码
		if(StringUtils.isNotEmpty(p("password"))){
			user.setPassword(Tools.encodePassword(p("password")));
		}
		
		//user.getRoles().clear();
		if(StringUtils.isNotEmpty(p("role"))){
			user.setRole(AdminHelper.toRole(p("role")));
		}
	}
	
	protected JSONObject toJsonObject(User user){
		AdminHelper record = new AdminHelper();
		record.put("id", user.getId());
		record.put("userName", user.getName());
		record.put("userDisplayName", user.getDisplayName());
		record.put("password", user.getPassword());
		record.put("password2", user.getPassword());
		record.put("employee", user.getEmployee());
		record.put("locked", user.isLocked());
		record.put("lastLoginTime", user.getLoginSession().getLastLoginTime());
		record.put("logined", user.getLoginSession().isLogined());
		
		
		record.put("role", user.getRole());
		return record.getJsonObject();
	}
	
	public String getUsers() throws Exception{
		List<User> userList = getDefService().findAll();
		JSONArray ja = new JSONArray();
		for(User user:userList){
			JSONObject record = new JSONObject();
			record.put("id", user.getId());
			record.put("text", user.getEmployee().getName());
			ja.add(record);
		}
		jo.put("data", ja);
		return render(jo); 
	}
	
	public String lockUser() throws Exception{
		boolean flag = userService.lockOrUnlockUser(getId());
		jo.put("success", flag);
		return render(jo);
	}
	
	public String resetPassword() throws Exception{
		boolean flag = userService.resetPassword(getId(),Tools.encodePassword(p("password")));
		jo.put("success", flag);
		return render(jo);
	}
	//================== setter and getter ===================
	
	@Override
	public GenericService<User> getDefService() {
		return userService;
	}
	

	
}
