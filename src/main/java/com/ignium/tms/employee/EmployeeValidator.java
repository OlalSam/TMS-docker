package com.ignium.tms.employee;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.naming.InitialContext;

@FacesValidator("employeeValidator")
public class EmployeeValidator implements Validator<String> {

    private DataSource dataSource;

    public EmployeeValidator() {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:global/tms");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize data source", e);
        }
    }

    @Override
    public void validate(FacesContext context, UIComponent component, String value) throws ValidatorException {
        if (value == null || value.trim().isEmpty()) {
            return; // Let required validator handle empty values
        }
        
        String fieldName = (String) component.getAttributes().get("fieldName");
        Object employeeIdObj = component.getAttributes().get("employeeId");
        
        if (fieldName == null) {
            return; // No field name specified, nothing to validate
        }

        // Check for unique constraints in database
        try {
            if (fieldExists(fieldName, value, employeeIdObj)) {
                String fieldLabel = getFieldLabel(fieldName);
                String message = fieldLabel + " already exists. Please choose a different " + fieldLabel.toLowerCase() + ".";
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
            }
        } catch (RuntimeException e) {
            // If it's not our custom exception, it's probably a DB error
            if (!(e instanceof ValidatorException)) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "A system error occurred while validating this field. Please try again later.", null));
            }
            throw e;
        }
        
        // Additional field-specific validations
        switch (fieldName) {
            case "username":
                if (value.length() < 4) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Username must be at least 4 characters long", null));
                }
                if (!value.matches("^[a-zA-Z0-9_]+$")) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Username can only contain letters, numbers, and underscores", null));
                }
                break;
                
            case "email":
                if (!value.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Please enter a valid email address", null));
                }
                break;
                
            case "phoneNumber":
                if (!value.matches("^[0-9]{10,15}$")) {
                    throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Phone number must contain 10-15 digits only", null));
                }
                break;
        }
    }

    private String getFieldLabel(String fieldName) {
        return switch (fieldName) {
            case "username" -> "Username";
            case "email" -> "Email";
            case "phoneNumber" -> "Phone number";
            default -> fieldName;
        };
    }

    private boolean fieldExists(String fieldName, String fieldValue, Object employeeIdObj) {
        // Map field names to database column names
        String dbColumn = switch (fieldName) {
            case "phoneNumber" -> "phone_number";
            default -> fieldName;
        };
        
        String sql;
        if (employeeIdObj != null) {
            sql = "SELECT COUNT(*) FROM users WHERE " + dbColumn + " = ? AND id <> ?";
        } else {
            sql = "SELECT COUNT(*) FROM users WHERE " + dbColumn + " = ?";
        }
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, fieldValue);
            if (employeeIdObj != null) {
                ps.setObject(2, employeeIdObj);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Database error while validating " + fieldName, e);
        }
    }
}
