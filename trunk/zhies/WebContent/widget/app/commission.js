
CommissionSummary = Ext.extend(Ext.app.BaseFuncPanel,{
	paging:false,
	initComponent : function(){
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '月份',dataIndex:'month'},
					{header: '业务员',dataIndex:'salesName'},
					{header: '总销售额',dataIndex:'totalSalesPrice'},
					{header: '总销售成本',dataIndex:'totalCost'},
					{header: '总完成额',dataIndex:'commission'}
				]),	
				storeMapping:[
					'salesName','salesId','month','totalSalesPrice', 'totalCost', 'commission'
				]

			},
			buttonConfig :[
				/*'月份:',
				{xtype: 'f-year',name: 'year',width:100},
			    {xtype: 'f-month',name: 'month',width:100},*/
			    //'业务员:',
			    {xtype: 'f-sales',id:'CommissionSummarySales',privilegeCode:this.funcCode+'_view_all',hiddenName: 'sales',width:100}
			],
			url:ctx+'/trade',
			listUrl : '/commissionSummary'
		});
		CommissionSummary.superclass.initComponent.call(this);
		
		Ext.getCmp('CommissionSummarySales').on('change',function(f,v,oldValue){
			Ext.getCmp('CommissionDetail').store.removeAll();
			this.store.load({params : {
				salesId : v
			}})
		},this);

	}
	
});



CommissionDetail = Ext.extend(Ext.app.BaseFuncPanel,{
	paging:false,
	initComponent : function(){
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '业务员',dataIndex:'sales',renderer:dictRenderer},
					{header: '创建时间',dataIndex:'createDate'},
					{header: '业务编号',dataIndex:'code'},
					{header: '客户',dataIndex:'customer',renderer:dictRenderer},
					{header: '货物描述',dataIndex:'itemDesc'},
					{header: '销售额',dataIndex:'totalSalesPrice'},
					{header: '销售成本',dataIndex:'totalCost'},
					{header: '完成额',dataIndex:'commission'}
				]),	
				storeMapping:[
					'sales','createDate','code', 'customer', 'itemDesc','totalSalesPrice', 'totalCost', 'commission'
				]

			},
			buttonConfig :[
			
			],
			url:ctx+'/trade',
			listUrl : '/commissionDetail'
		});
		CommissionDetail.superclass.initComponent.call(this);

	}
	
});

Commission = Ext.extend(Ext.Panel, {
	initComponent: function() {
		Ext.apply(this, {
			closable: true,
			layout: 'border',
			items: [
				new CommissionSummary({
					id: 'CommissionSummary',
					funcCode: this.funcCode,
					region: 'north', 
					height: 210, 
					split: true
				}),
				new CommissionDetail({
					id: 'CommissionDetail',
					funcCode: this.funcCode,
					region: 'center' 
				})
			]
		});
		
		Commission.superclass.initComponent.call(this);
		
		Ext.getCmp('CommissionSummary').getSelectionModel().on('rowselect',function(){
			Ext.getCmp('CommissionDetail').store.load({params:{
				monthTag : Ext.getCmp('CommissionSummary').getSelectionModel().getSelected().data.month,
				userId : Ext.getCmp('CommissionSummary').getSelectionModel().getSelected().data.salesId
			}});
		},this)
	},
	loadData: function() {
		Ext.getCmp('CommissionSummary').loadData();
	}
})
