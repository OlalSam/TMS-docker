/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.controller;

/**
 *
 * @author olal
 */
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")   // this makes all JAX‑RS resources live under /api/*
public class RestApplication extends Application {
    // no other methods needed
}
