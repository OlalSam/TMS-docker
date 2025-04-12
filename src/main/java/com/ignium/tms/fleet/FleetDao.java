package com.ignium.tms.fleet;

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

@RequestScoped
public class FleetDao implements FleetDaoApi{
    private static final Logger logger = Logger.getLogger(FleetDao.class.getName());

    @Resource(lookup = "java:global/tms") // Ensure JNDI matches your server settings
    private DataSource dataSource;

    // Save a new vehicle record.
    @Override
    public boolean save(Vehicle vehicle) {
        String sql = "INSERT INTO vehicle (plate_number, vehicle_model, vehicle_type, status) VALUES (?, ?, ?, ?)";
        boolean result = false;
        try (var conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vehicle.getPlateNumber());
            ps.setString(2, vehicle.getVehicleModel());
            ps.setString(3, vehicle.getVehicleType());
            ps.setString(4, vehicle.getStatus());

            int rows = ps.executeUpdate();
            result = rows > 0;
            return result;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error saving vehicle", ex);
        }
        return result;
    }

    // Find a vehicle by its plate number.
    @Override
    public Vehicle findByPlateNumber(String plateNumber) {
        String sql = "SELECT id, plate_number, vehicle_model, vehicle_type, status FROM vehicle WHERE plate_number = ?";
        Vehicle vehicle = null;
        try (var conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, plateNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    vehicle = new Vehicle();
                    vehicle.setId(rs.getLong("id"));
                    vehicle.setPlateNumber(rs.getString("plate_number"));
                    vehicle.setVehicleModel(rs.getString("vehicle_model"));
                    vehicle.setVehicleType(rs.getString("vehicle_type"));
                    vehicle.setStatus(rs.getString("status"));
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error finding vehicle by plate number", ex);
        }
        return vehicle;
    }

    // Retrieve a list of vehicles with pagination.
    @Override
    public List<Vehicle> findAll(int offset, int pageSize) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT id, plate_number, vehicle_model, vehicle_type, status FROM vehicle ORDER BY id DESC LIMIT ? OFFSET ?";
        try (var conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehicle vehicle = new Vehicle();
                    vehicle.setId(rs.getLong("id"));
                    vehicle.setPlateNumber(rs.getString("plate_number"));
                    vehicle.setVehicleModel(rs.getString("vehicle_model"));
                    vehicle.setVehicleType(rs.getString("vehicle_type"));
                    vehicle.setStatus(rs.getString("status"));
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error fetching all vehicles", ex);
        }
        return vehicles;
    }

    // Update an existing vehicle record.
    @Override
    public boolean update(Vehicle vehicle) {
        String sql = "UPDATE vehicle SET vehicle_model = ?, vehicle_type = ?, status = ? WHERE plate_number = ?";
        boolean result = false;
        try (var conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vehicle.getVehicleModel());
            ps.setString(2, vehicle.getVehicleType());
            ps.setString(3, vehicle.getStatus());
            ps.setString(4, vehicle.getPlateNumber());

            int rows = ps.executeUpdate();
            result = rows > 0;
            return result;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating vehicle", ex);
        }
        return result;
    }

    // Delete a vehicle record based on its plate number.
    @Override
    public void delete(String plateNumber) {
        String sql = "DELETE FROM vehicle WHERE plate_number = ?";
        try (var conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, plateNumber);
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error deleting vehicle", ex);
        }
    }

    // Get a list of vehicles that are available (status = 'available').
    public List<Vehicle> availableVehicles() {
        String sql = "SELECT id, plate_number, vehicle_model, vehicle_type, status FROM vehicle WHERE status = ?";
        List<Vehicle> vehicles = new ArrayList<>();
        try (var conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "available");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehicle vehicle = new Vehicle();
                    vehicle.setId(rs.getLong("id"));
                    vehicle.setPlateNumber(rs.getString("plate_number"));
                    vehicle.setVehicleModel(rs.getString("vehicle_model"));
                    vehicle.setVehicleType(rs.getString("vehicle_type"));
                    vehicle.setStatus(rs.getString("status"));
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error fetching available vehicles", ex);
        }
        return vehicles;
    }

    @Override
    public HashMap<String, Integer> fleetKpi() {
        HashMap<String, Integer> kpi = new HashMap<>();

        String sqlTotal = "SELECT COUNT(*) AS total FROM vehicle";
        String sqlByStatus = "SELECT status, COUNT(*) AS count FROM vehicle GROUP BY status";

        try (var conn = dataSource.getConnection();
             PreparedStatement psTotal = conn.prepareStatement(sqlTotal);
             PreparedStatement psByStatus = conn.prepareStatement(sqlByStatus)) {

            // Retrieve total vehicle count
            try (ResultSet rsTotal = psTotal.executeQuery()) {
                if (rsTotal.next()) {
                    kpi.put("TotalVehicles", rsTotal.getInt("total"));
                }
            }

            // Retrieve vehicle counts by status
            try (ResultSet rsByStatus = psByStatus.executeQuery()) {
                while (rsByStatus.next()) {
                    String status = rsByStatus.getString("status");
                    int count = rsByStatus.getInt("count");
                    kpi.put(status, count);
                }
            }

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving fleet KPIs", ex);
        }

        return kpi;
    }
}
