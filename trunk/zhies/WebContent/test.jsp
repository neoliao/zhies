<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
  	<title>FJDP</title>
  	<s:include value="/common/header.jsp" />
	
</head>
<body>
<script>
Ext.onReady(function(){

  var bd = Ext.getBody();

  /*
   * ================  Simple form  =======================
   */
  bd.createChild({tag: 'h2', html: 'Form 1 - Very Simple'});


  var simple = new Ext.FormPanel({
      labelWidth: 75, // label settings here cascade unless overridden
      url:'save-form.php',
      frame:true,
      title: 'Simple Form',
      bodyStyle:'padding:5px 5px 0',
      width: 350,
      defaults: {width: 230},
      defaultType: 'textfield',

      items: [{
            fieldLabel: 'First Name',
            name: 'first',
            allowBlank:false
        },{
            fieldLabel: 'Last Name',
            name: 'last'
        },{
            fieldLabel: 'Company',
            name: 'company'
        }, {
            fieldLabel: 'Email',
            name: 'email',
            vtype:'email'
        },{
			xtype: 'f-property',
			dataUrl:'/propertyCompany/getPropertyCompanys',
			fieldLabel: '物管公司',
			hiddenName: 'propertyCompany',
			emptyText:'请输入物业公司名称或拼音缩写',
			allowBlank: false
         }
      ],

      buttons: [{
          text: 'Save'
      },{
          text: 'Cancel'
      }]
  });

  PanelStepFive = Ext.extend(Ext.Window, {
  	border:false,
  	html : '<div style="margin-left:80px;margin-top:160px;"><div style="margin-left:120px;"><img src="'+ctx+'/include/image/bigIcon/finish.gif"></img></div>'+
  		'<div class="finish-text">所有业务流程都已经完成,请点击"<b>确定</b>"按钮关闭窗口</div></div>'
    });

  var p = new PanelStepFive({height : 570,width:520});
  p.show();

  //p.render(document.body);
	
  
});

</script>
<div id="panelel"></div>
</body>
</html>

