package com.ignium.tms.fleet;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author olal
 */
public interface FleetDaoApi {
    boolean save(Vehicle vehicle); // Create

    Vehicle findByPlateNumber(String plateNumber); // Read

    List<Vehicle> findAll(int offset, int pageSize); // Read all

    boolean update(Vehicle vehicle); // Update

    void delete(String plateNumber); // Delete
    
    HashMap<String, Integer> fleetKpi();
}
