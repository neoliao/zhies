package com.fortunes.fjdp.admin.action;

import java.io.InputStreamReader;
import java.util.Date;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.AppHelper;
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
import com.fortunes.zhies.model.AirCompany;
import com.fortunes.zhies.model.Business;
import com.fortunes.zhies.model.Buyer;
import com.fortunes.zhies.model.Customer;
import com.fortunes.zhies.model.CustomsBroker;
import com.fortunes.zhies.model.Inspection;
import com.fortunes.zhies.model.Item;
import com.fortunes.zhies.model.ShipCompany;
import com.fortunes.zhies.model.Trade;
import com.fortunes.zhies.model.TruckCompany;
import com.fortunes.zhies.model.VerificationCompany;
import com.fortunes.zhies.service.AirCompanyService;
import com.fortunes.zhies.service.BusinessService;
import com.fortunes.zhies.service.BuyerService;
import com.fortunes.zhies.service.CustomerService;
import com.fortunes.zhies.service.CustomsBrokerService;
import com.fortunes.zhies.service.InspectionService;
import com.fortunes.zhies.service.ShipCompanyService;
import com.fortunes.zhies.service.TradeService;
import com.fortunes.zhies.service.TruckCompanyService;
import com.fortunes.zhies.service.VerificationCompanyService;

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
	@Resource public CustomsBrokerService customsBrokerService;
	@Resource public TruckCompanyService truckCompanyService;
	@Resource public InspectionService inspectionService;
	@Resource public VerificationCompanyService verificationCompanyService;
	@Resource public ShipCompanyService shipCompanyService;
	@Resource public AirCompanyService airCompanyService;
	@Resource public BuyerService buyerService;
	@Resource public TradeService tradeService;
	
	@Resource JdbcTemplate jdbcTemplate;
	
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
		Employee adminEmployee = new Employee("001","谭生");
		employeeService.add(adminEmployee);
		
		//新建用户 
		User adminUser = new User("admin",Tools.encodePassword("admin"),adminEmployee.getName());
		
		//关联用户和角色,用户和员工
		adminUser.setEmployee(adminEmployee);
		
		adminUser.getRoles().add(admin);
		
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
		Business G = new Business("G","港建费");
		Business Z = new Business("Z","其它费用");
		businessService.add(A);
		businessService.add(B);
		businessService.add(C);
		businessService.add(D);
		businessService.add(E);
		businessService.add(F);
		businessService.add(G);
		businessService.add(Z);
		
		//customer
		Customer c = new Customer();
		c.setName("天汇国际");
		c.setCode("TH");
		customerService.add(c);
		
		Buyer b = new Buyer();
		b.setName("Crocs");
		b.setCode("CRS");
		b.setAddress("Road 1223,Mexico city,Mexico");
		b.setCustomer(c);
		buyerService.add(b);
		
		CustomsBroker cb = new CustomsBroker();
		cb.setName("罗湖报关行");
		customsBrokerService.add(cb);
		
		Inspection cb2 = new Inspection();
		cb2.setName("万唐商检公司");
		inspectionService.add(cb2);
		
		TruckCompany cb3 = new TruckCompany();
		cb3.setName("长平运输公司");
		truckCompanyService.add(cb3);
		
		ShipCompany cb4 = new ShipCompany();
		cb4.setName("海星货运");
		shipCompanyService.add(cb4);
		
		AirCompany cb5 = new AirCompany();
		cb5.setName("深圳申能运输");
		airCompanyService.add(cb5);
		
		VerificationCompany cb6 = new VerificationCompany();
		cb6.setName("中惠进出口");
		verificationCompanyService.add(cb6);
		
		return render(jo);
	}
	
	public String rebuildDb() throws Exception{
		menuService.initToDb(new InputStreamReader(
				this.getClass().getResourceAsStream(FUNC_XML_PATH),"utf-8"));
		dictService.initToDb(new InputStreamReader(
				this.getClass().getResourceAsStream(DICT_XML_PATH),"utf-8"));
		return render(jo);
	}
	
	public String convert() throws Exception{
		SqlRowSet  rs = jdbcTemplate.queryForRowSet("select * from Export");
		while(rs.next()){
			long id = rs.getLong("id");
			System.out.println(id);
			Trade t = tradeService.get(id+"");
			
			//t.setBuyer(AppHelper.toBuyer(rsget(rs,"buyer_id")));
			t.setCustomsBroker(AppHelper.toCustomsBroker(rsget(rs,"customsBroker_id")));//报关行
			
			t.setLoadingCity(rsget(rs,"loadingCity"));//装运口岸
			t.setLoadingPort(rsget(rs,"loadingPort"));
			t.setDestination(rsget(rs,"destination"));//目的地
			t.setDestinationPort(rsget(rs,"destinationPort"));
			t.setCurrency(AppHelper.toDict(rsget(rs,"currency_id")));
			t.setCabNo(rsget(rs,"cabNo"));
			t.setCabType(rsget(rs,"cabType"));
			//t.setSoNo(rsget(rs,"soNo"));
			t.setVerificationCompany(AppHelper.toVerificationCompany(rsget(rs,"verificationCompany_id")));
			t.setVerificationFormNo(rsget(rs,"verificationFormNo"));
			
			t.setMark(rsget(rs,"mark"));//唛头
			t.setContractNo(rsget(rs,"contractNo"));//合同号
			t.setContractDate(rsgetDate(rs,"contractDate"));//合同日期
			t.setInvoiceNo(rsget(rs,"invoiceNo"));//发票号
			t.setInvoiceDate(rsgetDate(rs,"invoiceDate"));//发票日期
			t.setTradeType(rsget(rs,"tradeType"));//成交方式
			t.setSignCity(rsget(rs,"signCity"));
			t.setPayCondition(rsget(rs,"payCondition"));
			t.setMemos(rsget(rs,"memos"));
			t.setTaxMemos(rsget(rs,"taxMemos"));
			t.setItemsCity(rsget(rs,"itemsCity"));
			
			t.setGrossWeight(rsgetDouble(rs,"grossWeight"));//毛重KG
			t.setNetWeight(rsgetDouble(rs,"netWeight"));//净重KG
			
			t.setStoragePeriod(rsget(rs,"storagePeriod"));
			t.setStorageVehicle(rsget(rs,"storageVehicle"));
			t.setPackageAndModel(rsget(rs,"packageAndModel"));
			
			//for 产地证
			t.setProducerNo(rsget(rs,"producerNo"));//产地证号
			t.setPackageNumber(rsgetInt(rs,"packageNumber"));//箱数
			t.setProduceDate(rsgetDate(rs,"produceDate"));//产地日期
			
			//for inspection 商检
			t.setInspection(AppHelper.toInspection(rsget(rs,"inspection_id")));//商检行
			t.setExportPort(rsget(rs,"exportPort"));//出口口岸
			t.setInspectionTransType(rsget(rs,"inspectionTransType"));//商检运输方式
			
			//for transport
			t.setTruckCompany(AppHelper.toTruckCompany(rsget(rs,"truckCompany_id")));//拖车公司
			t.setTransportType(AppHelper.toDict(rsget(rs,"transportType_id")));//运输方式
			t.setLoadingFactory(rsget(rs,"loadingFactory"));//装载工厂
			t.setLoadingFactoryAddr(rsget(rs,"loadingFactoryAddr"));//装载工厂地址
			t.setDeliverPort(rsget(rs,"deliverPort"));//发货港口
			t.setDriver(rsget(rs,"driver"));//司机
			t.setDriverPhone(rsget(rs,"driverPhone"));//司机电话
			t.setTruckLicense(rsget(rs,"truckLicense"));//车牌号
			
			t.setShipCompany(AppHelper.toShipCompany(rsget(rs,"shipCompany_id")));
			t.setAirCompany(AppHelper.toAirCompany(rsget(rs,"airCompany_id")));
			t.setVolume(rsgetDouble(rs,"volume"));//体积
			t.setWeight(rsgetDouble(rs,"weight"));//重量
			t.setLadingBillNo(rsget(rs,"ladingBillNo"));//提单号
			
			int totalPackage = 0;
			for(Item i : t.getItems()){
				totalPackage += i.getPackageQuantity();
			}
			t.setTotalPackage(totalPackage);
			tradeService.update(t);
		}
		return render(jo);
	}
	
	

	private String rsget(SqlRowSet rs,String string) {
		return rs.getString(string);
	}
	
	private Date rsgetDate(SqlRowSet rs,String string) {
		return rs.getDate(string);
	}
	
	private int rsgetInt(SqlRowSet rs,String string) {
		return rs.getInt(string);
	}
	
	private Double rsgetDouble(SqlRowSet rs,String string) {
		return rs.getDouble(string);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
		
	}	

}
