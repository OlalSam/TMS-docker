/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.employee;

import com.ignium.tms.identityManager.Role;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author olal
 */
public class EmployeeDao implements EmployeeDaoApi, Serializable {

    private static final Logger logger = Logger.getLogger(EmployeeDao.class.getName());

    @Resource(lookup = "java:global/tms")
    private DataSource dataSource;

    @Inject
    private Pbkdf2PasswordHash passwordHasher;

    @Override
    public boolean saveEmployee(Employee employee) {
        String sql = "INSERT INTO users (username, email, password, first_name, second_name, phone_number, role, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        boolean result = false;
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employee.getUsername());
            ps.setString(2, employee.getEmail());
            var passwordHash = passwordHasher.generate(employee.getPassword().toCharArray());
            ps.setString(3, passwordHash);
            ps.setString(4, employee.getFirstName());
            ps.setString(5, employee.getSecondName());
            ps.setString(6, employee.getPhoneNumber());
            ps.setString(7, "USER");
            ps.setString(8, employee.getStatus());

            result = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error saving employee", ex);
        }
        return result;
    }

    @Override
    public Employee getEmployeeById(int employeeId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Employee employee = null;

        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    employee = new Employee(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("first_name"),
                            rs.getString("second_name"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("status"),
                            Role.valueOf(rs.getString("role"))
                    );
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving employee by ID", ex);
        }

        return employee;
    }

    @Override
    public List<Employee> getAllEmployee(int offset, int pageSize) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, username, first_name, second_name, email, role, phone_number, status FROM users WHERE role = 'USER' AND status = 'ACTIVE' ORDER BY id DESC LIMIT ? OFFSET ?";

        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employee employee = new Employee(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("first_name"),
                            rs.getString("second_name"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("status"),
                            Role.valueOf(rs.getString("role"))
                    );
                    employees.add(employee);
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error fetching all employees with pagination", ex);
        }

        return employees;
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE users SET username = ?, email = ?, password = ?, first_name = ?, second_name = ?, phone_number = ?, role = ?, status = ? WHERE id = ?";
        boolean result = false;
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employee.getUsername());
            ps.setString(2, employee.getEmail());
            ps.setString(3, employee.getPassword());
            ps.setString(4, employee.getFirstName());
            ps.setString(5, employee.getSecondName());
            ps.setString(6, employee.getPhoneNumber());
            ps.setString(7, employee.getRole().name());
            ps.setString(8, employee.getStatus());
            ps.setInt(9, employee.getUserId());

            result = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating employee", ex);
        }
        return result;
    }

    @Override
    public boolean deleteEmployee(int employeeId) {
        String sql = "UPDATE users SET status = 'INACTIVE' WHERE id = ?";
        boolean result = false;
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            result = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error setting employee status to inactive", ex);
        }

        return result;
    }

    @Override
    public HashMap<String, Integer> employeeKpi() {
        HashMap<String, Integer> kpi = new HashMap<>();

        // SQL queries to count total and active employees
        String sqlTotal = "SELECT COUNT(*) FROM users WHERE role = 'USER'";
        String sqlActive = "SELECT COUNT(*) FROM users WHERE role = 'USER' AND status = 'ACTIVE'";

        try (var conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {

            // Execute query for total employees
            try (ResultSet rsTotal = stmt.executeQuery(sqlTotal)) {
                if (rsTotal.next()) {
                    int totalEmployees = rsTotal.getInt(1);
                    kpi.put("totalEmployees", totalEmployees);
                }
            }

            // Execute query for active employees
            try (ResultSet rsActive = stmt.executeQuery(sqlActive)) {
                if (rsActive.next()) {
                    int activeEmployees = rsActive.getInt(1);
                    kpi.put("activeEmployees", activeEmployees);
                }
            }

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving employee KPIs", ex);
        }

        return kpi;
    }

    public Employee getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        Employee employee = null;

        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    employee = new Employee(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("first_name"),
                            rs.getString("second_name"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("status"),
                            Role.valueOf(rs.getString("role"))
                    );
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving employee by ID", ex);
        }

        return employee;
    }

    public Employee findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        Employee employee = null;

        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    employee = new Employee(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("first_name"),
                            rs.getString("second_name"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("status"),
                            Role.valueOf(rs.getString("role"))
                    );
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving employee by ID", ex);
        }

        return employee;
    }

    public Employee findAdmin() {
        String sql = """
        SELECT
          id,
          username,
          first_name,
          second_name,
          email,
          phone_number,
          status,
          role
        FROM users
        WHERE role = ?
        LIMIT 1
        """;
        Employee admin = null;

        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // bind the ADMIN role name safely
            ps.setString(1, Role.ADMIN.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    admin = new Employee(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("first_name"),
                            rs.getString("second_name"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("status"),
                            Role.valueOf(rs.getString("role"))
                    );
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving admin user", ex);
        }

        return admin;
    }

}
