
Statements_QueryPanel = Ext.extend(Ext.form.FormPanel,{
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
				{ xtype: 'f-customer',fieldLabel: '客户',hiddenName: 'customer',allowBlank: false},
				{xtype: 's-yearmonth', fieldLabel: '月份', name: 'yearmonth',allowBlank: false},
				{xtype: 'button', text: '查询记录',width: 80,handler: this.search,scope:this,
					style: 'padding-top: 20px; padding-left: 60px;'}
			] 
		});
		Statements_QueryPanel.superclass.initComponent.call(this);
	},
	search : function(){
		if(this.getForm().isValid()){
			var tabPanel = Ext.getCmp('StatementsReport');
			tabPanel.loadData(this.getForm().getValues());
		}else{
			App.msg("某些查询条件不能为空!");
		}
		
	}
});


StatementsReport = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '业务发生时间',dataIndex:'createDate'},
					{header: '业务编号',dataIndex:'code'},
					//{header: '客户',dataIndex:'customer',renderer:dictRenderer},
					//{header: '买方',dataIndex:'buyer',renderer:dictRenderer},
					{header: '货物描述',dataIndex:'itemDesc'},
					//{header: '核销单主',dataIndex:'verificationCompany',renderer:dictRenderer},
					//{header: '核销单号',dataIndex:'verificationFormNo'},
					//{header: '业务员',dataIndex:'sales',renderer:dictRenderer},
					//{header: '操作员',dataIndex:'operator',renderer:dictRenderer},
					{header: '口岸',dataIndex:'loadingPort'},
					{header: '报关服务',dataIndex:'cost_A'},
					{header: '单证制作',dataIndex:'cost_B'},
					{header: '产地证制作',dataIndex:'cost_C'},
					{header: '商检',dataIndex:'cost_D'},
					{header: '拖车运输',dataIndex:'cost_E'},
					{header: '国际运输',dataIndex:'cost_F'},
					{header: '港建费',dataIndex:'cost_G'},
					{header: '其它费用',dataIndex:'cost_Z'},
					{header: '总费用',dataIndex:'totalSalesPrice'}
				]),	
				storeMapping:[
					'createDate','code', 'customer','buyer','sales','operator', 'itemDesc','verificationCompany', 'verificationFormNo', 'loadingPort',
					'cost_A','cost_B', 'cost_C', 'cost_D','cost_E','cost_F','cost_G', 'cost_Z', 'totalSalesPrice'
				]

			},
			buttonConfig :[{
				text : '导出报表',
				iconCls : 'excel',
				scope : this,
				handler : this.exportFile
			}],
			url:ctx+'/trade',
			listUrl : '/statements'
		});
		StatementsReport.superclass.initComponent.call(this);
	},
	exportFile : function(){
		location.href = this.url + '/exportStatements?'+Ext.getCmp('Statements_QueryPanel').getForm().getValues(true);
	}
});

Statements = Ext.extend(Ext.Panel, {
	initComponent: function() {
		Ext.apply(this, {
			closable: true,
			layout: 'border',
			items: [
				new Statements_QueryPanel({
					id : 'Statements_QueryPanel',
					region: 'west', 
					width: 210, 
					split: true, 
					collapsible: true, 
					collapseMode: 'mini'
				}),
				
				new StatementsReport({
					id: 'StatementsReport',
					funcCode: this.funcCode,
					region: 'center' 
				})
			]
		});
		
		Statements.superclass.initComponent.call(this);
	},
	loadData: function() {
	}
})
