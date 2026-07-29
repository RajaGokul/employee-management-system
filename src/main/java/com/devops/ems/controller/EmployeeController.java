package com.devops.ems.controller;

import com.devops.ems.entity.Employee;
import com.devops.ems.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Dashboard
    @GetMapping("/")
    public String dashboard(Model model) {

        List<Employee> employees = employeeService.getAllEmployees();

        long totalEmployees = employees.size();

        long activeEmployees = employees.stream()
                .filter(employee -> "ACTIVE".equalsIgnoreCase(employee.getStatus()))
                .count();

        long inactiveEmployees = employees.stream()
                .filter(employee -> "INACTIVE".equalsIgnoreCase(employee.getStatus()))
                .count();

        model.addAttribute("employees", employees);
        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("activeEmployees", activeEmployees);
        model.addAttribute("inactiveEmployees", inactiveEmployees);

        return "dashboard";
    }

    // Show Add Employee Form
    @GetMapping("/employees/new")
    public String showAddEmployeeForm(Model model) {

        Employee employee = new Employee();

        model.addAttribute("employee", employee);

        return "add-employee";
    }

    // Save Employee
    @PostMapping("/employees")
    public String saveEmployee(@ModelAttribute("employee") Employee employee) {

        employeeService.saveEmployee(employee);

        return "redirect:/";
    }

    // Show Edit Employee Form
    @GetMapping("/employees/edit/{id}")
    public String showEditEmployeeForm(@PathVariable Long id, Model model) {

        Employee employee = employeeService.getEmployeeById(id);

        model.addAttribute("employee", employee);

        return "edit-employee";
    }

    // Update Employee
    @PostMapping("/employees/update/{id}")
    public String updateEmployee(@PathVariable Long id,
                                 @ModelAttribute("employee") Employee employee) {

        employeeService.updateEmployee(id, employee);

        return "redirect:/";
    }

    // Delete Employee
    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {

        employeeService.deleteEmployee(id);

        return "redirect:/";
    }
}
