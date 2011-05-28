<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<html>
<head>
<title>FJDP管理控制平台</title>
<s:include value="/common/header.jsp" />
<script type="text/javascript">


Ext.onReady(function(){

	var intiDbPanel = new Ext.Panel({
		frame:true,
		height: 200,
		width: 400,
		title:'数据库初始化',
		layout:'anchor',
		html:'初始化本系统的数据，完全初始化会删除表并重新建立表再插入初始化数据，菜单权限字典初始化只会新增和更新（不会删除）菜单权限字典，不会将表整个重建',
		buttons:[{
			xtype:'button',
			text:'完全初始化',
			scope:this,
			handler:function(){
				Ext.Msg.show({
				   title:'完全初始化确认',
				   msg: '您确实要完全初始化吗？这个操作将重新构建数据库，以前所有的数据都会丢失!',
				   buttons: Ext.Msg.YESNO,
				   fn: function(buttonId){
					   if(buttonId == 'yes'){
						  	Ext.Ajax.request({
								url : ctx+'/console/initDb',
								success:function(){
									App.msg("成功更新");
								}
							})
						}
					},
					icon: Ext.MessageBox.WARNING
				});
			}
		},{
			xtype:'button',
			text:'菜单权限字典初始化',
			scope:this,
			handler:function(){
				Ext.Ajax.request({
					url : ctx+'/console/rebuildDb',
					success:function(){
						App.msg("成功更新");
					}
				})
			}
		}]
	});
	intiDbPanel.render('intiDbPanel');
  
});
</script>

</head>
<body style="padding:10px;">
<div id="intiDbPanel"></div>
</body>
</html>

