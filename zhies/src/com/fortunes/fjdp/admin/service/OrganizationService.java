package com.fortunes.fjdp.admin.service;

import java.util.List;

import javax.annotation.Resource;

import net.fortunes.core.log.annotation.LoggerClass;
import net.fortunes.core.service.GenericService;

import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.model.Employee;
import com.fortunes.fjdp.admin.model.Organization;

@Component
@LoggerClass
public class OrganizationService extends GenericService<Organization> {
	
	public static final boolean SINGLE_ORGANIZATION = true;
	
	@Resource private EmployeeService employeeService;
	
	
	@Override
	public void add(Organization entity) throws Exception {
		
		super.add(entity);
		if(entity.getParent() != null)
			entity.getParent().setLeaf(false);
	}
	
	@Override
	public void del(Organization entity) throws Exception {
		Organization parent = entity.getParent();
		super.del(entity);
		if(parent != null && parent.getChildren().size() <= 0)
			parent.setLeaf(true);
	}
	
	public void addEmployee(String organizationId,String employeeId){
		Organization organization = this.get(organizationId);
		Employee employee = employeeService.get(employeeId);
		//在组织中加入员工
		organization.getEmployees().add(employee);
	}
	
	public void removeEmployee(String organizationId,String employeeId){
		Organization organization = this.get(organizationId);
		Employee employee = employeeService.get(employeeId);
		
		//从组织中移除员工
		organization.getEmployees().remove(employee);
		this.getHt().flush();
	}

	/**
	 * 查找不属于某一组织的员工
	 * @param organizationId
	 * @return
	 */
	public List<Employee> getUnassignEmployeesByOrganizationId(String organizationId) {
		return this.getHt().find(
				" select e from Employee as e left join fetch e.user where e not in " +
				" (select oe from Organization as o join o.employees as oe where o.id =  ?)", 
				Long.parseLong(organizationId));
	}
	
	/**
	 * 查找不属于任何组织的员工(所有未分配的员工)
	 * @return
	 */
	public List<Employee> getUnassignEmployees() {
		return this.getHt().find(
				" select e from Employee as e left join fetch e.organization as o where o is null" );
	}
    
}
