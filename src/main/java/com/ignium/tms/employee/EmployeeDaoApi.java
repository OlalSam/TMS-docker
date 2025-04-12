/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.employee;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author olal
 */
public interface EmployeeDaoApi {
    // Create a new driver
    boolean saveEmployee(Employee employee);

    // Read (Get) a driver by ID
    Employee getEmployeeById(int employeeId);

    // Read (Get) all drivers
    List<Employee> getAllEmployee(int offset, int pageSize);

    // Update a driver's details
    boolean updateEmployee(Employee employee);

    // Delete a driver by ID
    boolean deleteEmployee(int employeeId);
    
    HashMap<String, Integer> employeeKpi();
}
