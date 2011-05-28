package net.fortunes.core.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.fortunes.core.ListData;
import net.fortunes.core.log.annotation.LoggerMethod;
import net.fortunes.util.GenericsUtil;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * 一个泛型Service,针对一个Entity提供一系列模板方法．
 * @param <E>　Entity的类型
 * @author Neo
 */
public abstract class GenericService<E>{
	
//	@Resource
//	private GenericDao<E> defDao;
	
	@Resource private HibernateTemplate hibernateTemplate;   
      
	
	private Class<E> entityClass;
	
	@Resource private TransactionTemplate transactionTemplate;
	@Resource private JdbcTemplate jdbcTemplate;
	
	/**
	 * 子类初始时根据子类的泛型参数决定Entity的类型,这个构造函数不能直接调用
	 */
	protected GenericService(){
		this.entityClass = GenericsUtil.getGenericClass(getClass());
	}
	
	@LoggerMethod(operateName = "新增")
	public void add(E entity) throws Exception{
		this.getHt().save(entity);
	}
	
	@LoggerMethod(operateName = "删除")
	public void del(E entity) throws Exception{
		this.getHt().delete(entity);
	}
	
	@LoggerMethod(operateName = "修改")
	public void update(E entity){
		this.getHt().update(entity);
	}
	
	public void addOrUpdate(E entity){
		this.getHt().saveOrUpdate(entity);
	}
	
	public E get(String id){
		return StringUtils.isEmpty(id) ? null : (E)this.getHt().get(entityClass,getPk(id));
	}
	
	public List<E> findAll(){
		return this.getHt().findByCriteria(DetachedCriteria.forClass(this.entityClass));
	}
	
	public ListData<E> getListData(String query,Map<String,String> queryMap,int start,int limit){
		ListData<E> listData = null;
		//不分页
		if(limit == 0){
			listData = getListData(getConditions(query,queryMap));
		//分页
		}else{
			listData = getListData(getConditions(query,queryMap),start,limit);
		}
		return listData;
	}
	
	
	
	
	
	
	/**
	 * override以改变数据集的默认排序方式
	 * @return
	 */
	protected Order getOrder(){
		return Order.desc("id");
	}
	
	
	
	
	/**
	 * override用来过滤数据集
	 * @param query
	 * @param queryMap
	 * @return
	 */
	protected DetachedCriteria getConditions(String query,Map<String,String> queryMap){
		return DetachedCriteria.forClass(this.entityClass);
	}
	
	public List<E> find(String hql, Object... objects) {
		return this.getHt().find(hql, objects);
	}
	
	public E findUnique(String hql, Object... objects) {
		List<E> list = find(hql,objects);
		return  (list != null && list.size() >0) ? list.get(0) : null;
	}
	
	public E getRoot(){
		return this.findUnique(
				"from "+entityClass.getSimpleName()+" as e where e.parent is null");
	}
	
	public int delAll(){
		return this.getHt().bulkUpdate("delete from "+entityClass.getSimpleName());
	}
	
	//========= private methods =========
	
	/**
	 * 转换为主键的值，这种实现会有问题
	 * @param id
	 * @return 实体List
	 */
	private Serializable getPk(String id) {
		try {
			long longPk = Long.valueOf(id);
			return longPk;
		} catch (NumberFormatException e) {
			return id;
		}
	}
	
	private int getTotal(DetachedCriteria conditions) {
		return (Integer)this.getHt().findByCriteria(
				conditions.setProjection(Projections.rowCount())).iterator().next();
	}
	
	private ListData<E> getListData(DetachedCriteria criteria,int start,int limit){
		int total = getTotal(criteria);
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.ROOT_ENTITY);
		if(getOrder() != null){
			criteria.addOrder(getOrder());
		}
		List<E> list = this.getHt().findByCriteria(criteria,start, limit);
		return new ListData<E>(list,total);
	}
	
	private ListData<E> getListData(DetachedCriteria criteria){
		if(getOrder() != null){
			criteria.addOrder(getOrder());
		}
		List<E> list = this.getHt().findByCriteria(criteria);
		int total = list.size();
		return new ListData<E>(list,total);
	}
	
	private ListData<E> getListData(){
		return getListData(getConditions(null,null),0,0);
	}
	
	protected HibernateTemplate getHt(){
		return hibernateTemplate;
	}

	public Class<E> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

}
