PropertyCompanySelect = Ext.extend(Ext.app.CompositeSelect,{
	dataUrl : '/propertyCompany/getPropertyCompanys'
});
Ext.reg('f-property', PropertyCompanySelect);

DeveloperSelect = Ext.extend(Ext.app.CompositeSelect,{
	dataUrl : '/developer/getDevelopers'
});
Ext.reg('f-developer', DeveloperSelect);

AreaSelect = Ext.extend(Ext.app.CompositeSelect,{
	dataUrl : '/area/getAreas'
});
Ext.reg('f-area', AreaSelect);

ProjectSelect = Ext.extend(Ext.app.CompositeSelect,{
	hasRelative : true,
	relativeHeader : '所属镇(街)',
	dataUrl : '/project/getProjects'
});
Ext.reg('f-project', ProjectSelect);

BuildingSelect = Ext.extend(Ext.app.CompositeSelect,{
	hasRelative : true,
	relativeHeader : '所属项目',
	dataUrl : '/project/getBuildings'
});
Ext.reg('f-building', BuildingSelect);

HouseSelect = Ext.extend(Ext.app.CompositeSelect,{
	hasRelative : true,
	relativeHeader : '所属项目',
	dataUrl : '/house/getHouses'
});
Ext.reg('f-house', HouseSelect);