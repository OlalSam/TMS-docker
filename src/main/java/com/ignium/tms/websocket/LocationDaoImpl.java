/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.websocket;

import com.ignium.tms.LocationUpdate;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 *
 * @author olal
 */
public class LocationDaoImpl implements LocationDaoApi {

    @Resource(lookup = "java:global/tms")
    private DataSource dataSource;

    @Override
    public List<LocationUpdate> getDriverHistory(String username) {
        String sql = "SELECT latitude, longitude, accuracy, logged_at "
                + "FROM driver_locations "
                + "WHERE driver_username = ? "
                + "ORDER BY logged_at";
        List<LocationUpdate> history = new ArrayList<>();
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocationUpdate lu = new LocationUpdate();
                    lu.setDriverUsername(username);
                    lu.setLat(rs.getDouble("latitude"));
                    lu.setLng(rs.getDouble("longitude"));
                    lu.setAccuracy(rs.getFloat("accuracy"));
                    // if LocationUpdate had timestamp field, set it here
                    history.add(lu);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    @Override
    public void logLocation(LocationUpdate update) {
        String sql = "INSERT INTO driver_locations "
                + "(driver_username, latitude, longitude, accuracy, logged_at) "
                + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (var conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, update.getDriverUsername());
            ps.setDouble(2, update.getLat());
            ps.setDouble(3, update.getLng());
            ps.setFloat(4, update.getAccuracy());
            ps.executeUpdate();
        } catch (SQLException e) {
            // Consider async retry or error logging
            e.printStackTrace();
        }
    }

}
