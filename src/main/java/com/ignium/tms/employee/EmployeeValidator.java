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
        String fieldName = (String) component.getAttributes().get("fieldName");
        // Optionally, pass the employee ID to ignore the current record during update
        Object employeeIdObj = component.getAttributes().get("employeeId");

        if (fieldName != null) {
            if (fieldExists(fieldName, value, employeeIdObj)) {
                String message = switch (fieldName) {
                    case "username" -> "Username already exists. Please choose a different username.";
                    case "email" -> "Email already exists. Please use a different email.";
                    case "phoneNumber" -> "Phone number already exists. Please use a different phone number.";
                    default -> "Invalid field validation.";
                };
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
            }
        }
    }

    private boolean fieldExists(String fieldName, String fieldValue, Object employeeIdObj) {
        // If employeeIdObj is provided, assume it's an update.
        // Modify the SQL query to ignore the record with that employeeId.
        String sql;
        if (employeeIdObj != null) {
            sql = "SELECT COUNT(*) FROM users WHERE " + fieldName + " = ? AND id <> ?";
        } else {
            sql = "SELECT COUNT(*) FROM users WHERE " + fieldName + " = ?";
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, fieldValue);
            if (employeeIdObj != null) {
                // Assume employeeId is an Integer; adjust type if necessary.
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
