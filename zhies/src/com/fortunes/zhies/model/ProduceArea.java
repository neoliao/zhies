package com.fortunes.zhies.model;

import javax.persistence.Entity;

@Entity
public class ProduceArea extends Trade {

	public ProduceArea() {
	}

	public ProduceArea(long id) {
		setId(id);
	}

	@Override
	public String toString() {
		return "";
	}

	/* =============== setter and getter ================= */

}
