
function okCloseJsWin(flexObject){
	var photoId = flexObject.photoId;
	var o = Ext.getCmp('photoField');
	o.setValue(photoId);
	o.uploadWin.close();
}
function cancelCloseJsWin(){
	var o = Ext.getCmp('photoField');
	o.uploadWin.close();
}

PhotoField = Ext.extend(Ext.form.Field, {
	noPhotoUrl : ctx+'/include/image/nophoto3.gif',
	photoUrlPrifix : ctx+'/employee/photo?photoId=',
    actionMode: 'wrap',
    initComponent : function() {
        this.photoArea = new Ext.Panel({
			border : false,
	    	html : ''
		});
		this.photoAreaTpl = new Ext.XTemplate(
			'<div class="photo"><img src="{url}" /></div>'
		);
        this.photoPanel = new Ext.Panel({
        	border : false,
        	items : [this.photoArea,{
				autoHeight:true,
				border:false,
				items :[{
					xtype:'f-button',
					text: '设置相片',
					style : 'margin:4 0 0 28;',
					scope:this,
					handler:this.setPhoto	
				}]
			}]
        });
        PhotoField.superclass.initComponent.call(this);
    },    
    
    onRender : function(ct, position){
        this.autoCreate = {
            id: this.id,
            name: this.name,
            type: 'hidden',
            tag: 'input'    
        };
        PhotoField.superclass.onRender.call(this, ct, position);
        this.wrap = this.el.wrap();
        this.photoPanel.render(this.wrap);
        this.photoAreaTpl.overwrite(this.photoArea.body,{url:this.noPhotoUrl});
    },
    
    beforeDestroy : function(){
        Ext.destroy(this.photoPanel);
        PhotoField.superclass.beforeDestroy.call(this);
    },
    setPhoto:function(){
    	this.uploadWin = new Ext.app.FormWindow({
			iconCls : 'picture',
			winConfig : {
				height : 520,
				width : 660,
				title : '设置人员相片',
				desc : '上传并设置人员相片',
				bigIconClass : 'pictureIcon'
			},
			formConfig : {
				fileUpload : true,
				items : [
					{xtype:'panel',height:400,width:640,
						border: false, autoLoad: {url: ctx + '/flash/uploadPhoto.jsp', scripts: true}}
				]
			}
		});
		this.uploadWin.show();
    },
    
	setupPhoto : function(){
		this.uploadWin = new Ext.app.FormWindow({
			iconCls : 'picture',
			winConfig : {
				height : 210,
				width : 395,
				title : '设置人员相片',
				desc : '上传并设置人员相片',
				bigIconClass : 'pictureIcon'
			},
			formConfig : {
				fileUpload : true,
				items : [
		 			{xtype: 'f-upload',fieldLabel: '上传相片',name: 'photoFile',allowBlank: false}
				]
			},
			buttons : [{
				text: '确定',
				scope:this,
				handler : function(){
					this.uploadWin.formPanel.getForm().submit({           
			            waitMsg:'保存中...',
						url:ctx+'/employee/setupPhoto',
						scope:this,
						success:function(form, action) {
							this.uploadWin.close();
							this.setValue(action.result.photoId);
							App.msg(action.result.msg);
			            }
			        });
				}
			}]
		});
		this.uploadWin.show();
    },
    setValue : function(photoId){
        this.photoAreaTpl.overwrite(this.photoArea.body,{
        	url : photoId? this.photoUrlPrifix + photoId:this.noPhotoUrl});
        return PhotoField.superclass.setValue.call(this, photoId);
    }
});

Ext.reg('f-photo', PhotoField);

Employee = Ext.extend(Ext.app.BaseFuncPanel,{
	initComponent : function(){
		var emailLink = function(v){
		    return !v? "" : String.format('<span><a href="mailto:{0}" target="_blank" class="emailLink">{0}</a></span>',v);
		}
		
		var qq = function(v){
			return !v ? "" : String.format('<span><a target=blank href=tencent://message/?uin={0}><img border="0" SRC=http://wpa.qq.com/pa?p=1:{0}:5 alt="QQ号:{0}"></a></span>',v);
		}
		
		var employeeStatus = function(v){
			var text = v['text']||'';
			var map = {
				'离职' : 'red',
				'试用' : 'blue'
			}	 
			return String.format('<span style="color:{0}">{1}</span>',map.text||'black',text);
		}
		var organizationRenderer = function(v){
			return v.text;
		}
		
		Ext.apply(this,{
			gridConfig:{
				sm:new Ext.grid.RowSelectionModel(),
				cm:new Ext.grid.ColumnModel([
					new Ext.grid.RowNumberer(),
					{header: '姓名',dataIndex:'name',sortable:true},
					{header: '工号',dataIndex:'code',sortable:true},
					{header: '部门',dataIndex:'organization',renderer:organizationRenderer},
					{header: '职务',dataIndex:'position',renderer:dictRenderer},
					{header: '学历',dataIndex:'education',renderer:dictRenderer},
					{header: '人员类型',dataIndex:'peopleType',renderer:dictRenderer},
					{header: '性别',dataIndex:'sex',renderer:dictRenderer},
					{header: '入职日期',dataIndex:'hireDate'},
					{header: '办公电话',dataIndex:'phone'},
					{header: '手机',dataIndex:'mobile'},
					{header: 'qq',dataIndex:'qq'},
					{header: '在职情况',dataIndex:'status',renderer:employeeStatus},
					{header: '电子邮件',dataIndex:'email',renderer:emailLink}
				]),	
				storeMapping:[
					'code', 'name','organization','position','education','peopleType', 'sex', 'phone', 'mobile', 'status', 'qq', 'hireDate', 'email','photoId'
				]
			},
			winConfig : {
				height: 520,width:440,
				desc : '新增，修改员工的的信息',
				bigIconClass : 'employeeIcon'
			},
			buttonConfig : ['all','-','->',{
				xtype : 'f-search',
				emptyText : '请输入姓名或者工号'
			}],
			formConfig:{
				items: [{
					layout:'column',
					border : false,
					defaults : {
                		border : false
                	},
					items :[{
						width:235,
	                	layout: 'form',
	                	defaults : {
	                		msgTarget : 'under',
	                		width : 130
	                	},
						items: [
							{xtype: 'f-text',fieldLabel: '姓名',name: 'name',emptyText: '请输入员工姓名',allowBlank: false}, 
							{xtype: 'f-text',fieldLabel: '拼音缩写',name: 'pinYinName',emptyText: '请输入姓名拼音'},
							{xtype: 'f-text',fieldLabel: '工号',name: 'code',vtype: 'digital',allowBlank: false},
							{xtype: 'f-select',dataUrl:'/organization/getOrganizations',storeFields:['id','text','code'],
								fieldLabel: '部门名',hiddenName: 'organization',id:'organizationSelect',listeners : {}},
							{xtype: 'f-dict',fieldLabel: '职务',hiddenName: 'position',kind: 'position'}, 
							{xtype: 'f-dict',fieldLabel: '学历',hiddenName: 'education',kind: 'education'}, 
							{xtype: 'f-dict',fieldLabel: '人员类型',hiddenName: 'peopleType',kind: 'peopleType'}, 
							{xtype: 'f-dict',fieldLabel: '性别',hiddenName: 'sex',kind: 'sex'}, 
							{xtype: 'f-text',fieldLabel: '电子邮件',name: 'email',vtype: 'email'},
							{xtype: 'f-text',fieldLabel: '办公电话',name: 'phone'},
							{xtype: 'f-text',fieldLabel: '手机',name: 'mobile'},
							{xtype: 'f-text',fieldLabel: 'qq',name: 'qq',vtype: 'digital'}
						]
					},{
						xtype : 'f-photo',
						id:'photoField',
						name: 'photoId',
						allowBlank: false,
						columnWidth:.7
					}]
				},{
					layout:'form',
					border : false,
					defaults : {
						width : 260
					},
					items :[
						{xtype: 'f-date',fieldLabel: '入职日期',name: 'hireDate'},
						{xtype: 'f-dict',fieldLabel: '在职情况',hiddenName: 'status',kind: 'employeeStatus'}
					]
				}]
			}
			,
			url:ctx+'/employee'	
		});
		Employee.superclass.initComponent.call(this);
		
	}
	
});
