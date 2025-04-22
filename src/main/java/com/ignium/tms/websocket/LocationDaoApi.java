/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.websocket;

import com.ignium.tms.LocationUpdate;
import java.util.List;

/**
 *
 * @author olal
 */
public interface LocationDaoApi {
    
    List<LocationUpdate> getDriverHistory(String username);
    
    void logLocation(LocationUpdate update);
    
    
}
