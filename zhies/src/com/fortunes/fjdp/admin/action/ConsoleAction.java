package com.fortunes.fjdp.admin.action;

import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.fortunes.core.action.BaseAction;
import net.fortunes.util.Tools;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.model.Config.ConfigKey;
import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;
import com.fortunes.fjdp.admin.model.Privilege;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.ConfigService;
import com.fortunes.fjdp.admin.service.DictService;
import com.fortunes.fjdp.admin.service.EmployeeService;
import com.fortunes.fjdp.admin.service.MenuService;
import com.fortunes.fjdp.admin.service.OrganizationService;
import com.fortunes.fjdp.admin.service.PrivilegeService;
import com.fortunes.fjdp.admin.service.RoleService;
import com.fortunes.fjdp.admin.service.UserService;
import com.fortunes.zhies.model.Business;
import com.fortunes.zhies.model.Customer;
import com.fortunes.zhies.service.BusinessService;
import com.fortunes.zhies.service.CustomerService;

@Component @Scope("prototype")
public class ConsoleAction extends BaseAction implements ApplicationContextAware{
	
	public static final String REBUILD_DB = "rebuildDb";
	public static final String DICT_XML_PATH = "/dict.xml";
	public static final String FUNC_XML_PATH = "/function.xml";
	
	private ApplicationContext applicationContext;
	
	@Resource public PrivilegeService privilegeService;
	@Resource public EmployeeService employeeService;
	@Resource public OrganizationService organizationService;
	@Resource public RoleService roleService;
	@Resource public UserService userService;
	@Resource public DictService dictService;
	@Resource public MenuService menuService;
	@Resource public ConfigService configService;
	@Resource public BusinessService businessService;
	@Resource public CustomerService customerService;
		
	public String initDb() throws Exception{
		AnnotationSessionFactoryBean annotationSessionFactoryBean = 
			(AnnotationSessionFactoryBean)applicationContext.getBean("&sessionFactory");
		annotationSessionFactoryBean.dropDatabaseSchema();
		annotationSessionFactoryBean.createDatabaseSchema();
		
		
		menuService.initToDb(new InputStreamReader(
				this.getClass().getResourceAsStream(FUNC_XML_PATH),"utf-8"));
		dictService.initToDb(new InputStreamReader(
				this.getClass().getResourceAsStream(DICT_XML_PATH),"utf-8"));
		
		//初始化系统参数
		Map<ConfigKey, String> maps = new EnumMap<ConfigKey, String>(ConfigKey.class);
		maps.put(ConfigKey.APP_ROOT_DIR, "/home/weblogic");
		maps.put(ConfigKey.ADMIN_EMAIL, "admin@sz.pbc.org.cn");
		maps.put(ConfigKey.TEMP_UPLOAD_DIR, "/upload");
		configService.initConfigs(maps);
		
		//新建权限
		List<Privilege> pList = privilegeService.findAll();	
		
		//新建角色
		Role admin = new Role("系统管理员","admin",Role.SYSTEM_ROLE);
		Role sales = new Role("业务人员","sales",Role.SYSTEM_ROLE);
		Role opers = new Role("操作员","operator",Role.SYSTEM_ROLE);
		Role financials = new Role("财务人员","financials",Role.SYSTEM_ROLE);
		Role managers = new Role("总经理","manager",Role.SYSTEM_ROLE);
		roleService.add(admin);
		roleService.add(sales);
		roleService.add(opers);
		roleService.add(financials);
		roleService.add(managers);
		
		//关联角色和权限
		admin.setPrivileges(pList);
	
		//新建员工
		Employee adminEmployee = new Employee("00","超级管理员");
		employeeService.add(adminEmployee);
		
		//新建用户 
		User adminUser = new User("admin",Tools.encodePassword("admin"),adminEmployee.getName());
		
		//关联用户和角色,用户和员工
		adminUser.setEmployee(adminEmployee);
		
		adminUser.setRole(admin);
		
		userService.add(adminUser);
		
		//新建部门 
		Organization root = new Organization(null,"组织root","");
		organizationService.add(root);
		
		Organization deparment1 = new Organization(root,"销售部","销售部");
		organizationService.add(deparment1);
		
		Organization deparment2 = new Organization(root,"财务部","财务部");
		organizationService.add(deparment2);
		
		//服务
		Business A = new Business("A","报关服务");
		Business B = new Business("B","单证制作");
		Business C = new Business("C","产地证制作");
		Business D = new Business("D","商检");
		Business E = new Business("E","拖车运输");
		Business F = new Business("F","国际运输");
		businessService.add(A);
		businessService.add(B);
		businessService.add(C);
		businessService.add(D);
		businessService.add(E);
		businessService.add(F);
		
		//customer
		Customer c = new Customer();
		c.setName("Crocs");
		customerService.add(c);
		
		return render(jo);
	}
	
	public String rebuildDb() throws Exception{
		menuService.initToDb(new InputStreamReader(
				this.getClass().getResourceAsStream(FUNC_XML_PATH),"utf-8"));
		dictService.initToDb(new InputStreamReader(
				this.getClass().getResourceAsStream(DICT_XML_PATH),"utf-8"));
		return render(jo);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
		
	}	

}
