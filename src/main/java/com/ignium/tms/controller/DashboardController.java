/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.controller;

import com.ignium.tms.employee.EmployeeDao;
import com.ignium.tms.fleet.FleetDao;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;
import java.util.HashMap;

/**
 *
 * @author olal
 */
@Named("dashboardBean")
@RequestScoped
public class DashboardController {

    @Inject
    private EmployeeDao employeeService;
    
    @Inject 
    private SecurityContext securityContext;

    @Inject
    private FleetDao fleetService;
    
    private String userRole;

    private String barChartJson;

    private int totalEmployees;

    private int activeEmployees;

    private int completeTask;

    private int totalVehicles;

    HashMap<String, Integer> employeeKpi;

    HashMap<String, Integer> fleetKpi;

    @PostConstruct
    public void init() {
        
        userRole = securityContext.isCallerInRole("ADMIN") ? "ADMIN" : "USER";

        employeeKpi = new HashMap<>();
        employeeKpi = employeeService.employeeKpi();

        activeEmployees = employeeKpi.get("activeEmployees");
        totalEmployees = employeeKpi.get("totalEmployees");

        fleetKpi = new HashMap<>();
        fleetKpi = fleetService.fleetKpi();
        totalVehicles = fleetKpi.get("TotalVehicles");

        int activeCount = fleetKpi.getOrDefault("Active", 0);
        int maintenanceCount = fleetKpi.getOrDefault("Maintenance", 0);
        int unavailableCount = fleetKpi.getOrDefault("Unavailable", 0);
        int offlineCount = fleetKpi.getOrDefault("Offline", 0);


        barChartJson =String.format("""
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
            """, activeCount, maintenanceCount, unavailableCount, offlineCount );
    }

    public String getBarChartJson() {
        return barChartJson;
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
