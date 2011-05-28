<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
	<title>用户登陆</title>
	<s:include value="/common/header.jsp" />
<script>
Ext.onReady(function(){
	
	var banner = new Ext.Panel({
		id:'loginimg',
        height:150,
        html: '<img src="include/image/theme/login.png"/>'  
    });
	
	var loginform = new Ext.FormPanel({
		id:'loginform',		
	    baseCls: 'x-plain',
	    labelWidth: 60, 
	   	bodyStyle:'padding:15px 10px 0 90px',
	    defaults: {width: 230},
	    defaultType: 'textfield',
	
	    items: [{
	            fieldLabel: '用户名',
	            name: 'userName',
	            id:'loginUser',
	            validateOnBlur:false,
	            blankText:'用户名不能为空,请输入您的用户名',
	            value:'admin',
	            allowBlank:false
	        },{
	            fieldLabel: '密码',
	            id:'loginPswd',
	            name: 'password',
	            validateOnBlur:false,
	            blankText:'密码不能为空',
	            allowBlank:false,
	            value:'admin',
	            inputType:'password'		
	        }
	    ]
	});   
	
	function submit(){
		loginform.getForm().submit({
             url: ctx+'/system/login',
             waitTitle: '请稍候...',   
             waitMsg: '正在登陆',
             success: function (form, action) {
                 document.location = ctx+'/system/viewport';
             },
             failure:function(form, action) {
                 if(action.failureType == Ext.form.Action.SERVER_INVALID)
             		Ext.MessageBox.alert('信息', action.result.msg);
             }
         });
	}
	
	var win = new Ext.Window({		
		title:'用户登陆',
		iconCls: 'userman',
	    layout:'fit',
	    draggable:false,
	    resizable:false,
	    constrain:true,
	    closable:false,
	    plain:true,
	    buttonAlign:'center',
	    width:510, 
	    height:300,
	    closeAction:'hide',
	    plain: true,
	    layout:'anchor', 
		items:[banner,loginform],
	
	    buttons: [{
	        text:'登陆',
	        name:'login',
	        handler: submit
	    }]
	});
	
	win.show();
	win.alignTo(document,'c-c',[0,-50]);  
	
	var map = new Ext.KeyMap(document, {
	    key: Ext.EventObject.ENTER,
	    fn: submit,
	    scope: this
	});
});
</script>
</head>
<body class="login-body">
<div id="login-copyright" class="login-copyright" >
	<p>深圳方迪计算机系统有限公司</p>
	<p>&copy;1997-2008</p>
</div>
</body>
</html>

