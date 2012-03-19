package com.fortunes.zhies.action;

import java.text.ParseException;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.fortunes.fjdp.AppHelper;
import com.fortunes.fjdp.admin.AdminHelper;
import net.fortunes.core.action.GenericAction;
import net.fortunes.core.service.GenericService;
import net.fortunes.util.PinYin;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fortunes.zhies.model.Buyer;
import com.fortunes.zhies.model.Customer;
import com.fortunes.zhies.service.BuyerService;

@Component @Scope("prototype")
public class BuyerAction extends GenericAction<Buyer> {
	
	private BuyerService buyerService;
	
	protected void setEntity(Buyer e) throws ParseException{
		e.setName(p("name"));
		e.setCode(p("code"));
		e.setAddress(p("address"));
		e.setEmail(p("email"));
		e.setTel(p("tel"));
		e.setFax(p("fax"));
		e.setQq(p("qq"));
		e.setLinkman(p("linkman"));
		e.setLinkmanTel(p("linkmanTel"));
		e.setLinkmanEmail(p("linkmanEmail"));
	}
	
	protected JSONObject toJsonObject(Buyer e) throws ParseException{
		AppHelper record = new AppHelper();
		record.put("id", e.getId());
		record.put("name", e.getName());
		record.put("code", e.getCode());
		record.put("address", e.getAddress());
		record.put("email", e.getEmail());
		record.put("tel", e.getTel());
		record.put("fax", e.getFax());
		record.put("qq", e.getQq());
		record.put("linkman", e.getLinkman());
		record.put("linkmanTel", e.getLinkmanTel());
		record.put("linkmanEmail", e.getLinkmanEmail());
		return record.getJsonObject();
	}
	
	public String getBuyers() throws Exception{
		List<Buyer> list = getDefService().findAll();
		JSONArray ja = new JSONArray();
		for(Buyer c:list){
			String namePy = PinYin.toPinYinString(c.getName());
			if(namePy.startsWith(getQuery().toUpperCase())
					|| c.getName().startsWith(getQuery())){
				JSONObject record = new JSONObject();
				record.put("id", c.getId());
				record.put("text", c.getName());
				record.put("pinyin", namePy);
				ja.add(record);
			}	
		}
		jo.put(DATA_KEY, ja);
		return render(jo); 
	}
	
	
	/*=============== setter and getter =================*/
	
	@Override
	public GenericService<Buyer> getDefService() {
		return buyerService;
	}
	
	public void setBuyerService(BuyerService buyerService) {
		this.buyerService = buyerService;
	}

	public BuyerService getBuyerService() {
		return buyerService;
	}

}
