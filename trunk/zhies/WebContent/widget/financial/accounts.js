
MustGain = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		
		var finishedRenderer = function(v){
			return v == true ?'<span style="color:green">已结清</span>' : '<span style="color:red">未结清</span>';
		};
		var sm = new Ext.grid.CheckboxSelectionModel();
		Ext.apply(this,{
			gridConfig:{
				sm:sm,
				cm:new Ext.grid.ColumnModel([
					sm,
					{header: '业务编号',dataIndex:'tradeCode',width:130},
					{header: '公司',dataIndex:'company'},
					{header: '应收',dataIndex:'amountInPlan',align:'right'},
					{header: '已收',dataIndex:'amountDone',align:'right'},
					{header: '银行账号',dataIndex:'bankAccount',renderer:dictRenderer},
					{header: '付款日期',dataIndex:'finishDate'},
					{header: '状态',dataIndex:'finished',renderer:finishedRenderer}
				]),	
				storeMapping:[
					'tradeCode','company','amountInPlan','amountDone','bankAccount','finishDate','finished'
				]
			},
			buttonConfig : [{
				text: '标记为已收',
				scope: this,
				iconCls : 'tick',
				handler : this.markAsGainedOrPayed
			},'->',
				
			{
				xtype : 'f-search',
				emptyText : '请输入公司名称'
			}
			
			/*'请选择公司',
			{ xtype: 'f-customer',id: 'accounts-customer',fieldLabel: '公司',hiddenName: 'customer'}*/
			
			
			],
			winConfig : {
				height: 330
			},
			url : ctx+'/accounts',
			listUrl : '/mustGain'
		});
		
		MustGain.superclass.initComponent.call(this);
		
		this.store.on('load',function(store,records,options){
			this.getSelectionModel().clearSelections();
		},this); 
		
/*		Ext.getCmp('accounts-customer').on('change',function(field,newValue,oldValue){
			this.store.reload({
				params:{ customerId : newValue }	
			});
		},this);*/
	},
	markAsGainedOrPayed : function(){
		var records = this.getSelectionModel().getSelections();
		if(records.length <= 0){
			App.msg("没有选择任何记录，请选择一个或者多个记录");
			return;
		}
		var confirmWin = new Ext.app.FormWindow({
				iconCls : 'key',
				winConfig : {
					height : 220,
					title : '收款确认',
					desc : '财务确认该笔业务的应收款项',
					bigIconClass : 'resetKeyIcon'
				},
				formConfig : {
					items: [
						{xtype: 'f-dict',fieldLabel: '收款账号',hiddenName: 'bankAccount',kind :'bankAccount'},
						{xtype: 'f-date',fieldLabel: '收款日期',name: 'finishDate'}
					]
					
				},
				buttons : [{
					text: '确定',
					scope:this,
					handler : function(){
						var selectedIds = [];
						var records = this.getSelectionModel().getSelections();
						for(i in records){
							if(records[i].id)
								selectedIds.push(records[i].id);
						}
						confirmWin.formPanel.getForm().submit({           
				            waitMsg:'保存中...',
							url:this.url+'/markAsGainedOrPayed',
							params: { checkedIds : selectedIds,accountsType : 'MUST_GAIN' },
							scope:this,
							success:function(form, action) {
								confirmWin.close();
								this.store.reload();
				            }
				        });
					}
				}]
		});
		confirmWin.show();
	}
});

MustPay = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		var finishedRenderer = function(v){
			return v == true ?'<span style="color:green">已结清</span>' : '<span style="color:red">未结清</span>';
		};
		var sm = new Ext.grid.CheckboxSelectionModel();
		Ext.apply(this,{
			gridConfig:{
				sm:sm,
				cm:new Ext.grid.ColumnModel([
					sm,
					{header: '业务编号',dataIndex:'tradeCode',width:130},
					{header: '公司',dataIndex:'company'},
					{header: '应付',dataIndex:'amountInPlan',align:'right'},
					{header: '已付',dataIndex:'amountDone',align:'right'},
					{header: '银行账号',dataIndex:'bankAccount',renderer:dictRenderer},
					{header: '付款日期',dataIndex:'finishDate'},
					{header: '状态',dataIndex:'finished',renderer:finishedRenderer}
				]),	
				storeMapping:[
					'tradeCode','company','amountInPlan','amountDone','bankAccount','finishDate','finished'
				]
			},
			buttonConfig : [{
				text: '标记为已付',
				iconCls : 'tick',
				scope: this,
				handler : this.markAsGainedOrPayed
			},'->',{
				xtype : 'f-search',
				emptyText : '请输入公司名称'
			}],
			winConfig : {
				height: 330
			},
			url : ctx+'/accounts',
			listUrl : '/mustPay'
		});
		MustPay.superclass.initComponent.call(this);
		
		this.store.on('load',function(store,records,options){
			this.getSelectionModel().clearSelections();
		},this); 
	},
	markAsGainedOrPayed : function(){
		var records = this.getSelectionModel().getSelections();
		if(records.length <= 0){
			App.msg("没有选择任何记录，请选择一个或者多个记录");
			return;
		}
		var confirmWin = new Ext.app.FormWindow({
				iconCls : 'key',
				winConfig : {
					height : 220,
					title : '付款确认',
					desc : '财务确认该笔业务的应付款项',
					bigIconClass : 'resetKeyIcon'
				},
				formConfig : {
					items: [
						{xtype: 'f-dict',fieldLabel: '付款账号',hiddenName: 'bankAccount',kind :'bankAccount'},
						{xtype: 'f-date',fieldLabel: '付款日期',name: 'finishDate'}
					]
					
				},
				buttons : [{
					text: '确定',
					scope:this,
					handler : function(){
						var selectedIds = [];
						var records = this.getSelectionModel().getSelections();
						for(i in records){
							if(records[i].id)
								selectedIds.push(records[i].id);
						}
						confirmWin.formPanel.getForm().submit({           
				            waitMsg:'保存中...',
							url:this.url+'/markAsGainedOrPayed',
							params: { checkedIds : selectedIds,accountsType : 'MUST_PAY' },
							scope:this,
							success:function(form, action) {
								confirmWin.close();
								this.store.reload();
				            }
				        });
					}
				}]
		});
		confirmWin.show();
	}
	
});

Accounts = Ext.extend(Ext.Panel,{
	layout:'border',
	closable: true,
	hideMode:'offsets',
	initComponent : function(){
		this.left = new MustGain({
			funcCode: this.funcCode,
			region:'west',
			title: '应收款列表',
			split:true,
			width: 540,
			minSize: 175,
			maxSize: 700
		});
		this.right = new MustPay({
			funcCode: this.funcCode,
			region:'center',	
			title: '应付款列表'
		});
		this.items = [this.left,this.right];
    	Accounts.superclass.initComponent.call(this);
			
    },
	loadData:function(){
		this.left.loadData();
		this.right.loadData();
	}
		
});
