
ShipCompany = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '公司名',dataIndex:'name'},
					{header: '地址',dataIndex:'address'},
					{header: '电子邮件',dataIndex:'email'},
					{header: '电话',dataIndex:'tel'},
					{header: '传真',dataIndex:'fax'},
					{header: 'QQ',dataIndex:'qq'},
					{header: '联系人',dataIndex:'linkman'},
					{header: '联系人电话',dataIndex:'linkmanTel'},
					{header: '联系人邮件',dataIndex:'linkmanEmail'}
				]),	
				storeMapping:[
					'name','address','email','tel','fax','qq','linkman','linkmanTel','linkmanEmail'
				]
			},
			winConfig : {
				height: 330
			},
			formConfig:{
				items: [
					{xtype: 'f-text',fieldLabel: '公司名',name: 'name',allowBlank: false},
					{xtype: 'f-text',fieldLabel: '地址',name: 'address'},
					{xtype: 'f-text',fieldLabel: '电子邮件',name: 'email'},
					{xtype: 'f-text',fieldLabel: '电话',name: 'tel'},
					{xtype: 'f-text',fieldLabel: '传真',name: 'fax'},
					{xtype: 'f-text',fieldLabel: 'QQ',name: 'qq'},
					{xtype: 'f-text',fieldLabel: '联系人',name: 'linkman'},
					{xtype: 'f-text',fieldLabel: '联系人电话',name: 'linkmanTel'},
					{xtype: 'f-text',fieldLabel: '联系人邮件',name: 'linkmanEmail'}
 
				]
			},
			url:ctx+'/shipCompany'	
		});
		ShipCompany.superclass.initComponent.call(this);
	}
	
});
