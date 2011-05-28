package com.fortunes.fjdp.admin.action;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.PinYin;
import net.fortunes.util.Tools;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.AdminHelper;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.OrganizationService;

@Component @Scope("prototype")
public class EmployeeAction extends GenericAction<Employee> {
	
	private static final String PHOTO_DIR = "E:/app/photo/";
	@Resource private EmployeeService employeeService;
	private File photoFile;
	public static final String PHOTO_URL_PREFIX = "/employee/photo?photoId=";
	@Resource private OrganizationService organizationService;
	
	protected void setEntity(Employee employee) throws ParseException{
		employee.setCode(p("code"));
		employee.setName(p("name"));
		employee.setEmail(p("email"));
		employee.setPhone(p("phone"));
		employee.setMobile(p("mobile"));
		employee.setPhotoId(p("photoId"));
		employee.setSex(AdminHelper.toDict(p("sex")));
		employee.setStatus(AdminHelper.toDict(p("status")));
		employee.setPosition(AdminHelper.toDict(p("position")));
		employee.setEducation(AdminHelper.toDict(p("education")));
		employee.setPeopleType(AdminHelper.toDict(p("peopleType")));
		employee.setHireDate(AdminHelper.toDate(p("hireDate")));
		//employee.setPrimaryOrganization(AdminHelper.toOrganization(p("primaryOrganization")));
		//employee.getOrganizations().add(AdminHelper.toOrganization(p("primaryOrganization")));
		employee.setOrganization(AdminHelper.toOrganization(p("organization")));
		
	}
	
	/**
	 * 覆盖父类的create
	 * 在新增员工选择了所在部门时,在"员工_部门表"中插入信息.
	 * */
	public String create() throws Exception{
		Employee e = getEntityClass().newInstance();
		setEntity(e);
		employeeService.add(e);
		
		String primaryOrganization = p("primaryOrganization");
		if(primaryOrganization!=null&&primaryOrganization!=""){
			organizationService.addEmployee(primaryOrganization,e.getId()+"");
		}
		jo.put(ENTITY_KEY, toJsonObject(e));
		setJsonMessage(true, e.toString().equals("")?
				"新增了一条记录!" : "新增了("+e+")的记录");
		return render(jo);
	}
	
	/**
	 * 覆盖父类的update
	 * 若选择了部门修改项,先把此员工从原来的部门中移除,再加入到新的部门.
	 **/
	public String update() throws Exception{
		Employee entity = getDefService().get(id);
		if(entity.getOrganization()!=null)
		    organizationService.removeEmployee(entity.getOrganization().getId()+"", id);
		
		setEntity(entity);
		String primaryOrganization = p("organization");
		if(primaryOrganization!=null&&primaryOrganization!="")
		    organizationService.addEmployee(primaryOrganization,entity.getId()+"");
	
		getDefService().update(entity);
		jo.put(ENTITY_KEY, toJsonObject(entity));
		setJsonMessage(true, entity.toString().equals("")?
				"更新了一条记录!" : "更新了("+entity+")的记录");
		return render(jo);
	}
	
	protected JSONObject toJsonObject(Employee e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("code", e.getCode());		
		record.put("sex", e.getSex());
		record.put("status", e.getStatus());
		record.put("position", e.getPosition());
		record.put("education", e.getEducation());
		record.put("peopleType", e.getPeopleType());
		record.put("name", e.getName());
		record.put("phone", e.getPhone());
		record.put("mobile", e.getMobile());
		record.put("email", e.getEmail());
		record.put("hireDate", e.getHireDate());
		record.put("photoId",e.getPhotoId());
		record.put("organization", e.getOrganization());
		return record.getJsonObject();
	}
	
	/**
	 * 用于雇员选择的下拉菜单,可以用于拼音首字母和关键字查询
	 * @return　json
	 * @throws Exception
	 */
	public String getEmployees() throws Exception{
		List<Employee> employeeList = getDefService().findAll();
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
	
	/**
	 * 查询没有被分配用户的雇员
	 * @return json
	 * @throws Exception
	 */
	public String getEmployeesUnAssign() throws Exception{
		List<Employee> employees = employeeService.getEmployeesUnAssign();
		JSONArray ja = new JSONArray();
		for(Employee employee : employees){
			JSONObject record = new JSONObject();
			record.put("id", employee.getId());
			record.put("text", employee.getName());
			record.put("code", employee.getCode());
			ja.add(record);			
		}
		jo.put("data", ja);
		return render(jo);
	}
	
	public String setupPhoto() throws Exception {
		String uuid = Tools.uuid();
		//final String photoDir = configService.get(ConfigKey.PHOTO_DIR);
		FileUtils.copyFile(photoFile, new File(PHOTO_DIR+uuid+".jpg"));
		jo.put("photoId", uuid);
		setJsonMessage(true,"设置人员相片成功!");
		return render(jo.toString());
	}
	
	public String photo() throws Exception{
		//final String photoDir = configService.get(ConfigKey.PHOTO_DIR);
		return render(FileUtils.readFileToByteArray(new File(PHOTO_DIR+p("photoId")+".jpg")), "image/jpeg");
	}
	
	public String uploadPhoto()throws Exception {
		String uuid = Tools.uuid();
		File file = new File(PHOTO_DIR+uuid+".jpg");
		
		int size = 0;
		int len = 0;
		byte[] tmp = new byte[100000];
		
		response.setContentType("application/octet-stream");
		InputStream is = getRequest().getInputStream();
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
		while ((len = is.read(tmp)) != -1) {
			dos.write(tmp, 0, len);
			size += len;
		}
		dos.flush();
		dos.close();
		jo.put("photoId", uuid);
		setJsonMessage(true,"设置人员相片成功!");
		return render(jo.toString());
	}
	//================== setter and getter ===================
	
	@Override
	public GenericService<Employee> getDefService() {
		return this.employeeService;
	}

	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	public EmployeeService getEmployeeService() {
		return employeeService;
	}
	public File getPhotoFile() {
		return photoFile;
	}

	public void setPhotoFile(File photoFile) {
		this.photoFile = photoFile;
	}

	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	public OrganizationService getOrganizationService() {
		return organizationService;
	}
}
