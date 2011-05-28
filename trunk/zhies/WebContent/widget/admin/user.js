
/**
 * 根据用户选择角色
 * 
 */
Ext.app.RoleSelect = Ext.extend(Ext.app.MultiSelectField, {
	initComponent : function(){	
		
		this.store = new Ext.data.JsonStore({
		    url: ctx+'/role/getRolesByUser',
			root:'data',
		    fields: ['id', 'text','checked']			
		});
		
		Ext.app.RoleSelect.superclass.initComponent.call(this);
		
		this.store.on('beforeload',function(store,o){
			this.store.baseParams['id'] = Ext.getCmp('User').saveId;
		},this);
	}
});
Ext.reg('f-roleByUser', Ext.app.RoleSelect);

User = Ext.extend(Ext.app.BaseFuncPanel,{
	
	initComponent : function(){
		var rolesRender = function(v){
			var re = [];
			for(var r in v){
				if(v[r].text){
					re.push(v[r].text);
				}		
			}
			return re.join(',');
		}
		
		var lockedRender = function(v){		
			return v == true ?'<span style="color:red">已锁定</span>' : '';
		}
		Ext.apply(this,{
			url:ctx+'/user',
			gridConfig: {
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '用户名',dataIndex:'userName',sortable:true},
					{header: '用户显示名',dataIndex:'userDisplayName',sortable:true},
					{header: '对应员工',dataIndex:'employee',renderer : dictRenderer},
					{header: '最后登陆时间',dataIndex:'lastLoginTime',width:150},
					{header: '所属角色',dataIndex:'role',renderer:dictRenderer,width:200},
					{header: '锁定',dataIndex:'locked',renderer:lockedRender}
				]),	
				storeMapping:[
					'id','userName','userDisplayName','employee','lastLoginTime','role','locked'
				]
			},
			winConfig : {
				height: 440, width : 405,
				desc : '为员工分配用户名，设置密码，并分配角色',
				bigIconClass : 'userIcon'
			},
			formConfig:{
				items: [
					{xtype:'fieldset',title: '对应员工',autoHeight:true,
						items :[
							{xtype: 'f-select',dataUrl:'/employee/getEmployeesUnAssign',storeFields:['id','text','code'],fieldLabel: '员工姓名',hiddenName: 'employee',id:'employeeSelect',allowBlank: false,emptyText: '请选择一个员工',listeners : {}}
						]
					},
					{xtype:'fieldset',title: '登陆信息',autoHeight:true,
						items :[
							{xtype: 'f-text',fieldLabel: '用户名',id: 'userName',name: 'userName',allowBlank: false},
							{xtype: 'f-text',fieldLabel: '用户显示名',id: 'userDisplayName',name: 'userDisplayName',allowBlank: false},
							{xtype:'panel',id:'passwordPanel',autoHeight:true,border:false,layout:'form',
								 items :[
									{xtype: 'f-text',fieldLabel: '密码',id:'pswd',name: 'password',inputType:'password',allowBlank: false},
									{xtype:'f-text',fieldLabel:'确认密码',id:'pswdComfirm',name:'password2',inputType:'password',vtype: 'password',initialPassField: 'pswd',allowBlank: false}
								]
							},
							{xtype:'panel',id:'resetPanel',autoHeight:true,border:false, buttonAlign:'center',hidden:true,
								 buttons :[
									{xtype:'f-button',text: '重设密码',iconCls:'key',scope:this,handler:this.resetPassword}			
								]
							}
						]
					},
					{xtype:'fieldset',title: '选择用户角色',autoHeight:true,
						items :[
//							{xtype:'f-roleByUser',fieldLabel: '用户角色',hiddenName:'role',emptyText: '请选择一个用户角色',allowBlank: false}			
							{xtype: 'f-select',dataUrl:'/role/getRoles',storeFields:['id','text'],allowBlank: false,
								fieldLabel: '用户角色',hiddenName: 'role',id:'roleSelect',listeners : {}}
						]
					}
				]
			},
			buttonConfig : ['all','-',{
				text:'锁定',
				iconCls:'lock',
				id:'lockUserBt',
				prililegeCode:this.funcCode+'_lock',
				scope:this,
				handler:this.lockUser
			},{
				text:'解锁',
				iconCls:'key',
				id:'unlockUserBt',
				prililegeCode:this.funcCode+'_lock',
				scope:this,
				handler:this.lockUser,
				hidden:true
			},'->',{
				xtype : 'f-search',
				emptyText : '请输入用户名或者用户显示名'
			}]
		});
		User.superclass.initComponent.call(this);

		this.getSelectionModel().on('rowselect',function(sm,rowIndex,record){
			var flag = sm.getSelected().data.locked;
			Ext.getCmp('lockUserBt').setVisible(!flag);
			Ext.getCmp('unlockUserBt').setVisible(flag);
		},this); 
		
		this.on('winshow',function(grid){
			if(this.saveType == 'update'){
				Ext.getCmp('passwordPanel').setVisible(false).setDisabled(true);
				Ext.getCmp('resetPanel').setVisible(true).setDisabled(false);
				Ext.getCmp('pswd').setDisabled(true);
				Ext.getCmp('pswdComfirm').setDisabled(true);
				Ext.getCmp('employeeSelect').setReadOnly(true);
				Ext.getCmp('userName').setReadOnly();
			}
			Ext.getCmp('employeeSelect').on('select',function(combo,record,index){
				Ext.getCmp('userName').setValue(record.data.code);
				Ext.getCmp('userDisplayName').setValue(record.data.text);
			},this);
		},this);
	},
	resetPassword : function(){
		this.resetWin = new Ext.app.FormWindow({
			iconCls : 'key',
			winConfig : {
				height : 210,
				width : 395,
				title : '重设密码',
				desc : '将旧密码作废,重设用户的新密码',
				bigIconClass : 'resetKeyIcon'
			},
			formConfig : {
				items : [
		 			{xtype: 'f-text',fieldLabel: '密码',id:'pswd2',name: 'password',inputType:'password',allowBlank: false},
					{xtype:'f-text',fieldLabel:'确认密码',id:'pswdComfirm2',name:'password2',inputType:'password',vtype: 'password',initialPassField: 'pswd2',allowBlank: false}
				]
			},
			buttons : [{
				text: '确定',
				scope:this,
				handler : function(){
					this.resetWin.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:this.url+'/resetPassword',
						params: { id :this.getSelectionModel().getSelected().id },
						scope:this,
						success:function(form, action) {
							this.resetWin.close();
							App.msg("密码设置成功！");
			            }
			        });
				}
			}]
		});
		this.resetWin.show();
	},
	lockUser : function(){
		Ext.Ajax.request({
			url:this.url+'/lockUser',
			params: { id :this.getSelectionModel().getSelected().id },
			scope:this,
			success:function(response, options) {
				this.loadData();
			}
		});
	}
});
