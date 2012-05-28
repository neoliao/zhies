

ProduceArea = Ext.extend(Ext.app.BaseFuncPanel,{
	//loadFromGrid : false,
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
		
		Ext.apply(this,{
			gridConfig:{
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '业务建立时间',dataIndex:'createDate'},
					{header: '业务编号',dataIndex:'code'},
					{header: '客户',dataIndex:'customer',renderer:dictRenderer},
					//{header: '买方',dataIndex:'buyer',renderer:dictRenderer},
					{header: '产地证类型',dataIndex:'producerCertificate',renderer:dictRenderer},
					{header: '产地证号',dataIndex:'producerNo'},
					{header: '箱数',dataIndex:'packageNumber'},
					{header: '产地日期',dataIndex:'produceDate'},
					{header: '状态',dataIndex:'status',renderer:statusRenderer},
					{header: '业务员',dataIndex:'sales',renderer:dictRenderer},
					{header: '操作员',dataIndex:'operator',renderer:dictRenderer}
				]),	
				storeMapping:[
					'reportPortDate','code','createDate','customer','buyer','itemDesc','memo','buyerName',
					'status','sales','operator','itemQuantity','producerCertificate','producerNo'
					,'packageNumber','produceDate','cost_C','salesPrice_C'
				]
			},
			buttonConfig : [
				'all',{
					text : '操作提交',
					iconCls : 'tick',
					id : 'produceArea-operator_submit-Bt',
					privilegeCode: this.funcCode + '_operator_submit',
					scope : this,
					handler : this.submitToConfirm
				},{
					text : '应收应付提交',
					iconCls : 'tick',
					id : 'produceArea-accounts_submit-Bt',
					privilegeCode: this.funcCode + '_accounts_submit',
					scope : this,
					handler : this.showConfirmWin
				},{
					text : '查看业务',
					iconCls : 'view',
					id : 'produceArea-viewTrade-Bt',
					scope : this,
					handler : this.viewTrade
				},'->',{
					xtype : 'f-search',
					emptyText : '请输入业务编号或者客户名称'
				}
			],
			winConfig : {
				height : 440,
				width : 395,
				title : '产地证业务',
				desc : '新建或者修改产地证业务',
				bigIconClass : 'trade'
			},
			formConfig : {
				items : [
					{ xtype: 'f-customer',fieldLabel: '客户',hiddenName: 'customer',allowBlank: false},
					{ xtype: 'f-text',fieldLabel: '买方',name: 'buyerName'},
			        { xtype: 'f-number',fieldLabel: '成本价',name:'cost_C',allowBlank: false},
			        { xtype: 'f-number',fieldLabel: '销售价',name:'salesPrice_C',allowBlank: false},
			        { xtype: 'f-dict',fieldLabel: '产地证类型',hiddenName: 'producerCertificate',kind:'producerCertificate',allowBlank:false},
			        { xtype: 'f-text',fieldLabel: '产地证号',name: 'producerNo'},
					{ xtype: 'f-number',fieldLabel: '箱数',name: 'packageNumber'},
					{ xtype: 'f-date',fieldLabel: '产地日期',name: 'produceDate'},
					{ xtype: 'f-textarea',fieldLabel: '备注',name: 'memo'}
				]
			},
			url:ctx+'/produceArea'
		});
		ProduceArea.superclass.initComponent.call(this);
		
		this.getSelectionModel().on('rowselect',this.changeOperateStatus,this);
		
	},
	changeOperateStatus: function(sm,rowIndex,record){
		
		this.getTopToolbar().items.each(function(item,index,length){
			if((item.xtype == 'button'||item.type == 'button') && (item.enableOnEmpty != true))
				item.hide();
		});
		
		this.addBt.show();
		
		if(loginUser.ownRole('manager') || loginUser.ownRole('financials')){
			Ext.getCmp('produceArea-viewTrade-Bt').show();
		}
		
		if(record.data.status == 'OPERATOR_SAVED'){

			if(record.data.operator.text == loginUser.userName){
				this.delBt.show();
				this.editBt.show();
				Ext.getCmp('produceArea-operator_submit-Bt').show();
			}
			if(loginUser.ownRole('manager') || loginUser.ownRole('financials')){
				this.delBt.show();
				this.editBt.show();
				Ext.getCmp('produceArea-operator_submit-Bt').show();
			}
			
		}
		
		if(record.data.status == 'OPERATOR_SUBMITED'){
			if(record.data.operator.text == loginUser.userName){

			}
			if(loginUser.ownRole('manager') || loginUser.ownRole('financials')){
				Ext.getCmp('produceArea-accounts_submit-Bt').show();
				this.delBt.show();
				this.editBt.show();
			}
			
		}
		
		if(record.data.status == 'COST_CONFIRMED'){
			if(record.data.operator.text == loginUser.userName){
				Ext.getCmp('produceArea-viewTrade-Bt').show();
			}
			if(loginUser.ownRole('manager') || loginUser.ownRole('financials')){
				Ext.getCmp('produceArea-viewTrade-Bt').show();
			}
		}
		
		if(record.data.status == 'FINISHED'){
			if(record.data.operator.text == loginUser.userName){
				Ext.getCmp('produceArea-viewTrade-Bt').show();
			}
			if(loginUser.ownRole('manager') || loginUser.ownRole('financials')){
				Ext.getCmp('produceArea-viewTrade-Bt').show();
			}
		}
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
	viewTrade : function(){
		this.edit();
		this.saveBt.hide();
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
					items: {xtype:'f-mustgain',dataUrl : '/produceArea/mustGain',title : '应收款',height:300,id : 'produceArea-mustgain'}
				},{
					columnWidth:.6,layout: 'form',border: false,
					items: {xtype:'f-mustpay',dataUrl : '/produceArea/mustPay',title : '应付款',height:300,id : 'produceArea-mustpay'}
				}]
			},
			buttons : [{
				text: '确定',
				scope:this,
				handler : function(){
					//加入确认动作
					Ext.Msg.prompt('请仔细核对应收应付明细', '输入大写的YES确认:', function(btn, text){
					    if (btn == 'ok' && text == 'YES'){
							this.ajaxParams = { id :this.getSelectionModel().getSelected().id };
				
							var mustPayGrid = Ext.getCmp('produceArea-mustpay');
							var mustGainGrid = Ext.getCmp('produceArea-mustgain');
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
						}else{
							if(text != 'YES'){
								App.msg('输入不正确,输入大写的YES确认');
							}
						}
					},this);
					
					
				}
			}]
		});
		confirmWin.show();
		
		Ext.getCmp('produceArea-mustgain').store.load({params:{id : this.getSelectionModel().getSelected().id }});
		Ext.getCmp('produceArea-mustpay').store.load({params:{id : this.getSelectionModel().getSelected().id }});
	}
});
