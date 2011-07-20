package com.fortunes.zhies.action;

import java.text.ParseException;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fortunes.zhies.model.Accounts;
import com.fortunes.zhies.model.Accounts.AccountsType;
import com.fortunes.zhies.service.AccountsService;

@Component @Scope("prototype")
public class AccountsAction extends GenericAction<Accounts> {
	
	private AccountsService accountsService;
	
	private long[] checkedIds;
	
	public String markAsGainedOrPayed() throws Exception{
		accountsService.markAsGainedOrPayed(checkedIds,pDict("bankAccount"),
				AccountsType.valueOf(p("accountsType")),pDate("finishDate"));
		setJsonMessage(true, "成功标记!");
		return render(jo);
	}
	
	public String mustPay() throws Exception{
		queryMap.put("type", AccountsType.MUST_PAY.name());
		return super.list();
	}
	
	public String mustGain() throws Exception{
		queryMap.put("type", AccountsType.MUST_GAIN.name());
		return super.list();
	}
	
	protected void setEntity(Accounts e) throws ParseException{
		e.setAmountDone(pDouble("amountDone"));
		if(pDouble("amountDone")-pDouble("amountInPlan") >= 0){
			e.setFinished(true);
		}
	}
	
	protected JSONObject toJsonObject(Accounts e) throws ParseException{
		AdminHelper record = new AdminHelper();
		record.put("id", e.getId());
		record.put("tradeCode", e.getTrade().getCode());
		record.put("company", e.getCompany().getName());
		record.put("amountInPlan", e.getAmountInPlan());
		record.put("amountDone", e.getAmountDone());
		record.put("bankAccount", e.getBankAccount());
		record.put("finishDate", e.getFinishDate());
		record.put("finished", e.isFinished());
		return record.getJsonObject();
	}
	
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<Accounts> getDefService() {
		return accountsService;
	}
	
	public void setAccountsService(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	public AccountsService getAccountsService() {
		return accountsService;
	}

	public void setCheckedIds(long[] checkedIds) {
		this.checkedIds = checkedIds;
	}

	public long[] getCheckedIds() {
		return checkedIds;
	}

}
