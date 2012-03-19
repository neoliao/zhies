package com.fortunes.zhies.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import net.fortunes.core.Model;

@Entity
public class CodeSequence extends Model {
	
	@Id
	private long id;
	private long importSequence;
	private long exportSequence;
	private long produceAreaSequence;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getImportSequence() {
		return importSequence;
	}
	public void setImportSequence(long importSequence) {
		this.importSequence = importSequence;
	}
	public long getExportSequence() {
		return exportSequence;
	}
	public void setExportSequence(long exportSequence) {
		this.exportSequence = exportSequence;
	}
	public long getProduceAreaSequence() {
		return produceAreaSequence;
	}
	public void setProduceAreaSequence(long produceAreaSequence) {
		this.produceAreaSequence = produceAreaSequence;
	}

}
