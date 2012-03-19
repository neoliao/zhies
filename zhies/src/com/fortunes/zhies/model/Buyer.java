package com.fortunes.zhies.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fortunes.fjdp.admin.model.Dict;
import net.fortunes.core.Model;

@Entity
public class Buyer extends Company{
	
	
    public Buyer() {
    }
    
    public Buyer(long id) {
    	setId(id);
    }
    
    @Override
	public String toString() {
		return "";
	}

    
    /*=============== setter and getter =================*/
    

}
