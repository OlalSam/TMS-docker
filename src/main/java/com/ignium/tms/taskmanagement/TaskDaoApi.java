/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.taskmanagement;

import com.ignium.tms.employee.Employee;
import com.ignium.tms.fleet.Vehicle;
import java.util.List;
import java.util.Map;

/**
 *
 * @author olal
 */
public interface TaskDaoApi {

    boolean saveTask(Task task);

    List<Task> findTaskByStatus(String status);

    List<Task> findAll();

    boolean updateTaskStatus(Long taskId, String status);

    boolean updateTask(Task task);

    void delete(Long taskId);

    Map<String, Integer> taskKpi();

    Task findById(Long taskId);

    List<Employee> drivers();

    List<Vehicle> vehicles();
    
    /**
     *
     * @param username
     * @return
     */
    List<Task> userTasks(String username);

}
