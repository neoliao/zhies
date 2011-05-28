 var catalog = [{
	id: '1',
    title: '通行车辆统计',
    samples: [{
        text: '超速车辆月报表',
        url: ctx+'/common/birtReport.jsp',
        icon: 'report.jpg',
        desc: '查看本月的超速车辆'
    },{
        text: '车辆流量统计年报',
        url: ctx+'/common/birtReport.jsp',
        icon: 'report2.jpg',
        desc: ''
    }]
}];

ReportView = Ext.extend(Ext.DataView, {
    autoHeight: true,
    frame:true,
    cls:'demos',
    itemSelector: 'dd',
    overClass: 'over',
    
    
    tpl : new Ext.XTemplate(
        '<div class="data-ct">',
            '<tpl for=".">',
            '<div><a name="{id}"></a><h2><div>{title}</div></h2>',
            '<dl>',
                '<tpl for="samples">',
                    '<dd ext:url="{url}"><img src="'+ctx+'/include/image/report/{icon}"/>',
                        '<div><h4>{text}</h4><p>{desc}</p></div>',
                    '</dd>',
                '</tpl>',
            '<div style="clear:left"></div></dl></div>',
            '</tpl>',
        '</div>'
    ),

    onClick : function(e){
        var group = e.getTarget('h2', 3, true);
        if(group){
            group.up('div').toggleClass('collapsed');
        }else {
            var t = e.getTarget('dd', 5, true);
            if(t && !e.getTarget('a', 2)){
                var url = t.getAttributeNS('ext', 'url');
                window.open(url);
            }
        }
        return ReportView.superclass.onClick.apply(this, arguments);
    }
});

Report = Ext.extend(Ext.Panel, {
	id:'all-reports',
	cls:'dataView',
	border:true,
    closable: true,
    layout:'fit',
    initComponent : function(){
    	this.items = [
    		new ReportView({
	    		store:new Ext.data.JsonStore({
	    		    idProperty: 'id',
	    		    fields: ['id', 'title', 'samples'],
	    		    data: catalog
	    		})
    		})
    	];
    	Report.superclass.initComponent.call(this);    	
    },
    loadData:function(){
    	
    }   
    
});