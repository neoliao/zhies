CustomerSelect = Ext.extend(Ext.app.CompositeSelect,{
	dataUrl : '/customer/getCustomers'
});
Ext.reg('f-customer', CustomerSelect);

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

VerificationCompanySelect = Ext.extend(Ext.app.SelectField,{
	dataUrl : '/verificationCompany/getVerificationCompanys'
});
Ext.reg('f-verificationcompany', VerificationCompanySelect);

OperatorSelect = Ext.extend(Ext.app.SelectField,{
	dataUrl : '/role/getUsers?roleNameEn=operator'
});
Ext.reg('f-operator', OperatorSelect);





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

