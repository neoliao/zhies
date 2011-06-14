
Export = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '创建日期',dataIndex:'createDate'}
				]),	
				storeMapping:[
					'createDate'
				]
			},
			winConfig : {
				height: 330
			},
			formConfig:{
				items: [
					{xtype: 'f-text',fieldLabel: '创建日期',name: 'createDate',allowBlank: false}
 
				]
			},
			url:ctx+'/export'	
		});
		Export.superclass.initComponent.call(this);
	}
	
});
