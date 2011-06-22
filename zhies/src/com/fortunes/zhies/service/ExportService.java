package com.fortunes.zhies.service;

import java.util.List;

import javax.annotation.Resource;

import com.fortunes.zhies.model.BusinessInstance;
import com.fortunes.zhies.model.Export;
import com.fortunes.zhies.model.Item;

import net.fortunes.core.service.GenericService;
import org.springframework.stereotype.Component;

@Component
public class ExportService extends GenericService<Export> {
	

	public void createExportInstance(Export export, List<Item> items,
			List<BusinessInstance> busis) throws Exception {
		add(export);
		
		for(Item i : items){
			i.setExport(export);
			this.getHt().save(i);
		}
		
		for(BusinessInstance b : busis){
			b.setExport(export);
			this.getHt().save(b);
		}
		
	}
	
}
