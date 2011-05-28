EmployeeSelect = Ext.extend(Ext.app.AutoSelect,{
	hiddenName : 'employee',
	fieldLabel : '员工',
	storeFields : ['id','text','code','pinyin']	,
	tpl:new Ext.XTemplate(
	    '<tpl for="."><table class="x-combo-list-item" style="width:100%;"><tr>',
	    '<td>{text}</td><td style="text-align:right;color:gray">{code}</td>',
	    '</tr></table></tpl>'
	),
	emptyText:'请输入员工姓名或姓名拼音缩写',
	dataUrl : '/organization/ListEmployeesUnassign',
	disabled : true,
	initComponent : function(){
		EmployeeSelect.superclass.initComponent.call(this);
		this.store.on('beforeload',function(store,options){
			options.params.organizationId  = Ext.getCmp('employeeList').organizationId;
		},this); 
		
		this.on('select',function(combo,record,index){
			Ext.getCmp('employeeList').addEmployee(record.id);		
			this.clearValue();
		},this); 
	},
	clearValue : function(){
		if(this.hiddenField){
            this.hiddenField.value = '';
        }
        this.setRawValue('');
        this.lastSelectionText = '';
        this.value = '';
	}
});

OrganizationTree = Ext.extend(Ext.app.BaseFuncTree,{
	winConfig : {
		height: 310, width : 390,
		desc : '新增，修改机构的信息，添加员工到机构',
		bigIconClass : 'organizationIcon'
		
	},
	formConfig:{
		items: [
			{xtype:'f-text',fieldLabel:'机构名称',name: 'name',emptyText:'请输入机构名称',allowBlank:false},
			{xtype:'f-text',fieldLabel:'代码',name: 'code'},
			{xtype:'f-text',fieldLabel:'简称',name: 'shortName',allowBlank:false},
			{xtype:'f-text',fieldLabel:'地址',name: 'address'},
			{xtype:'f-text',fieldLabel:'电话',name: 'tel'},
			{xtype:'f-dict',fieldLabel:'机构类型',hiddenName: 'type',kind:'organizationType'}
		]
	},
	rootConfig: { id:'0' },	
	url:ctx+'/organization',
	initComponent : function(){
		OrganizationTree.superclass.initComponent.call(this);
		this.on('click',function(node,e){
			this.listEmployees(node.id);
		},this);
		
		this.root.on('load',function(node){
			Ext.getCmp('employeeList').clearTree();
			Ext.getCmp('employeeList').employeeSelect.disable();
		},this);
		
	},
	listEmployees : function(nodeId){
		Ext.getCmp('employeeList').loadData(nodeId);
		Ext.getCmp('employeeList').employeeSelect.enable();
	}
});

EmployeeTree = Ext.extend(Ext.tree.TreePanel,{
	initComponent : function(){
		this.employeeSelect = new EmployeeSelect();
		this.removeBt = new Ext.app.Button({
			text : '移除',
            tooltip : '从该机构中移除该员工',
            iconCls : 'remove',
			scope:this,
			disabled : true,
			handler:this.removeEmployee	
		});
		Ext.apply(this, {
			tbar:['添加员工 ：',this.employeeSelect,'-',this.removeBt],
			loader: new Ext.tree.TreeLoader({            
				dataUrl :ctx+'/organization/ListEmployees'
	        }),
	        root: new Ext.tree.AsyncTreeNode({ id:'0'})
		});
		
		EmployeeTree.superclass.initComponent.call(this);
		
		this.getLoader().on('beforeload',function(node){
			if(!this.organizationId){
				return false;
			}else	
				this.body.mask('正在加载...', 'x-mask-loading');
		},this);
		
		this.getLoader().on('load',function(node){
			this.body.unmask();
			Ext.getCmp('employeeList').removeBt.disable();
		},this);	
		
		this.on('click',function(node,e){
			this.selectedId = node.id;
			Ext.getCmp('employeeList').removeBt.enable();
		},this); 
	},
	clearTree : function(){
		this.organizationId = '';
		this.getLoader().load(this.root);
	},
	addEmployee :function(employeeId){
		Ext.Ajax.request({
			url: ctx+'/organization/addEmployee',
			params : { organizationId: this.organizationId, employeeId : employeeId},
			scope:this,
			success:function(response , options) {
				this.getLoader().load(this.root);
				App.msg('加入了一个新员工！');
			}
		});
	},
	removeEmployee :function(){
		Ext.Ajax.request({
			url: ctx+'/organization/removeEmployee',
			params : { organizationId: this.organizationId, employeeId : this.selectedId},
			scope:this,
			success:function(response , options) {
				this.getLoader().load(this.root);
				App.msg('移除了一个员工！');
			}
		});
	},
	loadData:function(organizationId){
		this.organizationId = organizationId;
		this.getLoader().baseParams = { organizationId : this.organizationId};
		this.getLoader().load(this.root);
	}
});

Organization = Ext.extend(Ext.Panel,{
	layout:'border',
	closable: true,
	hideMode:'offsets',
	initComponent : function(){
		this.organizationList = new OrganizationTree({
			funcCode: this.funcCode,
			region:'west',
			title: '机构列表',
			split:true,
			width: 250,
			minSize: 175,
			maxSize: 400
		});
		this.employeeList = new EmployeeTree({
			id:'employeeList',
			funcCode: this.funcCode,
			region:'center',			
			title: '员工列表'
		});
		this.items = [this.organizationList,this.employeeList];
		Organization.superclass.initComponent.call(this);
			
    },
	loadData:function(){
		this.organizationList.loadData();
	}
		
});
