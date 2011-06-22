ItemsGrid = Ext.extend(Ext.grid.EditorGridPanel,{
    clicksToEdit:1,
    title : '货物列表',
    style : 'padding-left : 20px;',
    //height : 400,
    initComponent : function(){
    	
    	this.FieldItem = Ext.data.Record.create([
           {name: 'name'},
           {name: 'model'},
           {name: 'price'},
           {name: 'quantity'},
           {name: 'unit'}
		]);
		
		Ext.apply(this,{
			store : new Ext.data.JsonStore({
				root: 'root',
		        fields: this.FieldItem,
		        data : {root : []}
			}),
    		cm: new Ext.grid.ColumnModel([{
	        	header: "名称",dataIndex: 'name',width: 150,
	        	editor: new Ext.app.TextField({allowBlank: false})
	        },{
	        	header: "型号及描述",dataIndex: 'model',width: 150,
	        	editor: new Ext.app.TextField({allowBlank: false})
	        },
	        {
	        	header: "单价",dataIndex: 'price',width: 90,
	        	editor: new Ext.form.NumberField()
	        },{
	        	header: "数量",dataIndex: 'quantity',width: 90,
	        	editor: new Ext.form.NumberField()
	        },{
	        	header: "单位",dataIndex: 'unit',width: 80,
	        	editor: new Ext.form.TextField()
	        }]),
	        tbar: [{
	            text: '新增',
	            iconCls : 'add',
	            scope : this,
	            handler : this.add
	        },
	        {
	            text: '删除',
	            iconCls : 'remove',
	            scope : this,
	            handler : this.del
	        }]
		});
		ItemsGrid.superclass.initComponent.call(this);
	},
	add : function(){
        var item = new this.FieldItem({
        	unit:'KG'
        });
        this.stopEditing();
        var count = this.store.getCount();
        this.store.insert(count, item);
        this.startEditing(count, 0);
    },
    del : function(){
    	this.stopEditing();
    	var cell = this.getSelectionModel().getSelectedCell();
    	if(cell){
    		var rowIndex = this.getSelectionModel().getSelectedCell()[0];
        	this.store.removeAt(rowIndex);
    	}else{
    		App.msg("没有可用的数据，请选中一行");
    	}
       
    }
	
});

Ext.reg('f-itemsgrid', ItemsGrid);


Export = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		var statusRenderer = function(v){
			var map = {
				'CREATED' : '新建业务',
				'SUBMITED' : '已提交',
				'ASSIGNED' : '已分配操作员',
				'FINISHED' : '完成'
			}	 
			return String.format('<span >{0}</span>',map[v]);
		}
		
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '日期',dataIndex:'createDate'},
					{header: '客户',dataIndex:'customer',renderer:dictRenderer},
					{header: '货物描述',dataIndex:'itemDesc'},
					{header: '状态',dataIndex:'status',renderer:statusRenderer},
					{header: '业务员',dataIndex:'sales',renderer:dictRenderer},
					{header: '操作员',dataIndex:'operator',renderer:dictRenderer}
				]),	
				storeMapping:[
					'createDate','customer','itemDesc','status','sales','operator'
				]
			},
			winConfig : {
				height: 680,
				width: 1000
			},
			buttonConfig :[
				'all','-',{
					text : '提交',
					id : 'CREATED-Bt',
					scope : this,
					handler : this.submitToAssign
				},{
					text : '分配操作员',
					id : 'SUBMITED-Bt',
					scope : this,
					handler : this.showAssignWin
				},{
					text : '操作',
					id : 'ASSIGNED-Bt',
					scope : this,
					handler : this.showOperatorWin
				}
			],
			formConfig:{
				items: [{
					layout: 'column',border: false,
					items : [{
						columnWidth:.4,layout: 'form',border: false,
						items: [
							{ xtype: 'fieldset',title: '基本信息',items:[
								{ xtype: 'f-customer',fieldLabel: '客户',hiddenName: 'customer',allowBlank: false},
								{ xtype: 'compositefield',labelWidth: 20,fieldLabel: '出口地',
								    items: [
								        {xtype : 'f-text',name: 'loadingCity',value:'深圳',width: 84},
								        {xtype : 'displayfield',value: '出口口岸:'},
								        { xtype: 'f-text',name: 'loadingPort',width: 84}
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,fieldLabel: '目的地',
								    items: [
								        {xtype : 'f-text',name: 'destination',width: 84},
								        {xtype : 'displayfield',value: '目的港口:'},
								        { xtype: 'f-text',name: 'destinationPort',width: 84}
								    ]
								},
								{ xtype: 'f-dict',fieldLabel: '结算币种',hiddenName: 'currency',kind:'currency'},
								{ xtype: 'f-date',fieldLabel: '报关日期',name: 'reportPortDate'},
								{ xtype: 'compositefield',labelWidth: 20,fieldLabel: '柜号',
								    items: [
								        {xtype : 'f-text',name: 'cabNo',width: 84},
								        {xtype : 'displayfield',value: '　　柜型:'},
								        { xtype: 'f-text',name: 'cabType',width: 84}
								    ]
								},
								{ xtype: 'f-verificationcompany',fieldLabel: '核销单公司',hiddenName: 'verificationCompany'},
								{ xtype: 'f-text',fieldLabel: '核销单号',name: 'verificationFormNo'}
							]},
							{ xtype: 'fieldset',title: '服务内容',items:[
								{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'A.报关服务',name:'checkedBusiness-A',width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-text',width: 60,name:'cost-A'},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'textfield',width: 60,name:'salesPrice-A'}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'B.单证制作',name:'checkedBusiness-B',width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'textfield',width: 60,name:'cost-B'},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'textfield',width: 60,name:'salesPrice-B'}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'C.产地证制作',name:'checkedBusiness-C',width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'textfield',width: 60,name:'cost-C'},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'textfield',width: 60,name:'salesPrice-C'}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'D.商检',name:'checkedBusiness-D',width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'textfield',width: 60,name:'cost-D'},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'textfield',width: 60,name:'salesPrice-D'}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'E.拖车运输',name:'checkedBusiness-E',width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'textfield',width: 60,name:'cost-E'},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'textfield',width: 60,name:'salesPrice-E'}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'F.国际运输',name:'checkedBusiness-F',width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'textfield',width: 60,name:'cost-F'},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'textfield',width: 60,name:'salesPrice-F'}
								    ]
								}
							]}
							
						]
					},{
						columnWidth:.6,layout: 'fit',border: false,height : 380,
						items : [
							{xtype : 'f-itemsgrid',id:'itemsgrid'}
						]
					}]
				}]
				
			},
			url:ctx+'/export'	
		});
		Export.superclass.initComponent.call(this);
		
		this.getSelectionModel().on('rowselect',this.changeOperateStatus,this);
		
		this.on('beforesave',this.beforeAdd,this);
	},
	changeOperateStatus: function(sm,rowIndex,record){
		
		this.getTopToolbar().items.each(function(item,index,length){
			if((item.xtype == 'button'||item.type == 'button') && (item.enableOnEmpty != true))
				item.hide();
		});
		this.editBt.show();
		
		Ext.getCmp(record.data.status+'-Bt').show();
		
		if(record.data.status == 'CREATED'){
			this.delBt.show();
		}
		
		
	},
	showOperatorWin: function(){
		this.assignWin = new Ext.app.FormWindow({
			iconCls : 'key',
			winConfig : {
				height: 700,
				width: 800,
				title : '操作业务',
				desc : '操作员操作现有业务',
				bigIconClass : 'resetKeyIcon'
			},
			formConfig : {
				layout: 'column',border: false,
				items : [{
					columnWidth:.5,layout: 'form',border: false,
					items: [
						{ xtype: 'fieldset',title: 'A.报关,B.单证制作',items:[
							{ xtype: 'f-customsbroker',fieldLabel: '报关行',hiddenName: 'customsBroker',allowBlank: false},
							{ xtype: 'f-text',fieldLabel: '装运口岸',name: 'loadingPort'},
							{ xtype: 'f-text',fieldLabel: '目的地',name: 'destination'},
							{ xtype: 'f-text',fieldLabel: '唛头',name: 'mark'},
							{ xtype: 'f-text',fieldLabel: '合同号',name: 'contractNo'},
							{ xtype: 'f-date',fieldLabel: '合同日期',name: 'contractDate'},
							{ xtype: 'f-text',fieldLabel: '发票号',name: 'invoiceNo'},
							{ xtype: 'f-date',fieldLabel: '发票日期',name: 'invoiceDate'},
							{ xtype: 'f-text',fieldLabel: '成交方式',name: 'tradeType',value:'FOB'},
							{ xtype: 'f-text',fieldLabel: '毛重KG',name: 'grossWeight'},
							{ xtype: 'f-text',fieldLabel: '净重KG',name: 'netWeight'}
						]},
						{ xtype: 'fieldset',title: 'C.产地证制作',items:[
							{ xtype: 'f-text',fieldLabel: '产地证号',name: 'producerNo'},
							{ xtype: 'f-text',fieldLabel: '箱数',name: 'packageNumber'},
							{ xtype: 'f-date',fieldLabel: '产地日期',name: 'produceDate'}
						]}
					]
					
				},{
					columnWidth:.5,layout: 'form',border: false,style : 'padding-left : 20px;',
					items: [
						{ xtype: 'fieldset',title: 'D.商检',items:[
							{ xtype: 'f-inspection',fieldLabel: '商检行',hiddenName: 'inspection',allowBlank: false},
							{ xtype: 'f-text',fieldLabel: '出口口岸',name: 'exportPort'},
							{ xtype: 'f-text',fieldLabel: '商检运输方式',name: 'inspectionTransType'}
						]},
						{ xtype: 'fieldset',title: 'E.拖车运输',items:[

							{ xtype: 'f-truckcompany',fieldLabel: '拖车公司',hiddenName: 'truckCompany',allowBlank: false},
							{ xtype: 'f-dict',fieldLabel: '运输方式',hiddenName: 'transportType',kind:'transportType'},
							{ xtype: 'f-text',fieldLabel: '装载工厂',name: 'loadingFactory'},
							{ xtype: 'f-text',fieldLabel: '装载工厂地址',name: 'loadingFactoryAddr'},
							{ xtype: 'f-text',fieldLabel: '发货港口',name: 'deliverPort'},
							{ xtype: 'f-text',fieldLabel: '司机',name: 'driver'},
							{ xtype: 'f-text',fieldLabel: '司机电话',name: 'driverPhone'},
							{ xtype: 'f-text',fieldLabel: '车牌号',name: 'truckLicense'}
						]},
						{ xtype: 'fieldset',title: 'F.国际运输',items:[
							{ xtype: 'f-dict',fieldLabel: '国际运输方式',hiddenName: 'shipType',kind:'shipType'},
							{ xtype: 'f-text',fieldLabel: '目的港',name: 'destinitionPort'},
							{ xtype: 'f-text',fieldLabel: '体积',name: 'volume'},
							{ xtype: 'f-text',fieldLabel: '重量',name: 'weight'},
							{ xtype: 'f-text',fieldLabel: '提单号',name: 'ladingBillNo'}
						]}
					]
					
				}]
			},
			buttons : [{
				text: '确定',
				scope:this,
				handler : function(){
					this.assignWin.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:this.url+'/operator',
						params: { id :this.getSelectionModel().getSelected().id },
						scope:this,
						success:function(form, action) {
							this.assignWin.close();
							this.loadData();
							var sm = this.getSelectionModel();
							this.changeOperateStatus(sm,rowIndex,sm.getSelected());
			            }
			        });
				}
			}]
		});
		this.assignWin.show();
	},
	showAssignWin : function(){
		this.assignWin = new Ext.app.FormWindow({
			iconCls : 'key',
			winConfig : {
				height : 210,
				width : 395,
				title : '分配操作员',
				desc : '将现有业务分配给特定操作员',
				bigIconClass : 'resetKeyIcon'
			},
			formConfig : {
				items : [
		 			{xtype: 'f-operator',fieldLabel: '操作员',hiddenName: 'operator',allowBlank: false}
				]
			},
			buttons : [{
				text: '确定',
				scope:this,
				handler : function(){
					this.assignWin.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:this.url+'/assignOperator',
						params: { id :this.getSelectionModel().getSelected().id },
						scope:this,
						success:function(form, action) {
							this.assignWin.close();
							this.loadData();
							var sm = this.getSelectionModel();
							this.changeOperateStatus(sm,rowIndex,sm.getSelected());
			            }
			        });
				}
			}]
		});
		this.assignWin.show();
	},
	submitToAssign : function(){
		Ext.MessageBox.confirm('提交确认','您确实要提交该业务吗?提交后不能进行任何更改!',this.confirmSubmitToAssign,this);
	},
	confirmSubmitToAssign: function(){
		Ext.Ajax.request({
			url: this.url+'/submitToAssign' ,
			params: { id : this.getSelectionModel().getSelected().id },
			scope: this,
			success: function(response, options) {
				this.loadData();
			}
        });

	},
	beforeAdd : function(){
		var grid = Ext.getCmp('itemsgrid');
		grid.stopEditing();
		if(grid.store.getCount() <= 0){
			App.msg("未添加任何字段");
			return;
		}
		grid.store.commitChanges();
		
		Ext.apply(this.ajaxParams,{
			names : [],
			models : [],
			prices : [],
			quantitys : [],
			units : []
		});

		grid.store.each(function(record){
			this.ajaxParams['names'].push(record.data['name']);
			this.ajaxParams['models'].push(record.data['model']);
			this.ajaxParams['prices'].push(record.data['price']);
			this.ajaxParams['quantitys'].push(record.data['quantity']);
			this.ajaxParams['units'].push(record.data['unit']);
		},this);
		
	}
	
});
