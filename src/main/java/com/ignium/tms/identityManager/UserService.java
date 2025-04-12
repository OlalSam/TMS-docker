/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.identityManager;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author olal
 */
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Resource(lookup = "java:global/tms")
    private DataSource datasource;

    @Inject
    private Pbkdf2PasswordHash passwordHasher;

    //create new user method 
    public boolean newUser(User user) {
        String query = "INSERT INTO users(username,  password, email, role, first_name, second_name) VALUES(?, ?, ?, ?,?,?)";
        try (var conn = datasource.getConnection(); var ps = conn.prepareStatement(query)) {

            ps.setString(1, user.getUsername());
            var passwordHash = passwordHasher.generate(user.getPassword().toCharArray());
            ps.setString(2, passwordHash);
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getGroup().name());
            ps.setString(5, user.getFirstName());
            ps.setString(6, user.getSecondName());
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL error during new user cration", e);
            return false;
        }
    }


     public User getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        User user = null;

        try (var conn = datasource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
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
            logger.log(Level.SEVERE, "Error retrieving driver by ID", ex);
        }

        return user;
    }
     
     public boolean updateProfile(User user) {
        String sql = "UPDATE users SET email = ?, first_name = ?, second_name = ?, phone_number = ? WHERE id = ?";
        boolean result = false;
        try (var conn = datasource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getSecondName());
            ps.setString(4, user.getPhoneNumber());
            ps.setInt(5, user.getUserId());

            ps.executeUpdate();
            result = ps.executeUpdate() > 0;
            return result;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating driver", ex);
        }
        return result;
    }
    
    public boolean updatePassword(User user) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        boolean result = false;
        try (var conn = datasource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            var passwordHash = passwordHasher.generate(user.getPassword().toCharArray());
            ps.setString(1, passwordHash);
            
            ps.setInt(2, user.getUserId());

            ps.executeUpdate();
            result = ps.executeUpdate() > 0;
            return result;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating driver", ex);
        }
        return result;
    }

    public int verifyOldPassword(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
            try (var conn = datasource.getConnection(); var ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return 0;
            }

            String dbPassword = rs.getString("password");
            if (!passwordHasher.verify(password.toCharArray(), dbPassword)) {
                return 0;
            }

            return 1;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "SQL error during verification", e);
            return -1;
        }
    }
}
