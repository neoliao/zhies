
TradeReport_QueryPanel = Ext.extend(Ext.form.FormPanel,{
	initComponent: function() {
		var firstDay = new Date().getFirstDateOfMonth();
		Ext.apply(this, {
			title: '查询条件',
			labelAlign: 'top',
			bodyStyle: 'padding: 20px',
			defaults:{
				width: 166
			},
			items: [
				{xtype: 'f-radiogroup', fieldLabel: '业务类型', allowBlank: false, items: [
						{boxLabel: '全部', name: 'tradeType', inputValue: 'ALL',checked: true},
						{boxLabel: '出口', name: 'tradeType', inputValue: 'EXPORT'},
						{boxLabel: '进口', name: 'tradeType', inputValue: 'IMPORT'}
					]
				},
				{ xtype: 'f-customer',fieldLabel: '客户',hiddenName: 'customer',id:'exportCustomer'},
				{xtype: 's-yearmonth', fieldLabel: '月份', name: 'yearmonth',allowBlank: false},
				{xtype: 'button', text: '查询记录',width: 80,handler: this.search,scope:this,
					style: 'padding-top: 20px; padding-left: 60px;'}
			] 
		});
		TradeReport_QueryPanel.superclass.initComponent.call(this);
	},
	search : function(){
		var tabPanel = Ext.getCmp('TradeReportTabPanel');
		tabPanel.loadData(this.getForm().getValues());
	}
});


TradeReport = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '创建时间',dataIndex:'createDate'},
					{header: '业务编号',dataIndex:'code'},
					{header: '客户',dataIndex:'customer',renderer:dictRenderer},
					{header: '买方',dataIndex:'buyer',renderer:dictRenderer},
					{header: '货物描述',dataIndex:'itemDesc'},
					{header: '核销单主',dataIndex:'verificationCompany',renderer:dictRenderer},
					{header: '核销单号',dataIndex:'verificationFormNo'},
					{header: '业务员',dataIndex:'sales',renderer:dictRenderer},
					{header: '操作员',dataIndex:'operator',renderer:dictRenderer},
					{header: '口岸',dataIndex:'loadingPort'},
					{header: '销售额',dataIndex:'totalSalesPrice'},
					{header: '销售实际成本',dataIndex:'totalActuralCost'},
					{header: '毛利润',dataIndex:'commission'}
				]),	
				storeMapping:[
					'createDate','code', 'customer','buyer','sales','operator', 'itemDesc','totalSalesPrice','totalCost', 'totalActuralCost', 'commission',
					'verificationCompany', 'verificationFormNo', 'loadingPort'
				]

			},
			buttonConfig :[
			
			],
			url:ctx+'/trade',
			listUrl : '/report'
		});
		TradeReport.superclass.initComponent.call(this);
	}
});

SalesPieChart = Ext.extend(Ext.chart.PieChart, {
    initComponent: function(){
    	
    	var pieStore = new Ext.data.JsonStore({
	        fields: ['salesName','salesId','totalProfit'],
	        root: 'data',
	        url:ctx+'/trade/salesPieReport'
	    });
	    
    	Ext.apply(this,{
            categoryField : 'salesName',
            dataField : 'totalProfit',
            store : pieStore,
            style : 'border-style: solid;border-width:1px;border-color: #99bbe8;',
            extraStyle:{
            	padding:30,
                legend:{
                    display: 'bottom',
                    padding: 5,
                    font:{
                        size: 14
                    }
                }
            }
        });
        
        SalesPieChart.superclass.initComponent.call(this);   
    },
    loadData: function(formValues){
    	this.store.load({params:formValues});
    }
});

CustomerPieChart = Ext.extend(Ext.chart.PieChart, {
    initComponent: function(){
    	
    	var pieStore = new Ext.data.JsonStore({
	        fields: ['customerName','customerId','totalProfit'],
	        root: 'data',
	        url:ctx+'/trade/customerPieReport'
	    });
	    
    	Ext.apply(this,{
            categoryField : 'customerName',
            dataField : 'totalProfit',
            store : pieStore,
            style : 'border-style: solid;border-width:1px;border-color: #99bbe8;',
            extraStyle:{
            	padding:30,
                legend:{
                    display: 'bottom',
                    padding: 5,
                    font:{
                        size: 14
                    }
                }
            }
        });
        
        CustomerPieChart.superclass.initComponent.call(this);   
    },
    loadData: function(formValues){
    	this.store.load({params:formValues});
    }
});

TradeReportTabPanel = Ext.extend(Ext.TabPanel, {
	initComponent: function() {
		Ext.apply(this, {
			activeItem : 0,
			items: [
				new TradeReport({
					id: 'TradeReport',
					closable: false,
					title : '业绩表',
					funcCode: this.funcCode
				}),
				new SalesPieChart({
					closable: false,
					title : '业务员业绩分析图'
				}),new CustomerPieChart({
					closable: false,
					title : '客户贡献分析图'
				})
				
			]
		});
		
		TradeReportTabPanel.superclass.initComponent.call(this);
		
		this.on('tabchange',function(){
			this.loadData(Ext.getCmp('TradeReport_QueryPanel').getForm().getValues());
		},this);
		
	},
	loadData: function(v) {
		this.layout.activeItem.loadData(v);
	}
})

Report = Ext.extend(Ext.Panel, {
	initComponent: function() {
		Ext.apply(this, {
			closable: true,
			layout: 'border',
			items: [
				new TradeReport_QueryPanel({
					id : 'TradeReport_QueryPanel',
					region: 'west', 
					width: 210, 
					split: true, 
					collapsible: true, 
					collapseMode: 'mini'
				}),
				
				new TradeReportTabPanel({
					id: 'TradeReportTabPanel',
					funcCode: this.funcCode,
					region: 'center' 
				})
			]
		});
		
		Report.superclass.initComponent.call(this);
	},
	loadData: function() {
	}
})
