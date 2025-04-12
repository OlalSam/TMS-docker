/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.fleet;


/**
 *
 * @author olal
 */
public class Vehicle {
    private Long id;
    private String plateNumber;
    private String vehicleModel;
    private String vehicleType;
    private String status;
    
    public Vehicle() {
    }

    public Vehicle(Long id, String plateNumber, String vehicleModel, String vehicleType, String status) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.vehicleModel = vehicleModel;
        this.vehicleType = vehicleType;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Vehicle{" + "id=" + id + ", plateNumber=" + plateNumber + ", vehicleModel=" + vehicleModel + ", vehicleType=" + vehicleType + ", status=" + status + '}';
    }
    
    
    
}
