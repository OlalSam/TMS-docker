/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.controller;

import com.ignium.tms.MessageUtility;
import com.ignium.tms.identityManager.User;
import com.ignium.tms.identityManager.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;
import java.io.Serializable;

/**
 *
 * @author olal
 */
@Named("settings")
@RequestScoped
public class SettingsController implements Serializable {

    private User currentUser;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    @Inject
    private SecurityContext securityContext;

    @Inject
    private MessageUtility message;

    @Inject
    private UserService userService;

    @PostConstruct
    public void innit() {
        currentUser = userService.getUser(securityContext.getCallerPrincipal().getName());

    }

    public void updateProfile() {
        if (currentUser != null && userService.updateProfile(currentUser)) {
            message.addSuccessMessage("Prifile updated successfully");
        } else {
            message.addErrorMessage("Try Again");
        }
    }

    public void updatePassword() {
        if (confirmPassword.equals(newPassword)) {
            var match = userService.verifyOldPassword(currentUser.getUsername(), oldPassword);
            if (match == 1) {
                userService.updatePassword(currentUser);
                message.addSuccessMessage("Password changed ");
            }else{
                message.addErrorMessage("Old password did not match");
            }
            
        } else {
            message.addErrorMessage("Password dont match");
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
