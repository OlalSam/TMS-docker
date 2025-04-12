/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.employee;

import com.ignium.tms.identityManager.Role;
import com.ignium.tms.identityManager.User;

/**
 *
 * @author olal
 */
public class Employee extends User {
    
    public Employee(){
        super();
    }

    public Employee(String username, String firstName, String secondName, String password, String email, Role role) {
        super(username, firstName, secondName, password, email, role);
    }

    public Employee(int userId, String username) {
        super(userId, username);
    }

    public Employee(int userId, String username, String firstName, String secondName, String password, String email, String phoneNumber, String status, Role role) {
        super(userId, username, firstName, secondName, password, email, phoneNumber, status, role);
    }

    public Employee(int userId, String username, String firstName, String secondName, String email, String phoneNumber, String status, Role role) {
        super(userId, username, firstName, secondName, email, phoneNumber, status, role);
    }
    
    
}
