

Export = Ext.extend(Ext.app.BaseFuncPanel,{
	loadFromGrid : false,
	initComponent : function(){
		var statusRenderer = function(v){
			var map = {
				'CREATED' : { t: '新建业务',c:'green'},
				'ASSIGNED' : { t: '已分配操作员',c:'green'},
				'OPERATOR_SAVED' : { t: '操作已保存',c:'green'},
				'OPERATOR_SUBMITED': { t:  '操作已提交',c:'red'},
				'COST_CONFIRMED' : { t: '应收应付已确认',c:'red'},
				'FINISHED' :{ t:  '完成',c:'red'}
			};	 
			return String.format('<span style="color:{0}">{1}</span>',map[v].c,map[v].t);
		};
		
		var checkConfirm = {
			blur : function(f){
				var v = f.getValue();
				var code = f.id.split('_')[2];
				var checkBox = Ext.getCmp('export_checkedBusiness_'+code);
				if((v || v === 0) && !checkBox.getValue()){
					checkBox.setValue(true);
				}
			}
		}
		
		var checkValidate = {
			check : function(f){
				var checked = f.getValue();
				var code = f.id.split('_')[2];
				if(!checked){
					Ext.getCmp('export_cost_'+code).reset();
					Ext.getCmp('export_salesPrice_'+code).reset();
				}
			}
		}
		
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '业务建立时间',dataIndex:'createDate'},
					{header: '报关日期',dataIndex:'reportPortDate'},
					{header: '业务编号',dataIndex:'code'},
					{header: '客户',dataIndex:'customer',renderer:dictRenderer},
					{header: '口岸',dataIndex:'loadingPort',renderer:dictRenderer},
					{header: '货物描述',dataIndex:'itemDesc'},
					{header: '大概数量',dataIndex:'itemQuantity'},
					{header: '状态',dataIndex:'status',renderer:statusRenderer},
					{header: '业务员',dataIndex:'sales',renderer:dictRenderer},
					{header: '操作员',dataIndex:'operator',renderer:dictRenderer}
				]),	
				storeMapping:[
					'reportPortDate','code','createDate','customer','buyer','itemDesc','buyerName','loadingPort',
					'status','sales','operator','itemQuantity','producerCertificate','memo','otherPrice'
				]
			},
			winConfig : {
				height: 540,
				width: 800,
				title : '业务新增或修改',
				desc : '业务员新增或者修改业务内容',
				bigIconClass : 'trade'
			},
			buttonConfig :[
				'all','-',{
					text : '分配操作员',
					id : 'export-assign-Bt',
					iconCls : 'userman',
					privilegeCode: this.funcCode + '_assign',
					scope : this,
					handler : this.showAssignWin
				},{
					text : '操作',
					id : 'export-operator-Bt',
					iconCls : 'wizard',
					privilegeCode: this.funcCode + '_operator',
					scope : this,
					handler : this.showOperatorWin
				},{
					text : '操作提交',
					iconCls : 'tick',
					id : 'export-operator_submit-Bt',
					privilegeCode: this.funcCode + '_operator_submit',
					scope : this,
					handler : this.submitToConfirm
				},{
					text : '应收应付提交',
					iconCls : 'tick',
					id : 'export-accounts_submit-Bt',
					privilegeCode: this.funcCode + '_accounts_submit',
					scope : this,
					handler : this.showConfirmWin
				},{
					text : '导出单证',
					iconCls : 'excel',
					id : 'export-export_file-Bt',
					privilegeCode: this.funcCode + '_export_file',
					scope : this,
					handler : this.exportFile
				},{
					text : '查看操作',
					iconCls : 'view',
					id : 'export-viewOperator-Bt',
					scope : this,
					handler : this.viewOperator
				},{
					text : '查看业务',
					iconCls : 'view',
					id : 'export-viewTrade-Bt',
					scope : this,
					handler : this.viewTrade
				},'->',{
					xtype : 'f-search',
					emptyText : '业务编号,客户名称,货物描述,口岸...'
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
								//{ xtype: 'f-buyer',fieldLabel: '买方',hiddenName: 'buyer',id:'exportBuyer'},
								{ xtype: 'f-text',fieldLabel: '买方',name: 'buyerName',id:'exportBuyer'},
								{ xtype: 'compositefield',labelWidth: 20,fieldLabel: '出口地',
								    items: [
								        {xtype : 'f-text',name: 'loadingCity',value:'深圳',width: 84},
								        {xtype : 'displayfield',value: '出口口岸:'},
								        { xtype: 'f-dict',hiddenName: 'loadingPort',width: 84,kind:'port'}
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
								{ xtype: 'f-text',fieldLabel: '柜号',name: 'cabNo'},
								{ xtype: 'f-text',fieldLabel: '柜型',name: 'cabType'},
								{ xtype: 'f-text',fieldLabel: 'SO号码',name: 'soNo'},
								{xtype : 'f-text',fieldLabel: '货物描述',name: 'itemDesc',allowBlank: false},
								{xtype : 'f-text',fieldLabel: '货物大概数量',name: 'itemQuantity',allowBlank: false},
								{ xtype: 'f-textarea',fieldLabel: '备注',name: 'memo'}
								
							]}
							
						]
					},{
						columnWidth:.5,layout: 'form',border: false,style : 'padding-left : 20px;',
						items : [
							{ xtype: 'fieldset',title: '服务内容',items:[
								{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'A.报关服务',name:'checkedBusiness_A',id:'export_checkedBusiness_A',listeners:checkValidate,width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_A',id:'export_cost_A',listeners:checkConfirm},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_A',id:'export_salesPrice_A',listeners:checkConfirm}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'B.单证制作',name:'checkedBusiness_B',id:'export_checkedBusiness_B',listeners:checkValidate,width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_B',id:'export_cost_B',listeners:checkConfirm},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_B',id:'export_salesPrice_B',listeners:checkConfirm}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'C.产地证制作',name:'checkedBusiness_C',id:'export_checkedBusiness_C',listeners:checkValidate,width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_C',id:'export_cost_C',listeners:checkConfirm},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_C',id:'export_salesPrice_C',listeners:checkConfirm}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'D.商检',name:'checkedBusiness_D',id:'export_checkedBusiness_D',listeners:checkValidate,width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_D',id:'export_cost_D',listeners:checkConfirm},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_D',id:'export_salesPrice_D',listeners:checkConfirm}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'E.拖车运输',name:'checkedBusiness_E',id:'export_checkedBusiness_E',listeners:checkValidate,width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_E',id:'export_cost_E',listeners:checkConfirm},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_E',id:'export_salesPrice_E',listeners:checkConfirm}
								        
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'F.国际运输',name:'checkedBusiness_F',id:'export_checkedBusiness_F',listeners:checkValidate,width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_F',id:'export_cost_F',listeners:checkConfirm},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_F',id:'export_salesPrice_F',listeners:checkConfirm}
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'G.港建费',name:'checkedBusiness_G',id:'export_checkedBusiness_G',listeners:checkValidate,width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_G',id:'export_cost_G',listeners:checkConfirm},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_G',id:'export_salesPrice_G',listeners:checkConfirm}
								    ]
								},{ xtype: 'compositefield',labelWidth: 20,
								    items: [
								        {xtype : 'checkbox',fieldLabel: 'Z.其它费用',name:'checkedBusiness_Z',id:'export_checkedBusiness_Z',listeners:checkValidate,width: 20},
								        {xtype : 'displayfield',value: '成本价'},
								        {xtype : 'f-number',width: 60,name:'cost_Z',id:'export_cost_Z',listeners:checkConfirm},
								        {xtype : 'displayfield',value: '销售价'},
								        {xtype : 'f-number',width: 60,name:'salesPrice_Z',id:'export_salesPrice_Z',listeners:checkConfirm}
								    ]
								},{xtype : 'f-textarea',name:'otherPrice',fieldLabel: '其它费用内容'}
							]}
						]
					}]
				}]
				
			},
			url:ctx+'/export'	
		});
		Export.superclass.initComponent.call(this);
		
		this.getSelectionModel().on('rowselect',this.changeOperateStatus,this);
		
		this.on('afterload',this.afterLoad,this);
		
	},
	viewTrade : function(){
		this.edit();
		this.saveBt.hide();
	},
	viewOperator : function(){
		this.showOperatorWin();
		Ext.getCmp('export_operator_save-Bt').hide();
		Ext.getCmp('copyOperator-Bt').hide();
		Ext.getCmp('exportItemsGrid').getTopToolbar().hide();
	},
	exportFile : function(){
		location.href = this.url + '/exportFilePoi?id='+this.selectedId;
	},
	changeOperateStatus: function(sm,rowIndex,record){
		
		this.getTopToolbar().items.each(function(item,index,length){
			if((item.xtype == 'button'||item.type == 'button') && (item.enableOnEmpty != true))
				item.hide();
		});
		
		this.addBt.show();
		
		if(loginUser.ownRole('manager') || loginUser.ownRole('financials')){
			Ext.getCmp('export-viewTrade-Bt').show();
		}
		
		if(record.data.status == 'CREATED'){
			if(record.data.sales.text == loginUser.userName){
				this.delBt.show();
				this.editBt.show();
				Ext.getCmp('export-assign-Bt').show();
			}
			if(loginUser.ownRole('manager') || loginUser.ownRole('financials')){
				Ext.getCmp('export-assign-Bt').show();
				this.delBt.show();
				this.editBt.show();
			}
		}
		
		if(record.data.status == 'ASSIGNED'){
			if(record.data.sales.text == loginUser.userName){
				this.editBt.show();
			}
			if(record.data.operator.text == loginUser.userName){
				Ext.getCmp('export-operator-Bt').show();
			}
			if(loginUser.ownRole('manager') || loginUser.ownRole('financials')){
				Ext.getCmp('export-assign-Bt').show();
				this.delBt.show();
				this.editBt.show();
			}
		}
		
		if(record.data.status == 'OPERATOR_SAVED'){
			if(record.data.sales.text == loginUser.userName){
				this.editBt.show();
			}
			if(record.data.operator.text == loginUser.userName){
				Ext.getCmp('export-operator-Bt').show();
				Ext.getCmp('export-export_file-Bt').show();
				Ext.getCmp('export-operator_submit-Bt').show();
			}
			if(loginUser.ownRole('manager') || loginUser.ownRole('financials')){
				Ext.getCmp('export-assign-Bt').show();
				this.delBt.show();
				this.editBt.show();
			}
		}
		
		if(record.data.status == 'OPERATOR_SUBMITED'){
			if(record.data.sales.text == loginUser.userName){
				this.editBt.show();
			}
			if(record.data.operator.text == loginUser.userName){
				Ext.getCmp('export-operator-Bt').show();
				Ext.getCmp('export-export_file-Bt').show();
			}
			if(loginUser.ownRole('manager') || loginUser.ownRole('financials')){
				Ext.getCmp('export-assign-Bt').show();
				Ext.getCmp('export-accounts_submit-Bt').show();
				this.delBt.show();
				this.editBt.show();
			}
			
		}
		
		if(record.data.status == 'COST_CONFIRMED'){
			if(record.data.operator.text == loginUser.userName){
				Ext.getCmp('export-export_file-Bt').show();
				Ext.getCmp('export-viewTrade-Bt').show();
				Ext.getCmp('export-viewOperator-Bt').show();
			}
			if(loginUser.ownRole('manager') || loginUser.ownRole('financials')){
				Ext.getCmp('export-export_file-Bt').show();
				Ext.getCmp('export-viewTrade-Bt').show();
				Ext.getCmp('export-viewOperator-Bt').show();
			}
		}
		
		if(record.data.status == 'FINISHED'){
			if(record.data.operator.text == loginUser.userName){
				Ext.getCmp('export-export_file-Bt').show();
				Ext.getCmp('export-viewTrade-Bt').show();
				Ext.getCmp('export-viewOperator-Bt').show();
			}
			if(loginUser.ownRole('manager') || loginUser.ownRole('financials')){
				Ext.getCmp('export-export_file-Bt').show();
				Ext.getCmp('export-viewTrade-Bt').show();
				Ext.getCmp('export-viewOperator-Bt').show();
			}
		}
	},
	showOperatorWin: function(){
		var operWin = new Ext.app.FormWindow({
			iconCls : 'anchor',
			id : 'export_operator_win',
			winConfig : {
				height: 650,
				width: 850,
				title : '操作业务',
				desc : '操作员操作现有业务,填写与业务相关的各种资料',
				bigIconClass : 'trade'
			},
			formConfig : {
				layout: 'anchor',border: false,autoScroll:true,
				items:[{
					layout: 'column',border: false,
					items : [{
						columnWidth:.5,layout: 'form',border: false,
						items: [
							{ xtype: 'fieldset',title: 'A.报关',items:[
								{ xtype: 'f-text',fieldLabel: '买方',name: 'buyerName'},
								{ xtype: 'f-customsbroker',fieldLabel: '报关行',hiddenName: 'customsBroker'},
								{ xtype: 'compositefield',labelWidth: 20,fieldLabel: '出口地',
								    items: [
								        {xtype : 'f-text',name: 'loadingCity',value:'深圳',width: 84},
								        {xtype : 'displayfield',value: '出口口岸:'},
								        {xtype : 'f-dict',hiddenName: 'loadingPort',width: 84,kind:'port'}
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
								{ xtype: 'f-text',fieldLabel: '柜号',name: 'cabNo'},
								{ xtype: 'f-text',fieldLabel: '柜型',name: 'cabType'},
								{xtype : 'f-text',fieldLabel: 'SO号码',name: 'soNo'}
								
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
								{ xtype: 'f-textarea',fieldLabel: '标记唛头及备注',name: 'memos',value:'不退税'},
								//{ xtype: 'f-text',fieldLabel: '报关资料联系人',name: 'taxMemos'},
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
								{ xtype: 'f-dict',fieldLabel: '口岸',hiddenName: 'loadingPortCopy',width: 84,kind:'port',readOnly:true},
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
							]},
							{ xtype: 'fieldset',title: '装箱及仓存',items:[
								{ xtype: 'f-text',fieldLabel: '存放期',name: 'storagePeriod',value:'三个月'},
								{ xtype: 'f-text',fieldLabel: '包装及规格',name: 'packageAndModel',value:'纸箱'},
								{ xtype: 'f-text',fieldLabel: '仓存运输工具',name: 'storageVehicle',value:'汽车'}
							]},
							{ xtype: 'fieldset',title: '备注',items:[
								{ xtype: 'f-textarea',fieldLabel: '备注',name: 'memo'}
							]}
							
						]
					}]
				},{ xtype: 'fieldset',title: '货物清单',items:[
						{xtype : 'checkbox',fieldLabel: '以总价计算单价',name:'caluateAsTotalPrice'},
						{xtype : 'f-itemsgrid',id:'exportItemsGrid',style : 'padding-top : 5px;',height:400}
					]
				}]
			},
			buttons : [{
				text: '保存',
				id : 'export_operator_save-Bt',
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
						productCodes : [],
						models : [],
						prices : [],
						quantitys : [],
						units : [],
						totalPrices : [],
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
						this.ajaxParams['productCodes'].push(record.data['productCode']);
						this.ajaxParams['models'].push(record.data['model']);
						this.ajaxParams['prices'].push(record.data['price']);
						this.ajaxParams['quantitys'].push(record.data['quantity']);
						this.ajaxParams['units'].push(record.data['unit']);
						this.ajaxParams['totalPrices'].push(record.data['totalPrice']);
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
			}
			,{
				text : '从其它业务复制',
				id : 'copyOperator-Bt',
				iconCls : 'addPage',
				//privilegeCode: this.funcCode + '_add',
				scope : this,
				handler : this.showCopyWin
			}
			]
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
		return operWin;
	},
	showCopyWin : function(){
		var assignWin = new Ext.app.FormWindow({
			iconCls : 'anchor',
			winConfig : {
				height : 280,
				width : 395,
				title : '复制业务',
				desc : '复制已有业务数据到新的业务,复制的数据将覆盖已有数据',
				bigIconClass : 'trade'
			},
			formConfig : {
				items : [
					{ xtype: 'f-exportselect',fieldLabel: '已有业务',hiddenName: 'fromExport',id:'exportOperatorSelect',toTrade:this.selectedId,allowBlank: false},
					{ xtype: 'f-text',fieldLabel: '客户',name: 'customerName',id:'copyOperator-customerName',readOnly:true},
					//{ xtype: 'f-text',fieldLabel: '买方',name: 'buyerName',id:'copyOperator-buyerName',readOnly:true},
					{xtype : 'f-text',fieldLabel: '货物描述',name: 'itemDesc',id:'copyOperator-itemDesc',readOnly:true},
					{xtype : 'f-text',fieldLabel: '货物大概数量',name: 'itemQuantity',id:'copyOperator-itemQuantity',readOnly:true}
				]
			},
			buttons : [{
				text: '从该业务复制',
				scope:this,
				handler : function(){
					assignWin.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:this.url+'/copy',
						params: { id : this.selectedId },
						scope:this,
						success:function(form, action) {
							assignWin.close();
							this.store.reload({
								scope : this,
								callback : function(){
									Ext.getCmp('export_operator_win').formPanel.getForm().load({
										url : this.url+ '/loadOperator',
										params : { id : this.selectedId },
							            waitMsg:'加载中...',
										scope:this,
										success:function(form, action) {
											
											var itemsData = action.result.data.itemsData;
											Ext.getCmp('exportItemsGrid').store.loadData(itemsData);
										}
									});	
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
		//assignWin.formPanel.getForm().loadRecord(this.getSelectionModel().getSelected());
		Ext.getCmp('exportOperatorSelect').on('valueselect',function(selector,record){
			Ext.getCmp('copyOperator-customerName').setValue(record.data.customerName);
			//Ext.getCmp('copyOperator-buyerName').setValue(record.data.buyerName);
			Ext.getCmp('copyOperator-itemDesc').setValue(record.data.itemDesc);
			Ext.getCmp('copyOperator-itemQuantity').setValue(record.data.itemQuantity);
		},this)
	},
	showAssignWin : function(){
		var assignWin = new Ext.app.FormWindow({
			iconCls : 'anchor',
			winConfig : {
				height : 210,
				width : 395,
				title : '分配操作员',
				desc : '将现有业务分配给特定操作员',
				bigIconClass : 'trade'
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
			iconCls : 'anchor',
			winConfig : {
				height : 500,
				width : 695,
				title : '确认应收应付',
				desc : '总经理确认该笔业务的应收应付款项',
				bigIconClass : 'trade'
			},
			formConfig : {
				layout: 'column',border: false,
				items : [{
					columnWidth:.4,layout: 'form',border: false,
					items: {xtype:'f-mustgain',dataUrl : '/export/mustGain',title : '应收款',height:300,id : 'export-mustgain'}
				},{
					columnWidth:.6,layout: 'form',border: false,
					items: {xtype:'f-mustpay',dataUrl : '/export/mustPay',title : '应付款',height:300,id : 'export-mustpay'}
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
					
/*					for(var a in this.ajaxParams){
						if(Ext.isArray(this.ajaxParams[a]) && this.ajaxParams[a].length == 0){
							delete this.ajaxParams[a];
						}
					}*/
					
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
