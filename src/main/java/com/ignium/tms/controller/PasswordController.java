/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.controller;

import com.ignium.tms.MailUtility;
import com.ignium.tms.employee.EmployeeDao;
import com.ignium.tms.employee.Employee;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.mail.MessagingException;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olal
 */
@Named("forgotPassword")
@RequestScoped
public class PasswordController implements Serializable{
    
     @Inject
    private MailUtility mailService;

    @Inject
    private EmployeeDao userDao;

    @Inject
    private Pbkdf2PasswordHash passwordHasher;

    // Keep track of valid reset codes (in a real app, persist these in a table!)
    private static final Map<String,String> resetCodes = new ConcurrentHashMap<>();

    private String email;
    private String code;
    private String newPassword;

    // 1. User submits email → generate & send code
    public void requestReset() throws IOException {
        Employee user = userDao.getUserByEmail(email);
        if (user == null) {
            FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getFlash().put("error", "No account found for that email");
            FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("forgotPassword.xhtml");
            return;
        }

        // Generate a 6‑digit numeric code
        SecureRandom rnd = new SecureRandom();
        int number = rnd.nextInt(900_000) + 100_000;
        String pin = String.valueOf(number);

        // Store it in memory (keyed by email)
        resetCodes.put(email, pin);

        // Send email
        String body = "Your password reset code is: " + pin;
         try {
             mailService.sendWithSignOff(email, body);
         } catch (MessagingException ex) {
             Logger.getLogger(PasswordController.class.getName()).log(Level.SEVERE, null, ex);
         }

        FacesContext.getCurrentInstance()
                    .getExternalContext().getFlash()
                    .put("info", "Reset code sent to your email");
        FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("enterResetCode.xhtml");
    }

    // 2. User submits code + new password → verify, update
    public void submitNewPassword() throws IOException {
        String validPin = resetCodes.get(email);
        if (validPin == null || !validPin.equals(code)) {
            FacesContext.getCurrentInstance().getExternalContext().getFlash()
                        .put("error", "Invalid reset code");
            FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("enterResetCode.xhtml");
            return;
        }

        // Hash the new password
        Map<String,String> params = Map.of(
            "Pbkdf2PasswordHash.Iterations", "310000",
            "Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA256"
        );
        passwordHasher.initialize(params);
        String hashed = passwordHasher.generate(newPassword.toCharArray());

        // Update user record
        Employee user = userDao.getUserByEmail(email);
        user.setPassword(hashed);
        userDao.updateEmployee(user);

        // Clean up
        resetCodes.remove(email);

        FacesContext.getCurrentInstance().getExternalContext().getFlash()
                    .put("success", "Password successfully reset");
        FacesContext.getCurrentInstance().getExternalContext()
                    .redirect("login.xhtml");
    }

    // getters & setters
    public String getEmail()        { return email; }
    public void setEmail(String e)  { this.email = e; }
    public String getCode()         { return code; }
    public void setCode(String c)   { this.code = c; }
    public String getNewPassword()  { return newPassword; }
    public void setNewPassword(String p) { this.newPassword = p; }
    
    
}
