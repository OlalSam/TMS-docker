package com.ignium.tms.fleet;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.naming.InitialContext;
import javax.sql.DataSource;

@FacesValidator("fleetValidator")
public class FleetValidator implements Validator<String> {

    private DataSource dataSource;

    public FleetValidator() {
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
        // Optionally pass a vehicleId attribute to indicate an update operation
        Object vehicleId = component.getAttributes().get("vehicleId");

        if (fieldName != null) {
            if (fieldExists(fieldName, value, vehicleId)) {
                String message;
                switch (fieldName) {
                    case "plate_number":
                        message = "Plate number already exists. Please choose a different plate number.";
                        break;
                    // Add other cases if needed
                    default:
                        message = "Invalid field validation.";
                }
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
            }
        }
    }

    private boolean fieldExists(String fieldName, String fieldValue, Object vehicleId) {
        String sql;
        if (vehicleId != null) {
            // For update operations, exclude the current record from duplicate check
            sql = "SELECT COUNT(*) FROM vehicle WHERE " + fieldName + " = ? AND id <> ?";
        } else {
            sql = "SELECT COUNT(*) FROM vehicle WHERE " + fieldName + " = ?";
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fieldValue);
            if (vehicleId != null) {
                ps.setObject(2, vehicleId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Database error while validating " + fieldName, e);
        }
    }
}
