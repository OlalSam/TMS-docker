/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.taskmanagement;

/**
 *
 * @author olal
 */
public class Task {

    private Long id;
    private Integer userId;
    private Integer vehicleId;
    private String driverName;
    private String numberPlate;
    private String title;
    private String notes;
    private String pickupDesination;
    private String destinationLocation;
    private String status;

    public Task() {
    }

    //create task in DAO
    public Task(Integer userId, Integer vehicleId, String title, String notes, String pickupDesination, String destinationLocation, String status) {
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.title = title;
        this.notes = notes;
        this.pickupDesination = pickupDesination;
        this.destinationLocation = destinationLocation;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPickupDesination() {
        return pickupDesination;
    }

    public void setPickupDesination(String pickupDesination) {
        this.pickupDesination = pickupDesination;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
