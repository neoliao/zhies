

Export = Ext.extend(Ext.app.BaseFuncPanel,{
	loadFromGrid : false,
	initComponent : function(){
		var statusRenderer = function(v){
			var map = {
				'CREATED' : '新建业务',
				//'SUBMITED' : '已提交',
				'ASSIGNED' : '已分配操作员',
				'OPERATOR_SAVED' : '操作已保存',
				'OPERATOR_SUBMITED' : '操作已提交',
				'COST_CONFIRMED' : '应收应付已确认',
				'FINISHED' : '完成'
			};	 
			return String.format('<span >{0}</span>',map[v]);
		};
		
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '报关日期',dataIndex:'reportPortDate'},
					{header: '业务编号',dataIndex:'code'},
					{header: '客户',dataIndex:'customer',renderer:dictRenderer},
					{header: '买方',dataIndex:'buyer',renderer:dictRenderer},
					{header: '货物描述',dataIndex:'itemDesc'},
					{header: '状态',dataIndex:'status',renderer:statusRenderer},
					{header: '业务员',dataIndex:'sales',renderer:dictRenderer},
					{header: '操作员',dataIndex:'operator',renderer:dictRenderer}
				]),	
				storeMapping:[
					'reportPortDate','code','createDate','customer','buyer','itemDesc',
					'status','sales','operator'
				]
			},
			winConfig : {
				height: 420,
				width: 800
			},
			buttonConfig :[
				'all','-',{
					text : '分配操作员',
					id : 'assign-Bt',
					iconCls : 'userman',
					privilegeCode: this.funcCode + '_assign',
					scope : this,
					handler : this.showAssignWin
				},{
					text : '操作',
					id : 'operator-Bt',
					iconCls : 'pencil',
					privilegeCode: this.funcCode + '_operator',
					scope : this,
					handler : this.showOperatorWin
				},{
					text : '操作提交',
					iconCls : 'tick',
					id : 'operator_submit-Bt',
					privilegeCode: this.funcCode + '_operator_submit',
					scope : this,
					handler : this.submitToConfirm
				},{
					text : '应收应付提交',
					iconCls : 'tick',
					id : 'accounts_submit-Bt',
					privilegeCode: this.funcCode + '_accounts_submit',
					scope : this,
					handler : this.showConfirmWin
				},{
					text : '导出出口单证',
					iconCls : 'excel',
					id : 'export_file-Bt',
					privilegeCode: this.funcCode + '_export_file',
					scope : this,
					handler : this.exportFile
				}
			],
			formConfig:{
				items: [{
					layout: 'column',border: false,
					items : [{
						columnWidth:.5,layout: 'form',border: false,
						items: [
							{ xtype: 'fieldset',title: '基本信息',items:[
								{ xtype: 'f-customer',fieldLabel: '客户',hiddenName: 'customer',id:'exportCustomer',allowBlank: false},
								{ xtype: 'f-buyer',fieldLabel: '买方',hiddenName: 'buyer',id:'exportBuyer',allowBlank: false},
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
								{ xtype: 'f-dict',fieldLabel: '结算币种',hiddenName: 'currency',kind:'currency',allowBlank: false},
								{ xtype: 'f-date',fieldLabel: '报关日期',name: 'reportPortDate'},
								{ xtype: 'compositefield',labelWidth: 20,fieldLabel: '柜号',
								    items: [
								        {xtype : 'f-text',name: 'cabNo',width: 84},
								        {xtype : 'displayfield',value: '　　柜型:'},
								        { xtype: 'f-text',name: 'cabType',width: 84}
								    ]
								},
								{xtype : 'f-text',fieldLabel: '货物描述',name: 'itemDesc'},
								{xtype : 'f-text',fieldLabel: '货物大概数量',name: 'itemQuantity'}
								
							]}
							
						]
					},{
						columnWidth:.5,layout: 'form',border: false,style : 'padding-left : 20px;',
						items : [
							{ xtype: 'fieldset',title: '服务内容',items:[
								{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'A.报关服务',name:'checkedBusiness_A',width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_A'},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_A'}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'B.单证制作',name:'checkedBusiness_B',width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_B'},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_B'}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'C.产地证制作',name:'checkedBusiness_C',width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_C'},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_C'}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'D.商检',name:'checkedBusiness_D',width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_D'},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_D'}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'E.拖车运输',name:'checkedBusiness_E',width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_E'},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_E'}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'F.国际运输',name:'checkedBusiness_F',width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_F'},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_F'}
								    ]
								}
							]}
						]
					}]
				}]
				
			},
			url:ctx+'/export'	
		});
		Export.superclass.initComponent.call(this);
		
		this.getSelectionModel().on('rowselect',this.changeOperateStatus,this);
		
		//this.on('beforesave',this.beforeAdd,this);
		
		this.on('afterload',this.afterLoad,this);
		
		//this.on('winshow',this.winShow,this);
		
	},
	exportFile : function(){
		location.href = this.url + '/exportFile?id='+this.selectedId;
	},
	afterLoad : function(win,form,action){
		/*var itemsData = action.result.data.itemsData;
		Ext.getCmp('exportItemsGrid').store.loadData(itemsData);
		if(this.saveType == 'update'){
			Ext.getCmp('exportCustomer').setReadOnly(true);
			var r = this.getSelectionModel().getSelected();
			if(r.data.status != 'CREATED'){
				this.saveBt.hide();
				Ext.getCmp('exportItemsGrid').getTopToolbar().hide();
			}
		}*/
	},
	changeOperateStatus: function(sm,rowIndex,record){
		
		/*'ASSIGNED' : '已分配操作员',
				'OPERATOR_SAVED' : '操作已保存',
				'OPERATOR_SUBMITED' : '操作已提交',
				'COST_CONFIRMED' : '应收应付已确认',*/
		this.getTopToolbar().items.each(function(item,index,length){
			if((item.xtype == 'button'||item.type == 'button') && (item.enableOnEmpty != true))
				item.hide();
		});
		this.addBt.show();
		
		
		if(record.data.status == 'CREATED'){
			if(record.data.sales.text == loginUser.userName){
				this.delBt.show();
				this.editBt.show();
				Ext.getCmp('assign-Bt').show();
			}
			if(loginUser.ownRole('manager')){
				Ext.getCmp('assign-Bt').show();
			}
		}
		
		if(record.data.status == 'ASSIGNED'){
			if(record.data.sales.text == loginUser.userName){
				this.editBt.show();
			}
			if(record.data.operator.text == loginUser.userName){
				Ext.getCmp('operator-Bt').show();
			}
			if(loginUser.ownRole('manager')){
				Ext.getCmp('assign-Bt').show();
			}
		}
		
		if(record.data.status == 'OPERATOR_SAVED'){
			if(record.data.sales.text == loginUser.userName){
				this.editBt.show();
			}
			if(record.data.operator.text == loginUser.userName){
				Ext.getCmp('operator-Bt').show();
				Ext.getCmp('export_file-Bt').show();
				Ext.getCmp('operator_submit-Bt').show();
			}
			if(loginUser.ownRole('manager')){
				Ext.getCmp('assign-Bt').show();
			}
		}
		
		if(record.data.status == 'OPERATOR_SUBMITED'){
			if(record.data.sales.text == loginUser.userName){
				this.editBt.show();
			}
			if(record.data.operator.text == loginUser.userName){
				Ext.getCmp('operator-Bt').show();
				Ext.getCmp('export_file-Bt').show();
			}
			if(loginUser.ownRole('manager')){
				Ext.getCmp('assign-Bt').show();
			}
			Ext.getCmp('accounts_submit-Bt').show();
		}
		
		if(record.data.status == 'COST_CONFIRMED'){
			if(record.data.operator.text == loginUser.userName){
				Ext.getCmp('export_file-Bt').show();
			}
		}
		
		if(record.data.status == 'FINISHED'){
			if(record.data.operator.text == loginUser.userName){
				Ext.getCmp('export_file-Bt').show();
			}
			if(loginUser.ownRole('manager')){
				Ext.getCmp('export_file-Bt').show();
			}
		}
		
		
	},
	showOperatorWin: function(){
		var operWin = new Ext.app.FormWindow({
			iconCls : 'key',
			winConfig : {
				height: 700,
				width: 830,
				title : '操作业务',
				desc : '操作员操作现有业务',
				bigIconClass : 'resetKeyIcon'
			},
			formConfig : {
				layout: 'anchor',border: false,autoScroll:true,
				items:[{
					layout: 'column',border: false,
					items : [{
						columnWidth:.5,layout: 'form',border: false,
						items: [
							{ xtype: 'fieldset',title: 'A.报关',items:[
								{ xtype: 'f-customsbroker',fieldLabel: '报关行',hiddenName: 'customsBroker'},
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
								}
								
							]},
							{ xtype: 'fieldset',title: 'B.单证制作',items:[
								{ xtype: 'f-verificationcompany',fieldLabel: '核销单公司',hiddenName: 'verificationCompany'},
								{ xtype: 'f-text',fieldLabel: '核销单号',name: 'verificationFormNo'},
								{ xtype: 'f-text',fieldLabel: '唛头',name: 'mark',value:'纸箱'},
								{ xtype: 'compositefield',labelWidth: 20,fieldLabel: '合同号',
								    items: [
								        {xtype : 'f-text',name: 'contractNo',width: 84},
								        {xtype : 'displayfield',value: '合同日期:'},
								        { xtype: 'f-date',name: 'contractDate',width: 84}
								    ]
								},
								{ xtype: 'compositefield',labelWidth: 20,fieldLabel: '发票号',
								    items: [
								        {xtype : 'f-text',name: 'invoiceNo',width: 84},
								        {xtype : 'displayfield',value: '发票日期:'},
								        { xtype: 'f-date',name: 'invoiceDate',width: 84}
								    ]
								},
								{ xtype: 'f-text',fieldLabel: '成交方式',name: 'tradeType',value:'FOB SHENZHEN'},
								{ xtype: 'f-text',fieldLabel: '签约地点',name: 'signCity',value:'深圳'},
								{ xtype: 'f-text',fieldLabel: '付款条件',name: 'payCondition',value:'先出后结'},
								{ xtype: 'f-text',fieldLabel: '标记唛头及备注',name: 'memos',value:'不退税'},
								{ xtype: 'f-text',fieldLabel: '报关资料联系人',name: 'taxMemos'},
								{ xtype: 'f-text',fieldLabel: '境内货源地',name: 'itemsCity',value:'深圳'}
							]},
							{ xtype: 'fieldset',title: 'C.产地证制作',items:[
								{ xtype: 'f-text',fieldLabel: '产地证号',name: 'producerNo'},
								{ xtype: 'f-number',fieldLabel: '箱数',name: 'packageNumber'},
								{ xtype: 'f-date',fieldLabel: '产地日期',name: 'produceDate'}
							]}
						]
						
					},{
						columnWidth:.5,layout: 'form',border: false,style : 'padding-left : 20px;',
						items: [
							{ xtype: 'fieldset',title: 'D.商检',items:[
								{ xtype: 'f-inspection',fieldLabel: '商检行',hiddenName: 'inspection'},
								{ xtype: 'f-text',fieldLabel: '出口口岸',name: 'loadingPortCopy',readOnly:true},
								{ xtype: 'f-text',fieldLabel: '商检运输方式',name: 'inspectionTransType',value:'汽车'}
							]},
							{ xtype: 'fieldset',title: 'E.拖车运输',items:[
	
								{ xtype: 'f-truckcompany',fieldLabel: '拖车公司',hiddenName: 'truckCompany'},
								{ xtype: 'f-dict',fieldLabel: '运输方式',hiddenName: 'transportType',kind:'transportType'},
								{ xtype: 'f-text',fieldLabel: '装载工厂',name: 'loadingFactory'},
								{ xtype: 'f-text',fieldLabel: '装载工厂地址',name: 'loadingFactoryAddr'},
								{ xtype: 'f-text',fieldLabel: '发货港口',name: 'deliverPort'},
								{ xtype: 'f-text',fieldLabel: '司机',name: 'driver'},
								{ xtype: 'f-text',fieldLabel: '司机电话',name: 'driverPhone'},
								{ xtype: 'f-text',fieldLabel: '车牌号',name: 'truckLicense'}
							]},
							{ xtype: 'fieldset',title: 'F.国际运输',items:[
								{
						            xtype: 'radiogroup',
						            fieldLabel: '国际运输方式',
						            items: [
						                {boxLabel: '海运', name: 'shipType',inputValue:'ship', checked: true},
						                {boxLabel: '空运', name: 'shipType',inputValue:'air'}
						            ],
						            listeners:{
						            	change : function(radioGroup,checkedRadio){
						            		if(checkedRadio.inputValue == 'ship'){
						            			Ext.getCmp('ship-company').show();
						            			Ext.getCmp('air-company').hide();
						            		}else{
						            			Ext.getCmp('air-company').show();
						            			Ext.getCmp('ship-company').hide();
						            		}
						            	}
						            }
						        },
						        { xtype: 'f-shipcompany',fieldLabel: '海运公司',id:'ship-company',hiddenName: 'shipCompany'},
						        { xtype: 'f-aircompany',fieldLabel: '空运公司',id:'air-company',hidden:true,hiddenName: 'airCompany'},
								{ xtype: 'f-number',fieldLabel: '体积',name: 'volume'},
								{ xtype: 'f-number',fieldLabel: '重量',name: 'weight'},
								{ xtype: 'f-text',fieldLabel: '提单号',name: 'ladingBillNo'}
							]}
						]
					}]
				},{ xtype: 'fieldset',title: '装箱及仓存',items:[
					{ xtype: 'f-text',fieldLabel: '存放期',name: 'storagePeriod',value:'三个月'},
					{ xtype: 'f-text',fieldLabel: '包装及规格',name: 'packageAndModel',value:'纸箱'},
					{ xtype: 'f-text',fieldLabel: '仓存运输工具',name: 'storageVehicle',value:'汽车'},
					{xtype : 'f-itemsgrid',id:'exportItemsGrid',style : 'padding-top : 20px;',height:200}
				]}]
			},
			buttons : [{
				text: '保存',
				scope:this,
				handler : function(){
					var grid = Ext.getCmp('exportItemsGrid');
					grid.stopEditing();
					if(grid.store.getCount() <= 0){
						App.msg("未添加任何货物信息");
						return;
					}
					grid.store.commitChanges();
					
					this.ajaxParams = {};
					
					Ext.apply(this.ajaxParams,{
						itemIds : [],
						names : [],
						models : [],
						prices : [],
						quantitys : [],
						units : [],
						packageQuantitys : [],
						unitQuantitys : [],
						unitForQuantitys : [],
						grossWeights : [],
						netWeights : [],
						unitForWeights : []
					});
					
					grid.store.each(function(record){
						this.ajaxParams['itemIds'].push(Ext.isString(record.id) ? 0 : record.id);
						this.ajaxParams['names'].push(record.data['name']);
						this.ajaxParams['models'].push(record.data['model']);
						this.ajaxParams['prices'].push(record.data['price']);
						this.ajaxParams['quantitys'].push(record.data['quantity']);
						this.ajaxParams['units'].push(record.data['unit']);
						this.ajaxParams['packageQuantitys'].push(record.data['packageQuantity']);
						this.ajaxParams['unitQuantitys'].push(record.data['unitQuantity']);
						this.ajaxParams['unitForQuantitys'].push(record.data['unitForQuantity']);
						this.ajaxParams['grossWeights'].push(record.data['grossWeight']);
						this.ajaxParams['netWeights'].push(record.data['netWeight']);
						this.ajaxParams['unitForWeights'].push(record.data['unitForWeight']);
					},this);
					
					this.ajaxParams['id'] = this.getSelectionModel().getSelected().id;
					
					if(Ext.isArray(grid.deletedIds) && grid.deletedIds.length > 0){
						this.ajaxParams['deletedItemIds'] = grid.deletedIds;
					}
					
					grid.deletedIds = [];
					
					operWin.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:this.url+'/operator',
						params: this.ajaxParams,
						scope:this,
						success:function(form, action) {
							operWin.close();
							this.store.reload({
								scope : this,
								callback : function(){
									var sm = this.getSelectionModel();
									var r = sm.getSelected();
									this.changeOperateStatus(sm,0,r);
								}
							});
			            }
			        });
				}
			}]
		});
		operWin.show();
		var r = this.getSelectionModel().getSelected();
		operWin.formPanel.getForm().load({
			url : this.url+ ( (r.data.status == 'ASSIGNED') ? '/firstLoadOperator' : '/loadOperator'),
			params : {id : this.selectedId },
            waitMsg:'加载中...',
			scope:this,
			success:function(form, action) {
				var itemsData = action.result.data.itemsData;
				Ext.getCmp('exportItemsGrid').store.loadData(itemsData);
			}
		});	
	},
	showAssignWin : function(){
		var assignWin = new Ext.app.FormWindow({
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
					assignWin.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:this.url+'/assignOperator',
						params: { id :this.getSelectionModel().getSelected().id },
						scope:this,
						success:function(form, action) {
							assignWin.close();
							this.store.reload({
								scope : this,
								callback : function(){
									var sm = this.getSelectionModel();
									var r = sm.getSelected();
									this.changeOperateStatus(sm,0,r);
								}
							});
			            }
			        });
				}
			}]
		});
		assignWin.show();
	},

	submitToConfirm : function(){
		Ext.MessageBox.confirm('提交确认',
			'您确实要提交该业务吗?提交后不能进行任何更改!',
			this.confirmSubmitToConfirm,this);
	},
	confirmSubmitToConfirm: function(btn){
		if(btn == 'yes'){
			Ext.Ajax.request({
				url: this.url+'/submitOperator' ,
				params: { id : this.getSelectionModel().getSelected().id },
				scope: this,
				success: function(response, options) {
					this.store.reload({
						scope : this,
						callback : function(){
							var sm = this.getSelectionModel();
							var r = sm.getSelected();
							this.changeOperateStatus(sm,0,r);
						}
					});
				}
	        });
		}	
	},
	showConfirmWin : function(){
		var confirmWin = new Ext.app.FormWindow({
			iconCls : 'key',
			winConfig : {
				height : 500,
				width : 695,
				title : '确认应收应付',
				desc : '总经理确认该笔业务的应收应付款项',
				bigIconClass : 'resetKeyIcon'
			},
			formConfig : {
				layout: 'column',border: false,
				items : [{
					columnWidth:.4,layout: 'form',border: false,
					items: {xtype:'f-mustgain',title : '应收款',height:300,id : 'export-mustgain'}
				},{
					columnWidth:.6,layout: 'form',border: false,
					items: {xtype:'f-mustpay',title : '应付款',height:300,id : 'export-mustpay'}
				}]
			},
			buttons : [{
				text: '确定',
				scope:this,
				handler : function(){
					this.ajaxParams = { id :this.getSelectionModel().getSelected().id };
		
					var mustPayGrid = Ext.getCmp('export-mustpay');
					var mustGainGrid = Ext.getCmp('export-mustgain');
					mustPayGrid.store.commitChanges();
					mustGainGrid.store.commitChanges();
					
					Ext.apply(this.ajaxParams,{
						businessInstanceIds : [],
						companyIds : [],
						mustPayAmounts : [],
						mustGainAmounts : []
					});
					
					mustPayGrid.store.each(function(record){
						this.ajaxParams['businessInstanceIds'].push(record.id);
						this.ajaxParams['companyIds'].push(record.data.company.id);
						this.ajaxParams['mustPayAmounts'].push(record.data['amount']);
					},this);
					
					mustGainGrid.store.each(function(record){
						this.ajaxParams['mustGainAmounts'].push(record.data['amount']);
					},this);
					
					Ext.Ajax.request({
						url:this.url+'/confirmCost',
						params: this.ajaxParams,
						scope:this,
						success:function(form, action) {
							confirmWin.close();
							this.store.reload({
								scope : this,
								callback : function(){
									var sm = this.getSelectionModel();
									var r = sm.getSelected();
									this.changeOperateStatus(sm,0,r);
								}
							});
			            }
			        });
				}
			}]
		});
		confirmWin.show();
		
		Ext.getCmp('export-mustgain').store.load({params:{id : this.getSelectionModel().getSelected().id }});
		Ext.getCmp('export-mustpay').store.load({params:{id : this.getSelectionModel().getSelected().id }});
	}
	
});