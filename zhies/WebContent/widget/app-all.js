Ext.app.sYearMonthSelect = Ext.extend(Ext.form.CompositeField,{
	initComponent : function(){
    	Ext.apply(this,{
    		defaults:{
    			width:80
    		},
    		items:[
    			{xtype: 'f-year',name: 'year'},
			    {xtype: 'f-month',name: 'month'}
			]
    	})
        Ext.app.sYearMonthSelect.superclass.initComponent.call(this);
    }
});
Ext.reg('s-yearmonth', Ext.app.sYearMonthSelect);

ExportSelect = Ext.extend(Ext.app.CompositeSelect,{
	storeFields : ['id','text','code','pinyin','relative','customerName','buyerName','itemDesc','itemQuantity'],	
	dataUrl : '/export/getExports'
});
Ext.reg('f-exportselect', ExportSelect);

ImportSelect = Ext.extend(Ext.app.CompositeSelect,{
	storeFields : ['id','text','code','pinyin','relative','customerName','buyerName','itemDesc','itemQuantity'],	
	dataUrl : '/import/getImports'
});
Ext.reg('f-importselect', ImportSelect);

CustomerSelect = Ext.extend(Ext.app.CompositeSelect,{
	dataUrl : '/customer/getCustomers'
});
Ext.reg('f-customer', CustomerSelect);

BuyerSelect = Ext.extend(Ext.app.SelectField,{
	dataUrl : '/buyer/getBuyers'
});
Ext.reg('f-buyer', BuyerSelect);

CustomsBrokerSelect = Ext.extend(Ext.app.SelectField,{
	dataUrl : '/customsBroker/getCustomsBrokers'
});
Ext.reg('f-customsbroker', CustomsBrokerSelect);

InspectionSelect = Ext.extend(Ext.app.SelectField,{
	dataUrl : '/inspection/getInspections'
});
Ext.reg('f-inspection', InspectionSelect);

TruckCompanySelect = Ext.extend(Ext.app.SelectField,{
	dataUrl : '/truckCompany/getTruckCompanys'
});
Ext.reg('f-truckcompany', TruckCompanySelect);

ShipCompanySelect = Ext.extend(Ext.app.SelectField,{
	dataUrl : '/shipCompany/getShipCompanys'
});
Ext.reg('f-shipcompany', ShipCompanySelect);

AirCompanySelect = Ext.extend(Ext.app.SelectField,{
	dataUrl : '/airCompany/getAirCompanys'
});
Ext.reg('f-aircompany', AirCompanySelect);

VerificationCompanySelect = Ext.extend(Ext.app.SelectField,{
	dataUrl : '/verificationCompany/getVerificationCompanys'
});
Ext.reg('f-verificationcompany', VerificationCompanySelect);

OperatorSelect = Ext.extend(Ext.app.SelectField,{
	dataUrl : '/role/getUsers?roleNameEn=operator'
});
Ext.reg('f-operator', OperatorSelect);

SalesSelect = Ext.extend(Ext.app.SelectField,{
	dataUrl : '/role/getUsers?roleNameEn=sales',
	initComponent : function(){
		
        SalesSelect.superclass.initComponent.call(this);
        
        this.on('beforerender', function(c){
        	if(this.privilegeCode){
				return loginUser.ownPrivilege(this.privilegeCode);
			}
        }, this);
    }
});
Ext.reg('f-sales', SalesSelect);


ItemsGrid = Ext.extend(Ext.grid.EditorGridPanel,{
    clicksToEdit:1,
    title : '货物列表',
    style : 'padding-left : 20px;',
    initComponent : function(){
    	
    	this.FieldItem = Ext.data.Record.create([
           {name: 'name'},
           {name: 'model'},
           {name: 'price'},
           {name: 'quantity'},
           {name: 'unit'},
           {name: 'packageQuantity'},
           {name: 'unitQuantity'},
           {name: 'unitForQuantity'},
           {name: 'grossWeight'},
           {name: 'netWeight'},
           {name: 'unitForWeight'}
		]);
		
		Ext.apply(this,{
			store : new Ext.data.JsonStore({
				root: 'root',
				idProperty: 'itemId',
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
	        	header: "单价",dataIndex: 'price',width: 80,
	        	editor: new Ext.form.NumberField({allowBlank: false})
	        },{
	        	header: "合同数量",dataIndex: 'quantity',width: 80,
	        	editor: new Ext.form.NumberField({allowBlank: false})
	        },{
	        	header: "单位",dataIndex: 'unit',width: 80,
	        	editor: new Ext.form.TextField({allowBlank: false})
	        },{
	        	header: "箱数",dataIndex: 'packageQuantity',width: 80,
	        	editor: new Ext.form.NumberField({allowBlank: false})
	        },{
	        	header: "数量",dataIndex: 'unitQuantity',width: 80,
	        	editor: new Ext.form.NumberField({allowBlank: false})
	        },{
	        	header: "数量单位",dataIndex: 'unitForQuantity',width: 80,
	        	editor: new Ext.form.TextField({allowBlank: false})
	        },{
	        	header: "毛重",dataIndex: 'grossWeight',width: 80,
	        	editor: new Ext.form.NumberField({allowBlank: false})
	        },{
	        	header: "净重",dataIndex: 'netWeight',width: 80,
	        	editor: new Ext.form.NumberField({allowBlank: false})
	        },{
	        	header: "重量单位",dataIndex: 'unitForWeight',width: 80,
	        	editor: new Ext.form.TextField({allowBlank: false})
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
        	unit:'个',
        	unitForQuantity:'个',
        	unitForWeight:'KG'
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
    		var record = this.store.getAt(rowIndex);
    		this.deletedIds = this.deletedIds || [];
    		if(!Ext.isString(record.id)){
	    		this.deletedIds.push(record.id);
    		}
        	this.store.removeAt(rowIndex);
    	}else{
    		App.msg("没有可用的数据，请选中一行");
    	}
       
    }
	
});

Ext.reg('f-itemsgrid', ItemsGrid);

MustPayGrid = Ext.extend(Ext.grid.EditorGridPanel,{
    clicksToEdit:1,
    initComponent : function(){
    	
    	this.FieldItem = Ext.data.Record.create([
           {name: 'name'},
           {name: 'company'},
           {name: 'amount'}
		]);
		
		Ext.apply(this,{
			store : new Ext.data.JsonStore({
				root: 'data',
		        fields: this.FieldItem,
		        url : ctx + this.dataUrl
			}),
    		cm: new Ext.grid.ColumnModel([{
	        	header: "名称",dataIndex: 'name',width: 120
	        },{
	        	header: "公司",dataIndex: 'company',width: 150,renderer:dictRenderer
	        },{
	        	header: "金额(实际成本)",dataIndex: 'amount',width: 100,
	        	editor: new Ext.form.NumberField({allowBlank: false})
	        }])
		});
		MustPayGrid.superclass.initComponent.call(this);
	}
});

Ext.reg('f-mustpay', MustPayGrid);

MustGainGrid = Ext.extend(Ext.grid.EditorGridPanel,{
    clicksToEdit:1,
    initComponent : function(){
    	
    	this.FieldItem = Ext.data.Record.create([
           {name: 'name'},
           {name: 'amount'}
		]);
		
		Ext.apply(this,{
			store : new Ext.data.JsonStore({
				root: 'data',
		        fields: this.FieldItem,
		        url : ctx+ this.dataUrl
			}),
    		cm: new Ext.grid.ColumnModel([{
	        	header: "名称",dataIndex: 'name',width: 120
	        },{
	        	header: "金额",dataIndex: 'amount',width: 100,
	        	editor: new Ext.form.NumberField({allowBlank: false})
	        }])
		});
		MustGainGrid.superclass.initComponent.call(this);
	}
});

Ext.reg('f-mustgain', MustGainGrid);





/*Ext.app.EditDictSelect = Ext.extend(Ext.app.DictSelect, {
    setValue : function(v){		
		if(typeof v == 'object'){
			var text = v[this.displayField];
			var value = v[this.valueField]||'';
			this.lastSelectionText = text;
			//设置控件DOM的值
			if (this.hiddenField) {
		    	this.hiddenField.value = value;
		    }
			//设置显示值
			Ext.form.ComboBox.superclass.setValue.call(this, text);
			//设置变量的值
			this.value = value;
			
			this.fireEvent('initvalue', this, v, this.startValue);
		}else{
			var record = this.store.getById(v);
			if(record){
				this.value = record.data;
			}
	        Ext.app.EditDictSelect.superclass.setValue.call(this, this.value);
		}
    },
    getValue : function(){
        var record = this.store.getById(this.value);
		if(record){
			return record.data;
		}
    }
});*/

