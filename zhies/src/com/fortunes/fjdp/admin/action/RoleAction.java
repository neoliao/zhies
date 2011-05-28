package com.fortunes.fjdp.admin.action;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Privilege;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.PrivilegeService;
import com.fortunes.fjdp.admin.service.RoleService;
import com.fortunes.fjdp.admin.service.UserService;

import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.sf.json.JSONArray;
import net.sf.json.JSONFunction;
import net.sf.json.JSONObject;

@Component @Scope("prototype")
public class RoleAction extends GenericAction<Role> {
	
	@Resource private RoleService roleService;
	@Resource private UserService userService;
	@Resource private PrivilegeService privilegeService;
	
	private String[] checkedId;
	
	
	protected void setEntity(Role role){
		role.setName(p("nameCn"));
		role.setDescription(p("description"));
	}
	
	protected JSONObject toJsonObject(Role role){
		AdminHelper record = new AdminHelper();
		record.put("id", role.getDbId());
		record.put("nameCn", role.getName());
		record.put("description", role.getDescription());
		return record.getJsonObject();
	}

	public String getRolesByUser() throws Exception {
		User user = userService.get(getId());
		List<Role> roleList = roleService.findAll();
		JSONArray ja = new JSONArray();
		for(Role role:roleList){
			JSONObject record = new JSONObject();
			record.put("id", role.getDbId());
			record.put("text", role.getName());
			record.put("checked",user == null?false : (user.getRole()==null?false:(user.getRole().getId()==role.getId())));
			ja.add(record);
		}
		jo.put("data", ja);
		return render(jo);
	}
	
	public String listPrivileges() throws Exception {
		Privilege rootPrivilege = privilegeService.getRoot();
		Role role = roleService.get(id);
		JSONArray ja = walkPrivilegeTree(rootPrivilege,role);
		return render(ja);
	}
	
	public String updatePrivileges() throws Exception{
		roleService.updatePrivileges(id, getCheckedId());
		return render(jo);
	}
	
	private JSONArray walkPrivilegeTree(Privilege privilege,Role role){
		JSONArray ja = new JSONArray();
		List<Privilege> ps = privilege.getChildren();
		for (Privilege p : ps) {
			JSONObject jo = new JSONObject();
			jo.put("id", p.getCode());  
			jo.put("checked", role.getPrivileges().contains(p));
			jo.put("text", p.getText());
			
			if (p.isLeaf()) {
				jo.put("leaf", true);
				if(p.getCode().endsWith("view")){
					jo.put("qtip", "当有其它权限被选中时,浏览权限必须是选中状态");
					JSONObject listenerJo = new JSONObject();
					JSONFunction function = new JSONFunction(new String[]{"node","checked"},
							"if(checked == false){"+
							"  node.parentNode.ui.toggleCheck(false);"+
							"}"
					);
					listenerJo.put("checkchange", function);
					jo.put("listeners", listenerJo);
				}else{
					JSONObject listenerJo = new JSONObject();
					JSONFunction function = new JSONFunction(new String[]{"node","checked"},
							"if(checked == true){"+
							"  node.parentNode.firstChild.ui.toggleCheck(true);"+
							"}"
					);
					listenerJo.put("checkchange", function);
					jo.put("listeners", listenerJo);
				}
			} else {
				jo.put("expanded", true);
				jo.put("children",walkPrivilegeTree(p,role));				
			}
			ja.add(jo);
		}
		return ja;
	}
	
	public String getRoles()throws Exception{
		List<Role> roles = roleService.findAll();
		JSONArray ja = new JSONArray();
		JSONObject record = null;
		for(Role role:roles){
			record = new JSONObject();
			record.put("id", role.getDbId());
			record.put("text", role.getName());
			ja.add(record);
		}
		jo.put("data", ja);
		return render(jo);
	}
	
	//================== setter and getter ===================
	
	@Override
	public GenericService<Role> getDefService() {
		return roleService;
	}
	
	public String[] getCheckedId() {
		return checkedId;
	}

	public void setCheckedId(String[] checkedId) {
		this.checkedId = checkedId;
	}

	

}
