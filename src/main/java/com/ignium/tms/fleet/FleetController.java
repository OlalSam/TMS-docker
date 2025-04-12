package com.ignium.tms.fleet;

import com.ignium.tms.MessageUtility;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.Flash;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

/**
 *
 * @author olal
 */
@Named("fleetController")
@ViewScoped
public class FleetController extends LazyDataModel<Vehicle> implements Serializable {

    @Inject
    private MessageUtility message;

    @Inject
    private FleetDao fleetDao;

    @Inject
    private Flash flash;

    private Vehicle newVehicle;
    private Vehicle selectedVehicle;

    @PostConstruct
    public void init() {
        newVehicle = new Vehicle();
        if(selectedVehicle == null){
            selectedVehicle = (Vehicle) flash.get("selectedVehicle");
        }
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        // Implement your count logic if needed.
        return 0;
    }

    @Override
    public List<Vehicle> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        var vehicleList = fleetDao.findAll(offset, pageSize);
        int count = vehicleList.isEmpty() ? 0 : vehicleList.size();
        setRowCount(count);
        return vehicleList;
    }

    public Vehicle getNewVehicle() {
        return newVehicle;
    }

    public void setNewVehicle(Vehicle newVehicle) {
        this.newVehicle = newVehicle;
    }

    public Vehicle getSelectedVehicle() {
        return selectedVehicle;
    }

    public void setSelectedVehicle(Vehicle selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
    }

    public String addVehicle() {
        boolean result = fleetDao.save(newVehicle);
        if (result) {
            message.addSuccessMessage("Vehicle added successfully");
            flash.setKeepMessages(true);
            return "fleet?faces-redirect=true";
        }
        message.addErrorMessage("Error adding vehicle. Try again.");
        return "vehicleForm.xhtml";
    }

    /**
     * Fetches vehicle details by plate number and loads them into the form.
     *
     * @param plateNumber
     * @return
     */
    public String editVehicle(String plateNumber) {
        selectedVehicle = fleetDao.findByPlateNumber(plateNumber);
        if (selectedVehicle != null) {
            flash.put("selectedVehicle", selectedVehicle);
            return "vehicleUpdateForm.xhtml?faces-redirect=true";
        } else {
            message.addErrorMessage("Vehicle not found.");
            return "fleet.xhtml?faces-redirect=true";
        }
    }

    /**
     * Updates the vehicle details.
     */
    public String updateVehicle() {
        boolean result = fleetDao.update(selectedVehicle);
        if (result) {
            message.addSuccessMessage("Vehicle updated successfully");
            flash.setKeepMessages(true);
            return "fleet.xhtml?faces-redirect=true";
        }
        message.addErrorMessage("Error updating vehicle. Try again.");
        return "vehicleForm.xhtml";
    }
}
