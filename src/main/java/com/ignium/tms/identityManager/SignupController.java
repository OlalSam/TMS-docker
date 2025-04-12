package com.ignium.tms.identityManager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 *
 * @author olal
 */
@Named("signup")
@RequestScoped
public class SignupController {

    private String username;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String secondName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    private String email;
    private String role;

    @Inject
    private UserService userService;

    @Inject
    private FacesContext facesContext;

    @Inject
    private Flash flash;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String signup() {
        User user = new User(this.username,this.firstName, this.secondName, this.password, this.email, Role.valueOf(this.role));
        boolean success = userService.newUser(user);
        if (success) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "User created successfully. You can now login.", null));
            flash.setKeepMessages(true);
            return "login.xhtml?faces-redirect=true";
        }
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while signing up", null));
        return "signup.xhtml";
    }
}
