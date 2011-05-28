package com.fortunes.fjdp.admin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import net.fortunes.core.Model;

@Entity
public class Config extends Model {
	
	public static enum ConfigKey {
		APP_ROOT_DIR("应用根目录"),
		ADMIN_EMAIL("管理员邮箱"),
		TEMP_UPLOAD_DIR("临时上传文件夹(相对目录)"),
		NON_HOUSE_PAY_RATE("非住宅缴纳标准"),
		HOUSE_PAY_RATE("住宅缴纳标准"),
		ALERT_PERCENT("续缴预警百分比"),
		//PHOTO_DIR("人员照片文件夹","E:/app/photo"),
		FULL_PAY_DEVIATION_PERCENT("缴齐偏差百分比");
		
		private String label;
		
		private ConfigKey(String label) {
			this.label = label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		
	}
	@Id 
	@GeneratedValue
	private long id;
	
	@Enumerated(EnumType.STRING) @Column(nullable = false)
	private ConfigKey configKey;
	
	@Column(nullable = false)
	private String configValue;
	
	@Column(nullable = false)
	private String configLabel;
	
	@Column(nullable = false)
	private String lastValue;
	
	@Column(nullable = false)
	private String defaultValue;
	
	public Config() {
	}
	
	public Config(ConfigKey key,String value,String lastValue,String defaultValue) {
		this.setConfigKey(key);
		this.configLabel = key.getLabel();
		this.configValue = value;
		this.lastValue = lastValue;
		this.defaultValue = defaultValue;
		
	}

	@Override
	public String toString() {
		return "系统参数:"+getConfigKey();
	}
	
	//================================ setter and getter ====================================
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}


	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

	public void setConfigKey(ConfigKey configKey) {
		this.configKey = configKey;
	}

	public ConfigKey getConfigKey() {
		return configKey;
	}

	public void setLastValue(String lastValue) {
		this.lastValue = lastValue;
	}

	public String getLastValue() {
		return lastValue;
	}

	public void setConfigLabel(String configLabel) {
		this.configLabel = configLabel;
	}

	public String getConfigLabel() {
		return configLabel;
	}
	
}
