
Invoice = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '发票抬头',dataIndex:'title'},
					{header: '金额',dataIndex:'amount'},
					{header: '发票号',dataIndex:'billNo'},
					{header: '日期',dataIndex:'date'}
				]),	
				storeMapping:[
					'title','amount','billNo','date'
				]
			},
			winConfig : {
				height: 330
			},
			formConfig:{
				items: [
					{xtype: 'f-text',fieldLabel: '发票抬头',name: 'title',allowBlank: false},
					{xtype: 'f-number',fieldLabel: '金额',name: 'amount',allowBlank: false},
					{xtype: 'f-text',fieldLabel: '发票号',name: 'billNo'},
					{xtype: 'f-date',fieldLabel: '日期',name: 'date'}
 
				]
			},
			url:ctx+'/invoice'	
		});
		Invoice.superclass.initComponent.call(this);
	}
	
});
