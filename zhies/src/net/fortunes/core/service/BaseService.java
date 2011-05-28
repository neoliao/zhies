package net.fortunes.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * 所有Service的基类
 * @author Neo.Liao
 *
 */
public abstract class BaseService{
	
	protected final Logger logger = LoggerFactory.getLogger("ROOT");
	
}
