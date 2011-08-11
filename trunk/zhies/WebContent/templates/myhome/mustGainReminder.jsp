<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<div class="msg-body">
	<p class="welcome">
		共有<span class="countNumber"><s:property value="tradeReminders.size"/></span>个收款提醒！
		<a class="block refreshBt" href="#" onclick="Ext.getCmp('MustGainReminder-portal').refresh();">刷新</a>
	</p>
	<table class="body-table"><tbody>
		<s:iterator value="tradeReminders" status="rowStatus">
		<tr class="msg-item <s:if test="#rowStatus.even">even</s:if>">
			<td style="" class="userName"><s:property value="customerName"/></td> 
			<td style="" class="userName"><s:property value="tradeCode"/></td> 
			<td style="" class="countNumber"><s:property value="delayDays"/></td> 
			<td style="" class="userName"><s:property value="totalMoney"/></td> 
			<td>完成日期：<span class="date"><s:date name="finishDate" format="yyyy-MM-dd" /></span></td>
		</tr>
		</s:iterator>
		
	</tbody></table>		
</div>
<script type="text/javascript">

</script>
