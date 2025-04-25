/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.controller;

import com.ignium.tms.employee.EmployeeDao;
import com.ignium.tms.fleet.FleetDao;
import com.ignium.tms.taskmanagement.TaskDaoApi;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author olal
 */
@Named("dashboardBean")
@ViewScoped
public class DashboardController implements Serializable {

    @Inject
    private EmployeeDao employeeService;

    @Inject
    private SecurityContext securityContext;

    @Inject
    private FleetDao fleetService;

    @Inject
    private TaskDaoApi taskService;
    

    private String userRole;

    private String barChartJson;

    private int totalEmployees;

    private int activeEmployees;

    private int completeTask;

    private int totalVehicles;

    HashMap<String, Integer> employeeKpi;

    HashMap<String, Integer> fleetKpi;

    Map<String, Integer> taskKpi;

    private int totalTasks;

    private String taskChartJson;
    
    private String username;

    public String getUsername() {
        username = securityContext.getCallerPrincipal().getName();
        return username;
    }
    

    @PostConstruct
    public void init() {

        userRole = securityContext.isCallerInRole("ADMIN") ? "ADMIN" : "USER";

        employeeKpi = new HashMap<>();
        employeeKpi = employeeService.employeeKpi();

        activeEmployees = employeeKpi.get("activeEmployees");
        totalEmployees = employeeKpi.get("totalEmployees");

        taskKpi = taskService.taskKpi();
        totalTasks = taskKpi.values().stream().mapToInt(Integer::intValue).sum();

        List<String> statuses = new ArrayList<>(taskKpi.keySet());
        List<Integer> counts = statuses.stream()
                .map(taskKpi::get)
                .toList();
        completeTask = taskKpi.getOrDefault("COMPLETED", 0);

        fleetKpi = new HashMap<>();
        fleetKpi = fleetService.fleetKpi();
        totalVehicles = fleetKpi.get("TotalVehicles");

        int activeCount = fleetKpi.getOrDefault("Active", 0);
        int maintenanceCount = fleetKpi.getOrDefault("Maintenance", 0);
        int unavailableCount = fleetKpi.getOrDefault("Unavailable", 0);
        int offlineCount = fleetKpi.getOrDefault("Offline", 0);

        barChartJson = String.format("""
            {
                "type": "bar",
                "data": {
                    "labels": ["Available", "Maintenance", "Unavailable", "Offline"],
                    "datasets": [{
                        "label": "Vehicle Count",
                        "data": [%d, %d, %d, %d],
                        "backgroundColor": [
                            "rgba(75, 192, 192, 0.6)",
                            "rgba(255, 159, 64, 0.6)",
                            "rgba(255, 99, 132, 0.6)",
                            "rgba(153, 102, 255, 0.6)"
                        ],
                        "borderColor": [
                            "rgba(75, 192, 192, 1)",
                            "rgba(255, 159, 64, 1)",
                            "rgba(255, 99, 132, 1)",
                            "rgba(153, 102, 255, 1)"
                        ],
                        "borderWidth": 1
                    }]
                },
                "options": {
                    "responsive": true,
                    "maintainAspectRatio": false,
                    "plugins": {
                        "legend": {
                            "display": false
                        }
                    },
                    "scales": {
                        "y": {
                            "beginAtZero": true,
                            "ticks": {
                                "precision": 0
                            }
                        }
                    }
                }
            }
            """, activeCount, maintenanceCount, unavailableCount, offlineCount);

        String labelsJson = statuses.stream()
                .map(status -> "\"" + status + "\"")
                .collect(Collectors.joining(", ", "[", "]"));

// Convert counts to a JSON array string
        String countsJson = counts.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));
        taskChartJson = String.format("""
            {
              "type": "doughnut",
              "data": {
                "labels": %s,
                "datasets": [{
                  "data": %s,
                  "backgroundColor": [
                    "rgba(54, 162, 235, 0.6)",
                    "rgba(255, 206, 86, 0.6)",
                    "rgba(75, 192, 192, 0.6)",
                    "rgba(255, 99, 132, 0.6)"
                  ],
                  "borderWidth": 1
                }]
              },
              "options": {
                "responsive": true,
                "plugins": {
                  "legend": { "position": "bottom" }
                }
              }
            }
            """,
                labelsJson, // e.g. ["PENDING","IN_TRANSIT","COMPLETED"]
                countsJson // e.g. [5,10,3]
        );

    }

    public String getAdminBar(){
        return barChartJson;
    }
    public String getBarChartJson() {
        return barChartJson;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public String getTaskChartJson() {
        return taskChartJson;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public int getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(int totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public int getActiveEmployees() {
        return activeEmployees;
    }

    public void setActiveEmployees(int activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public int getCompleteTask() {
        return completeTask;
    }

    public void setCompleteTask(int completeTask) {
        this.completeTask = completeTask;
    }

}
