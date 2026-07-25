package com.devops.ems.service;

import com.devops.ems.entity.Employee;

import java.util.List;

public interface EmployeeService {

    List<Employee> getAllEmployees();

    Employee getEmployeeById(Long employeeId);

    Employee getEmployeeByEmail(String email);

    Employee saveEmployee(Employee employee);

    Employee updateEmployee(Long employeeId, Employee employee);

    void deleteEmployee(Long employeeId);

}
