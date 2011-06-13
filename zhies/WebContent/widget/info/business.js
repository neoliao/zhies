
Business = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '服务代码',dataIndex:'code'},
					{header: '服务名称',dataIndex:'name'},
					{header: '成本价格',dataIndex:'cost'}
				]),	
				storeMapping:[
					'code','name','cost'
				]
			},
			winConfig : {
				height: 330
			},
			formConfig:{
				items: [
					{xtype: 'f-text',fieldLabel: '服务代码',name: 'code'},
					{xtype: 'f-text',fieldLabel: '服务名称',name: 'name'},
					{xtype: 'f-text',fieldLabel: '成本价格',name: 'cost'}
 
				]
			},
			url:ctx+'/business'	
		});
		Business.superclass.initComponent.call(this);
	}
	
});
