package com.fortunes.zhies.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import net.fortunes.core.Model;
import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.User;

@Entity
public class Export extends Model{
	
	public enum Status{
		CREATED,
		SUBMITED,
		ASSIGNED,
		FINISHED
	}
	
	@Id @GeneratedValue
	private long id;
	
	private String code;
	
	@Temporal(TemporalType.DATE)
	private Date createDate;//创建日期
	
	@ManyToOne
	private Dict currency;
	
	@Temporal(TemporalType.DATE)
	private Date reportPortDate;//报关日期
	
	private String cabNo;//柜号
	
	private String cabType;//柜型
	
	private String verificationFormNo;//核销单号
	
	@ManyToOne
	private VerificationCompany verificationCompany;//核销单公司
	
	@ManyToOne
	private User sales;//销售业务员
	
	@ManyToOne
	private User operator;//操作员
	
	@Enumerated(EnumType.STRING)
	private Status status;//业务状态
	
	@ManyToOne
	private User currentOperator;//当前处理人
	
	@ManyToOne
	private Customer customer;//客户
	
	@OneToMany(mappedBy = "export",cascade  = CascadeType.REMOVE)
	private List<Item> items;//货物
	
	@OneToMany(mappedBy = "export",cascade  = CascadeType.REMOVE)
	private List<BusinessInstance> businessInstances;//包含的服务
	
	//for CustomsBroker 报关
	@ManyToOne
	private CustomsBroker customsBroker;
	
	private String loadingCity;//出口地
	
	private String loadingPort;//装运口岸/出口口岸
	
	private String destination;//目的地
	
	private String destinationPort;//目的港
	
	private String mark;//唛头
	
	private String contractNo;//合同号
	
	@Temporal(TemporalType.DATE)
	private Date contractDate;//合同日期
	
	private String invoiceNo;//发票号
	
	@Temporal(TemporalType.DATE)
	private Date invoiceDate;//发票日期
	
	private String tradeType;//成交方式
	
	private double grossWeight;//毛重KG
	
	private double netWeight;//净重KG
	
	//for 产地证
	private String producerNo;//产地证号
	
	private int packageNumber;//箱数
	
	@Temporal(TemporalType.DATE)
	private Date produceDate;//产地日期
	
	//for inspection 商检
	@ManyToOne
	private Inspection inspection;//商检行
	
	private String exportPort;//出口口岸
	
	private String inspectionTransType;//商检运输方式
	
	//for transport
	@ManyToOne
	private TruckCompany truckCompany;//拖车公司
	
	@ManyToOne
	private Dict transportType;//运输方式
	
	private String loadingFactory;//装载工厂
	
	private String loadingFactoryAddr;//装载工厂地址
	
	private String deliverPort;//发货港口
	
	private String driver;//司机
	
	private String driverPhone;//司机电话
	
	private String truckLicense;//车牌号
	
	//for 国际运输
	@ManyToOne
	private Dict shipType;//国际运输方式,海运,空运 
	
	private String destinitionPort;//目的港
	
	private double volume;//体积
	
	private double weight;//重量
	
	private String ladingBillNo;//提单号
	
	@ManyToOne
	private ShipCompany shipCompany;
	
	@ManyToOne
	private AirCompany airCompany;
	
	
    public Export() {
    }
    
    public Export(long id) {
    	this.id = id;
    }
    
    @Override
	public String toString() {
		return "";
	}
    
    /*=============== setter and getter =================*/
    
    public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public User getSales() {
		return sales;
	}

	public void setSales(User sales) {
		this.sales = sales;
	}

	public User getOperator() {
		return operator;
	}

	public void setOperator(User operator) {
		this.operator = operator;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public User getCurrentOperator() {
		return currentOperator;
	}

	public void setCurrentOperator(User currentOperator) {
		this.currentOperator = currentOperator;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public CustomsBroker getCustomsBroker() {
		return customsBroker;
	}

	public void setCustomsBroker(CustomsBroker customsBroker) {
		this.customsBroker = customsBroker;
	}

	public String getLoadingPort() {
		return loadingPort;
	}

	public void setLoadingPort(String loadingPort) {
		this.loadingPort = loadingPort;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Date getContractDate() {
		return contractDate;
	}

	public void setContractDate(Date contractDate) {
		this.contractDate = contractDate;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public double getGrossWeight() {
		return grossWeight;
	}

	public void setGrossWeight(double grossWeight) {
		this.grossWeight = grossWeight;
	}

	public double getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(double netWeight) {
		this.netWeight = netWeight;
	}

	public String getProducerNo() {
		return producerNo;
	}

	public void setProducerNo(String producerNo) {
		this.producerNo = producerNo;
	}

	public int getPackageNumber() {
		return packageNumber;
	}

	public void setPackageNumber(int packageNumber) {
		this.packageNumber = packageNumber;
	}

	public Date getProduceDate() {
		return produceDate;
	}

	public void setProduceDate(Date produceDate) {
		this.produceDate = produceDate;
	}

	public Inspection getInspection() {
		return inspection;
	}

	public void setInspection(Inspection inspection) {
		this.inspection = inspection;
	}

	public String getExportPort() {
		return exportPort;
	}

	public void setExportPort(String exportPort) {
		this.exportPort = exportPort;
	}

	public TruckCompany getTruckCompany() {
		return truckCompany;
	}

	public void setTruckCompany(TruckCompany truckCompany) {
		this.truckCompany = truckCompany;
	}

	public Dict getTransportType() {
		return transportType;
	}

	public void setTransportType(Dict transportType) {
		this.transportType = transportType;
	}

	public String getLoadingFactory() {
		return loadingFactory;
	}

	public void setLoadingFactory(String loadingFactory) {
		this.loadingFactory = loadingFactory;
	}

	public String getLoadingFactoryAddr() {
		return loadingFactoryAddr;
	}

	public void setLoadingFactoryAddr(String loadingFactoryAddr) {
		this.loadingFactoryAddr = loadingFactoryAddr;
	}

	public String getDeliverPort() {
		return deliverPort;
	}

	public void setDeliverPort(String deliverPort) {
		this.deliverPort = deliverPort;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getDriverPhone() {
		return driverPhone;
	}

	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}

	public String getTruckLicense() {
		return truckLicense;
	}

	public void setTruckLicense(String truckLicense) {
		this.truckLicense = truckLicense;
	}

	public Dict getShipType() {
		return shipType;
	}

	public void setShipType(Dict shipType) {
		this.shipType = shipType;
	}


	public String getDestinitionPort() {
		return destinitionPort;
	}

	public void setDestinitionPort(String destinitionPort) {
		this.destinitionPort = destinitionPort;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getLadingBillNo() {
		return ladingBillNo;
	}

	public void setLadingBillNo(String ladingBillNo) {
		this.ladingBillNo = ladingBillNo;
	}

	public ShipCompany getShipCompany() {
		return shipCompany;
	}

	public void setShipCompany(ShipCompany shipCompany) {
		this.shipCompany = shipCompany;
	}

	public AirCompany getAirCompany() {
		return airCompany;
	}

	public void setAirCompany(AirCompany airCompany) {
		this.airCompany = airCompany;
	}

	public void setBusinessInstances(List<BusinessInstance> businessInstances) {
		this.businessInstances = businessInstances;
	}

	public List<BusinessInstance> getBusinessInstances() {
		return businessInstances;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setInspectionTransType(String inspectionTransType) {
		this.inspectionTransType = inspectionTransType;
	}

	public String getInspectionTransType() {
		return inspectionTransType;
	}

	public void setVerificationFormNo(String verificationFormNo) {
		this.verificationFormNo = verificationFormNo;
	}

	public String getVerificationFormNo() {
		return verificationFormNo;
	}

	public void setReportPortDate(Date reportPortDate) {
		this.reportPortDate = reportPortDate;
	}

	public Date getReportPortDate() {
		return reportPortDate;
	}

	public void setVerificationCompany(VerificationCompany verificationCompany) {
		this.verificationCompany = verificationCompany;
	}

	public VerificationCompany getVerificationCompany() {
		return verificationCompany;
	}

	public void setCabType(String cabType) {
		this.cabType = cabType;
	}

	public String getCabType() {
		return cabType;
	}

	public void setCabNo(String cabNo) {
		this.cabNo = cabNo;
	}

	public String getCabNo() {
		return cabNo;
	}

	public void setDestinationPort(String destinationPort) {
		this.destinationPort = destinationPort;
	}

	public String getDestinationPort() {
		return destinationPort;
	}

	public void setLoadingCity(String loadingCity) {
		this.loadingCity = loadingCity;
	}

	public String getLoadingCity() {
		return loadingCity;
	}

	public void setCurrency(Dict currency) {
		this.currency = currency;
	}

	public Dict getCurrency() {
		return currency;
	}

}
