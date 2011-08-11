package com.fortunes.fjdp.admin.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.model.NoticeMessage;
import com.fortunes.fjdp.admin.model.User;
import com.fortunes.fjdp.admin.service.NoticeMessageService;
import com.fortunes.fjdp.admin.service.NoticeService;
import com.fortunes.fjdp.admin.service.UserService;
import com.fortunes.zhies.model.Accounts;
import com.fortunes.zhies.model.Trade;
import com.fortunes.zhies.service.AccountsService;
import com.fortunes.zhies.service.TradeService;

import net.fortunes.core.action.BaseAction;

@Component @Scope("prototype")
public class MyhomeAction extends BaseAction{
	
	@Resource private NoticeService noticeService;
	@Resource private NoticeMessageService noticeMessageService;
	@Resource private UserService userService;
	@Resource private AccountsService accountsService;
	@Resource private TradeService tradeService;
	
	private List<NoticeMessage> noticeMessages;
	private List<User> onlineUsers;
	private List<TradeReminder> tradeReminders = new ArrayList<TradeReminder>();
	
	
	public static final int DELAYDAY_REMINDER_PARAM = 10;
	public class TradeReminder{
		public String customerName;
		public String tradeCode;
		public String delayDays;
		public Date finishDate;
		public Double totalMoney;
	}

	public String noticeList()throws Exception{
		noticeMessages = noticeMessageService.getNotReadedNoticesByUser(authedUser);
		return TEMPLATE;
	}
	
	public String mustGainReminder()throws Exception{
		Calendar now = Calendar.getInstance();
		List<Trade> trades = tradeService.getMustGainReminder(authedUser);
		
		for(Trade t : trades){
			Calendar finishCalendar = Calendar.getInstance();
			finishCalendar.setTime(t.getFinishDate());
			long miliSeconds = now.getTimeInMillis() - finishCalendar.getTimeInMillis();
			long days = miliSeconds/(24*3600*1000);
			if(days > DELAYDAY_REMINDER_PARAM){
				TradeReminder r = new TradeReminder();
				r.customerName = t.getCustomer().getName();
				r.tradeCode = t.getCode();
				r.delayDays = days+"天";
				r.finishDate = t.getFinishDate();
				r.totalMoney = t.getTotalSalesPrice();
				tradeReminders.add(r);
			}
		}
		return TEMPLATE;
	}
	
	public String loginStat()throws Exception{
		onlineUsers = userService.getOnlineUsers();
		return TEMPLATE;
	}
	
	
	
	
	//====================== setter and getter ======================
	public void setNoticeService(NoticeService noticeService) {
		this.noticeService = noticeService;
	}


	public NoticeService getNoticeService() {
		return noticeService;
	}


	public void setNoticeMessages(List<NoticeMessage> noticeMessages) {
		this.noticeMessages = noticeMessages;
	}


	public List<NoticeMessage> getNoticeMessages() {
		return noticeMessages;
	}

	public void setNoticeMessageService(NoticeMessageService noticeMessageService) {
		this.noticeMessageService = noticeMessageService;
	}

	public NoticeMessageService getNoticeMessageService() {
		return noticeMessageService;
	}

	public void setOnlineUsers(List<User> onlineUsers) {
		this.onlineUsers = onlineUsers;
	}

	public List<User> getOnlineUsers() {
		return onlineUsers;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setTradeReminders(List<TradeReminder> tradeReminders) {
		this.tradeReminders = tradeReminders;
	}

	public List<TradeReminder> getTradeReminders() {
		return tradeReminders;
	}
	
}
