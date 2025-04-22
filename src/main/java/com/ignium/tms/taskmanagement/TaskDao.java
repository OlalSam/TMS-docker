/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.taskmanagement;

import com.ignium.tms.employee.Employee;
import com.ignium.tms.fleet.Vehicle;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author olal
 */
@RequestScoped
public class TaskDao implements TaskDaoApi {

    private static final Logger logger = Logger.getLogger(TaskDao.class.getName());

    @Resource(lookup = "java:global/tms")
    private DataSource dataSource;

    @Override
    public boolean saveTask(Task task) {
        String sql = "INSERT INTO transport_tasks (user_id, vehicle_id, title, notes, pickup_location, destination_location, tracking_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        boolean result = false;
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, task.getUserId());
            ps.setInt(2, task.getVehicleId());
            ps.setString(3, task.getTitle());
            ps.setString(4, task.getNotes());
            ps.setString(5, task.getPickupDesination());
            ps.setString(6, task.getDestinationLocation());
            ps.setString(7, task.getStatus().toUpperCase());

            result = ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }

        return result;
    }

    @Override
    public List<Task> findTaskByStatus(String status) {
        String sql = "SELECT t.id, t.user_id, t.vehicle_id, t.title, t.notes, "
                + "t.pickup_location, t.destination_location, t.tracking_status, "
                + "u.first_name, u.second_name, v.plate_number "
                + "FROM transport_tasks t "
                + "JOIN users u ON t.user_id = u.id "
                + "JOIN vehicle v ON t.vehicle_id = v.id "
                + "WHERE t.tracking_status = ?";
        List<Task> list = new ArrayList<>();
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Task t = mapRowToTask(rs);
                    list.add(t);
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error finding tasks by status: {0}", ex.getMessage());
        }
        return list;
    }

    @Override
    public Task findById(Long taskId) {
        String sql
                = "SELECT t.id, t.user_id, t.vehicle_id, t.title, t.notes, "
                + "       t.pickup_location, t.destination_location, t.tracking_status, "
                + "       u.first_name, u.second_name, v.plate_number "
                + "FROM transport_tasks t "
                + "JOIN users u ON t.user_id = u.id "
                + "JOIN vehicle v ON t.vehicle_id = v.id "
                + "WHERE t.id = ?";
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTask(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding task by ID", e);
        }
        return null;
    }

    @Override
    public List<Task> findAll() {
        String sql = "SELECT t.id, t.user_id, t.vehicle_id, t.title, t.notes, "
                + "t.pickup_location, t.destination_location, t.tracking_status, "
                + "u.first_name, u.second_name, v.plate_number "
                + "FROM transport_tasks t "
                + "JOIN users u ON t.user_id = u.id "
                + "JOIN vehicle v ON t.vehicle_id = v.id";
        List<Task> list = new ArrayList<>();
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Task t = mapRowToTask(rs);
                list.add(t);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error finding all tasks: {0}", ex.getMessage());
        }
        return list;
    }

    @Override
    public List<Task> userTasks(String username) {
        String sql = "SELECT t.id, t.user_id, t.vehicle_id, t.title, t.notes, "
                + "t.pickup_location, t.destination_location, t.tracking_status, "
                + "u.first_name, u.second_name, v.plate_number "
                + "FROM transport_tasks t "
                + "JOIN users u ON t.user_id = u.id "
                + "JOIN vehicle v ON t.vehicle_id = v.id "
                + "WHERE u.username = ?";
        List<Task> list = new ArrayList<>();
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Task t = mapRowToTask(rs);
                    list.add(t);
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error finding tasks for user: {0}", ex.getMessage());
        }
        return list;
    }

    @Override
    public boolean updateTaskStatus(Long taskId, String status) {
        String sql = "UPDATE transport_tasks SET tracking_status = ? WHERE id = ?";
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, taskId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating task status: {0}", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateTask(Task task) {
        String sql = "UPDATE transport_tasks SET user_id = ?, vehicle_id = ?, title = ?, notes = ?, "
                + "pickup_location = ?, destination_location = ?, tracking_status = ? WHERE id = ?";
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, task.getUserId());
            ps.setInt(2, task.getVehicleId());
            ps.setString(3, task.getTitle());
            ps.setString(4, task.getNotes());
            ps.setString(5, task.getPickupDesination());
            ps.setString(6, task.getDestinationLocation());
            ps.setString(7, task.getStatus());
            ps.setLong(8, task.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating task: {0}", ex.getMessage());
            return false;
        }
    }

    @Override
    public void delete(Long taskId) {
        String sql = "DELETE FROM transport_tasks WHERE id = ?";
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error deleting task: {0}", ex.getMessage());
        }
    }

    @Override
    public Map<String, Integer> taskKpi() {
        String sql = "SELECT tracking_status, COUNT(*) AS cnt FROM transport_tasks GROUP BY tracking_status";
        Map<String, Integer> kpi = new HashMap<>();
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                kpi.put(rs.getString("tracking_status"), rs.getInt("cnt"));
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error computing task KPI: {0}", ex.getMessage());
        }
        return kpi;
    }

    /**
     * Maps a ResultSet row to a Task POJO, including driverName and
     * numberPlate.
     */
    private Task mapRowToTask(ResultSet rs) throws SQLException {
        Task t = new Task();
        t.setId(rs.getLong("id"));
        t.setUserId(rs.getInt("user_id"));
        t.setVehicleId(rs.getInt("vehicle_id"));
        t.setTitle(rs.getString("title"));
        t.setNotes(rs.getString("notes"));
        t.setPickupDesination(rs.getString("pickup_location"));
        t.setDestinationLocation(rs.getString("destination_location"));
        t.setStatus(rs.getString("tracking_status").toUpperCase());
        String fname = rs.getString("first_name");
        String sname = rs.getString("second_name");
        t.setDriverName((fname != null ? fname : "") + " " + (sname != null ? sname : ""));
        t.setNumberPlate(rs.getString("plate_number"));
        return t;
    }

    @Override
    public List<Employee> drivers() {
        List<Employee> drivers = new ArrayList<>();
        String sql = "SELECT id, username FROM users WHERE role = 'USER'";

        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employee driver = new Employee(
                            rs.getInt("id"),
                            rs.getString("username")
                    );

                    drivers.add(driver);
                }
            }

        } catch (SQLException e) {
        }

        return drivers;

    }

    @Override
    public List<Vehicle> vehicles() {
        String sql = "SELECT * FROM vehicle WHERE status = ?";
        List<Vehicle> vehicles = new ArrayList<>();

        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "Active");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehicle vehicle = new Vehicle();
                    vehicle.setId(rs.getLong("id"));
                    vehicle.setPlateNumber(rs.getString("plate_number"));
                    vehicle.setVehicleModel(rs.getString("vehicle_model"));
                    vehicle.setVehicleType(rs.getString("vehicle_type"));
                    vehicles.add(vehicle);
                }
            }

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error", ex);
        }

        return vehicles;
    }

}
