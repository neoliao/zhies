package com.fortunes.fjdp.admin.action;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.service.DictService;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.OrganizationService;

import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.PinYin;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component @Scope("prototype")
public class OrganizationAction extends GenericAction<Organization> {
	
	@Resource private EmployeeService employeeService;
	@Resource private OrganizationService organizationService;
	@Resource private DictService dictService;

	private int[] checkedId;


	protected void setEntity(Organization o) throws ParseException{
		o.setName(p("name"));
		o.setCode(p("code"));
		o.setShortName(p("shortName"));
		o.setAddress(p("address"));
		o.setTel(p("tel"));
		o.setType(AdminHelper.toDict(p("type")));
		//新增时f
		if(o.getId() == 0){
			o.setParent(parentId.equals("0") ? 
					organizationService.getRoot() : organizationService.get(parentId));
		}
	}
	
	protected JSONObject toJsonObject(Organization e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("name", e.getName());
		record.put("code",e.getCode());
		record.put("shortName", e.getShortName());
		record.put("text", e.getShortName());
		record.put("fullName", e.getFullName());
		record.put("address", e.getAddress());
		record.put("tel", e.getTel());
		record.put("type", e.getType());
		record.put("iconCls", "organization");
		return record.getJsonObject();
	}

	public JSONArray walkTree(Organization organization) throws Exception{
		JSONArray ja = new JSONArray();
		if(organization != null){
			List<Organization> ds = organization.getChildren();		
			for(Organization d : ds){
				JSONObject jo = toJsonObject(d);
				if(d.isLeaf()){				
					jo.put("leaf", true);
				}else{
					//异步load
					//jo.put("children", walkTree(d));
				}
				ja.add(jo);
			}
		}
		return ja;
	}	
	
	//所有部门（用于下拉菜单）
	public String getOrganizations() throws Exception{  
		List<Organization> organizationList = organizationService.findAll();
		JSONArray ja = new JSONArray();
		JSONObject record = null;
		for(Organization organization:organizationList){
			if(organization.getParent() != null ){
				record = new JSONObject();
				record.put("id", organization.getId());
				record.put("text", organization.getName());
				record.put("code", organization.getCode());
				ja.add(record);
			}
		}
		jo.put("data", ja);
		
		return render(jo);  
	}
	
	//列出某个部门所拥有的所有员工
	public String ListEmployees() throws Exception{
		List<Employee> employees = organizationService.get(p("organizationId")).getEmployees();
		JSONArray ja = new JSONArray();
		for(Employee e : employees){
			JSONObject record = new JSONObject();
			record.put("id", e.getId());
			record.put("text", e.getName());
			record.put("qtip", "工号："+e.getCode());
			record.put("iconCls", 
					(e.getSex()!=null && e.getSex().getText().equals("女"))? "employee_female" : "employee");
			record.put("leaf", true);
			ja.add(record);
		}
		return render(ja);  
	}
	
	//列出某个部门未拥有的员工(根据关键字过滤)
	public String ListEmployeesUnassign() throws Exception{
		List<Employee> employeeList;
		if(OrganizationService.SINGLE_ORGANIZATION){
			employeeList = organizationService.getUnassignEmployees();
		}else{
			employeeList = organizationService.getUnassignEmployeesByOrganizationId(p("organizationId"));
		}
		JSONArray ja = new JSONArray();
		for(Employee employee:employeeList){
			String namePy = PinYin.toPinYinString(employee.getName());
			if(namePy.startsWith(getQuery().toUpperCase())
					|| employee.getName().startsWith(getQuery())){
				JSONObject record = new JSONObject();
				record.put("id", employee.getId());
				record.put("text", employee.getName());
				record.put("code", employee.getCode());
				record.put("pinyin", namePy);
				ja.add(record);
			}	
		}
		jo.put("data", ja);
		return render(jo);  
	}
	
	//加入一个员工到一个部门
	public String addEmployee() throws Exception{
		organizationService.addEmployee(p("organizationId"), p("employeeId"));
		
		//修改员工表里面的primaryOrganization属性
	    Employee entity=employeeService.get(p("employeeId"));
	    entity.setOrganization(AdminHelper.toOrganization(p("organizationId")));
		employeeService.update(entity);
		
		return render(jo);
	}
	
	//从一个部门移除某一个员工
	public String removeEmployee() throws Exception{
		organizationService.removeEmployee(p("organizationId"), p("employeeId"));
		
		//修改员工表里面的primaryOrganization属性设置为null
		Employee entity=employeeService.get(p("employeeId"));
	    entity.setOrganization(AdminHelper.toOrganization(null));
	    employeeService.update(entity);
		return render(jo);
	}
	
	//================== setter and getter ===================
	
	@Override
	public GenericService<Organization> getDefService() {
		return organizationService;
	}
	
	public int[] getCheckedId() {
		return checkedId;
	}

	public void setCheckedId(int[] checkedId) {
		this.checkedId = checkedId;
	}

	
	
}