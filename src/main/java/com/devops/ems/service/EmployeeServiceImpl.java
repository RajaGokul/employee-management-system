package com.devops.ems.service;

import com.devops.ems.entity.Employee;
import com.devops.ems.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId).orElse(null);
    }

    @Override
    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long employeeId, Employee employee) {

        Employee existingEmployee = employeeRepository.findById(employeeId).orElse(null);

        if (existingEmployee != null) {

            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());
            existingEmployee.setEmail(employee.getEmail());
            existingEmployee.setDepartment(employee.getDepartment());
            existingEmployee.setDesignation(employee.getDesignation());
            existingEmployee.setSalary(employee.getSalary());
            existingEmployee.setPhoneNumber(employee.getPhoneNumber());
            existingEmployee.setStatus(employee.getStatus());
            existingEmployee.setJoiningDate(employee.getJoiningDate());

            return employeeRepository.save(existingEmployee);
        }

        return null;
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        employeeRepository.deleteById(employeeId);
    }

}
