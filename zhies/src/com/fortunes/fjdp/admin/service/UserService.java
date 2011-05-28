package com.fortunes.fjdp.admin.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.Constants;
import com.fortunes.fjdp.admin.model.Privilege;
import com.fortunes.fjdp.admin.model.Role;
import com.fortunes.fjdp.admin.model.User;

import net.fortunes.core.log.annotation.LoggerClass;
import net.fortunes.core.service.GenericService;

@Component
@LoggerClass
public class UserService extends GenericService<User>{
	
	private static Map<String,Integer> userLoginStatus = new HashMap<String,Integer>();
	
	protected DetachedCriteria getConditions(String query,Map<String, String> queryMap) {
		DetachedCriteria criteria = super.getConditions(query, queryMap);
		if(query !=  null){
			criteria.add(Restrictions.or(
					Restrictions.ilike("name", query, MatchMode.ANYWHERE), 
					Restrictions.ilike("displayName", query, MatchMode.ANYWHERE)
			));
		}
		return criteria;
	}
	
	public boolean lockOrUnlockUser(String userId){
		User user = get(userId);
		user.setLocked(user.isLocked()? false : true);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getOnlineUsers(){
		Set<String> names = userLoginStatus.keySet();
		int i = 0 ;
		int len = names.size();
		StringBuffer HQL = new StringBuffer("from User as u where u.name in (");
		if(0==len){
			return null;
		}
		for(String name : names){
			i++;
			if(1 == len){
				HQL.append("'").append(name).append("'");
			}
			else{
				HQL.append("'").append(name).append("'");
			}
			if (i != len) {
				HQL.append(",");
			}
		}	
		HQL.append(")");
		return this.find(HQL.toString());
		//return getDefDao().findByQueryString("from User as u where u.loginSession.logined = true");
	}
	
	public boolean resetPassword(String userId,String password){		
		User user = get(userId);
		user.setPassword(password);
		return true;
	}
	
	public void updateLoginSession(User user, int logined){
		user.getLoginSession().setLastLoginTime(new Date());
		update(user);
		if(Constants.USER_STATUS_LOGOUT == logined){
			userLoginStatus.remove(user.getName());
			return;
		}
		userLoginStatus.put(user.getName(), logined);
		
	}
	
	
	/** 验证用户
	 * @param user
	 * @return
	 */
	
	@SuppressWarnings("unchecked")
	public User authUser(User user){
		List<User> userList = this.find(
				"from User as u where u.name = ? and u.password = ? and locked = ?", 
				user.getName(),user.getPassword(),false);
		if(userList.size() == 1){
			return userList.get(0);
		}else{
			return null;
		}
	}
	
	public List<Privilege> getPrivileges(User authedUser){
		List<Privilege> userPrivileges = new ArrayList<Privilege>();
		Role role = authedUser.getRole();
		for(Privilege p:role.getPrivileges()){
			userPrivileges.add(p);
		}
		return userPrivileges;
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getUsersByPrivilegeCode(String privilegeCode){
		return this.find(
				"select u from User as u join u.roles as r join r.privileges as p" +
				" where p.code = '"+privilegeCode+"'");
	}

	public static Map<String, Integer> getUserLoginStatus() {
		return userLoginStatus;
	}

	public static void setUserLoginStatus(HashMap<String, Integer> userLoginStatus) {
		UserService.userLoginStatus = userLoginStatus;
	}

}
