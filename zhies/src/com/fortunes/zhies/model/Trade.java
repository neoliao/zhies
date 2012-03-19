package com.fortunes.zhies.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.compass.annotations.Cascade;

import net.fortunes.core.Model;

import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.User;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public class Trade extends Model {
	
	public enum Status{
		CREATED,
		ASSIGNED,
		OPERATOR_SAVED,
		OPERATOR_SUBMITED,
		COST_CONFIRMED,
		FINISHED
	}
	
	@Id @GeneratedValue
	private long id;
	
	private String code;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;//创建日期
	
	private String memo;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date endedDate;//结束日期(已收款)
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date finishDate;//完成日期(未收款)
	
	@ManyToOne
	private User sales;//销售业务员
	
	@ManyToOne
	private User operator;//操作员
	
	@Enumerated(EnumType.STRING)
	private Status status;//业务状态
	
	@ManyToOne
	private Customer customer;//客户
	
	/*@ManyToOne
	private Buyer buyer;*/
	
	private String buyerName;
	
	private String itemDesc;
	
	private String itemQuantity;
	
	private String soNo;
	
	@OneToMany(mappedBy = "trade",cascade = CascadeType.REMOVE)
	private List<Item> items = new ArrayList<Item>();//货物
	
	@OneToMany(mappedBy = "trade",cascade = CascadeType.REMOVE)
	private List<BusinessInstance> businessInstances = new ArrayList<BusinessInstance>();//包含的服务
	
	private Double totalSalesPrice;
	private Double totalCost;
	private Double totalActualCost;
	
	private Integer totalPackage;

	//for CustomsBroker 报关
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
	private CustomsBroker customsBroker;
	private String loadingCity;//出口地
	
	@ManyToOne
	private Dict loadingPort;//装运口岸/出口口岸
	//private String loadingPort;//装运口岸/出口口岸
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
	//private String curencyType;//结汇方式
	private String signCity;
	private String payCondition;
	private String memos;
	private String taxMemos;
	private String itemsCity;
	
	//for 存仓
	
	private String storagePeriod;
	private String storageVehicle;
	private String packageAndModel;
	private Double grossWeight;//毛重KG
	private Double netWeight;//净重KG
	
	//for 产地证
	@ManyToOne
	private Dict producerCertificate;
	private String producerNo;//产地证号
	private Integer packageNumber;//箱数
	
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

	private Double volume;//体积
	private Double weight;//重量
	private String ladingBillNo;//提单号
	@ManyToOne
	private ShipCompany shipCompany;
	@ManyToOne
	private AirCompany airCompany;
	
	private boolean caluateAsTotalPrice;//以总价来计算单价
	
	private String otherPrice;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getEndedDate() {
		return endedDate;
	}
	public void setEndedDate(Date endedDate) {
		this.endedDate = endedDate;
	}
	public Date getFinishDate() {
		return finishDate;
	}
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
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
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	/*public Buyer getBuyer() {
		return buyer;
	}
	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}*/
	public String getItemDesc() {
		return itemDesc;
	}
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
	public String getItemQuantity() {
		return itemQuantity;
	}
	public void setItemQuantity(String itemQuantity) {
		this.itemQuantity = itemQuantity;
	}
	public String getSoNo() {
		return soNo;
	}
	public void setSoNo(String soNo) {
		this.soNo = soNo;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	public List<BusinessInstance> getBusinessInstances() {
		return businessInstances;
	}
	public void setBusinessInstances(List<BusinessInstance> businessInstances) {
		this.businessInstances = businessInstances;
	}
	public Double getTotalSalesPrice() {
		return totalSalesPrice;
	}
	public void setTotalSalesPrice(Double totalSalesPrice) {
		this.totalSalesPrice = totalSalesPrice;
	}
	public Double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}
	public Double getTotalActualCost() {
		return totalActualCost;
	}
	public void setTotalActualCost(Double totalActualCost) {
		this.totalActualCost = totalActualCost;
	}
	public Integer getTotalPackage() {
		return totalPackage;
	}
	public void setTotalPackage(Integer totalPackage) {
		this.totalPackage = totalPackage;
	}
	public Dict getCurrency() {
		return currency;
	}
	public void setCurrency(Dict currency) {
		this.currency = currency;
	}
	public Date getReportPortDate() {
		return reportPortDate;
	}
	public void setReportPortDate(Date reportPortDate) {
		this.reportPortDate = reportPortDate;
	}
	public String getCabNo() {
		return cabNo;
	}
	public void setCabNo(String cabNo) {
		this.cabNo = cabNo;
	}
	public String getCabType() {
		return cabType;
	}
	public void setCabType(String cabType) {
		this.cabType = cabType;
	}
	public String getVerificationFormNo() {
		return verificationFormNo;
	}
	public void setVerificationFormNo(String verificationFormNo) {
		this.verificationFormNo = verificationFormNo;
	}
	public VerificationCompany getVerificationCompany() {
		return verificationCompany;
	}
	public void setVerificationCompany(VerificationCompany verificationCompany) {
		this.verificationCompany = verificationCompany;
	}
	public CustomsBroker getCustomsBroker() {
		return customsBroker;
	}
	public void setCustomsBroker(CustomsBroker customsBroker) {
		this.customsBroker = customsBroker;
	}
	public String getLoadingCity() {
		return loadingCity;
	}
	public void setLoadingCity(String loadingCity) {
		this.loadingCity = loadingCity;
	}
	
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getDestinationPort() {
		return destinationPort;
	}
	public void setDestinationPort(String destinationPort) {
		this.destinationPort = destinationPort;
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
	public String getSignCity() {
		return signCity;
	}
	public void setSignCity(String signCity) {
		this.signCity = signCity;
	}
	public String getPayCondition() {
		return payCondition;
	}
	public void setPayCondition(String payCondition) {
		this.payCondition = payCondition;
	}
	public String getMemos() {
		return memos;
	}
	public void setMemos(String memos) {
		this.memos = memos;
	}
	public String getTaxMemos() {
		return taxMemos;
	}
	public void setTaxMemos(String taxMemos) {
		this.taxMemos = taxMemos;
	}
	public String getItemsCity() {
		return itemsCity;
	}
	public void setItemsCity(String itemsCity) {
		this.itemsCity = itemsCity;
	}
	public String getStoragePeriod() {
		return storagePeriod;
	}
	public void setStoragePeriod(String storagePeriod) {
		this.storagePeriod = storagePeriod;
	}
	public String getStorageVehicle() {
		return storageVehicle;
	}
	public void setStorageVehicle(String storageVehicle) {
		this.storageVehicle = storageVehicle;
	}
	public String getPackageAndModel() {
		return packageAndModel;
	}
	public void setPackageAndModel(String packageAndModel) {
		this.packageAndModel = packageAndModel;
	}
	public Double getGrossWeight() {
		return grossWeight;
	}
	public void setGrossWeight(Double grossWeight) {
		this.grossWeight = grossWeight;
	}
	public Double getNetWeight() {
		return netWeight;
	}
	public void setNetWeight(Double netWeight) {
		this.netWeight = netWeight;
	}
	public String getProducerNo() {
		return producerNo;
	}
	public void setProducerNo(String producerNo) {
		this.producerNo = producerNo;
	}
	public Integer getPackageNumber() {
		return packageNumber;
	}
	public void setPackageNumber(Integer packageNumber) {
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
	public String getInspectionTransType() {
		return inspectionTransType;
	}
	public void setInspectionTransType(String inspectionTransType) {
		this.inspectionTransType = inspectionTransType;
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
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
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
	public void setCaluateAsTotalPrice(boolean caluateAsTotalPrice) {
		this.caluateAsTotalPrice = caluateAsTotalPrice;
	}
	public boolean isCaluateAsTotalPrice() {
		return caluateAsTotalPrice;
	}
	public void setProducerCertificate(Dict producerCertificate) {
		this.producerCertificate = producerCertificate;
	}
	public Dict getProducerCertificate() {
		return producerCertificate;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getMemo() {
		return memo;
	}
	public void setOtherPrice(String otherPrice) {
		this.otherPrice = otherPrice;
	}
	public String getOtherPrice() {
		return otherPrice;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public void setLoadingPort(Dict loadingPort) {
		this.loadingPort = loadingPort;
	}
	public Dict getLoadingPort() {
		return loadingPort;
	}
	

	
	
}
