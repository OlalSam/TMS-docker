/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms;

import jakarta.annotation.Priority;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.CallerPrincipal;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.sql.ResultSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author olal
 */
@ApplicationScoped
@Priority(100)
public class AppIdentityStore implements IdentityStore {

    private static final Logger logger = Logger.getLogger(AppIdentityStore.class.getName());

    @Resource(lookup = "java:global/tms")
    private DataSource datasource;

    @Inject
    private Pbkdf2PasswordHash passwordHasher;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (!(credential instanceof UsernamePasswordCredential)) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        var upc = (UsernamePasswordCredential) credential;
        String username = upc.getCaller();
        String password = upc.getPasswordAsString();

        int result = verify(username, password);

        switch (result) {
            case 0: //failed vaidation
                return CredentialValidationResult.INVALID_RESULT;
            case 1:
                String group = getRole(username);
                return new CredentialValidationResult(new CallerPrincipal(username), Set.of(group));

            case -1: // SQL error during validation
                logger.log(Level.SEVERE, "SQL error during credential verification for user: {0}", username);
                return CredentialValidationResult.INVALID_RESULT;
            default:
                throw new IllegalArgumentException("Unexpected verification result: " + result);

        }
    }

    private int verify(String username, String password) {
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

    private String getRole(String username) {
        String query = "SELECT role FROM users WHERE username = ?";
        try (var conn = datasource.getConnection(); var ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                return role;
            }
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SQL error while fetching group for user: " + username, e);
            return null;
        }
    }
}
