package com.fortunes.zhies.service;

import net.fortunes.core.service.GenericService;

import org.springframework.stereotype.Component;

import com.fortunes.zhies.model.CodeSequence;

@Component
public class CodeSequenceService extends GenericService<CodeSequence>{
	
	public long nextImportSequence(){
		CodeSequence s = this.get("1");
		long v = s.getImportSequence();
		s.setImportSequence(v+1);
		this.update(s);
		return v;
	}
	
	public long nextExportSequence(){
		CodeSequence s = this.get("1");
		long v = s.getExportSequence();
		s.setExportSequence(v+1);
		this.update(s);
		return v;
	}
	
	public long nextProduceAreaSequence(){
		CodeSequence s = this.get("1");
		long v = s.getProduceAreaSequence();
		s.setProduceAreaSequence(v+1);
		this.update(s);
		return v;
	}

}
