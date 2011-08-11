package com.fortunes.zhies.model;

import javax.persistence.Entity;

@Entity
public class Import extends Trade{
	
	//代交关税
	private Double valueAddedTax;
	private Double consumeTax;
	private Double delayFee;
	
    public Import() {
    }
    
    public Import(long id) {
    	setId(id);
    }
    
    @Override
	public String toString() {
		return "";
	}

	public Double getValueAddedTax() {
		return valueAddedTax;
	}

	public void setValueAddedTax(Double valueAddedTax) {
		this.valueAddedTax = valueAddedTax;
	}

	public Double getConsumeTax() {
		return consumeTax;
	}

	public void setConsumeTax(Double consumeTax) {
		this.consumeTax = consumeTax;
	}

	public Double getDelayFee() {
		return delayFee;
	}

	public void setDelayFee(Double delayFee) {
		this.delayFee = delayFee;
	}
    
    /*=============== setter and getter =================*/
    


}
