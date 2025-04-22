/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.controller;

import com.ignium.tms.employee.Employee;
import com.ignium.tms.employee.EmployeeDao;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author olal
 */
@RequestScoped
@Path("/profile")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProfileController {
    @Inject
    private EmployeeDao emplService;

    @Inject
    private SecurityContext securityContext;

    /**
     * GET /profile/driverDetails
     * 
     * Returns the profile of the currently authenticated user.
     */
    @GET
    @Path("/driverDetails")
    public Response getMyProfile() {
        // obtain the logged-in username via Jakarta SecurityContext
        String username = securityContext.getCallerPrincipal().getName();
        Employee emp = emplService.findByUsername(username);
        if (emp == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"User not found\"}")
                    .build();
        }
        return Response.ok(emp).build();
    }

    /**
     * GET /profile/driverDetails/{username}
     * 
     * Returns the profile for any driver by username.
     * @param username
     * @return 
     */
    @GET
    @Path("/driverDetails/{username}")
    public Response getProfileByUsername(@PathParam("username") String username) {
        Employee emp = emplService.findByUsername(username);
        if (emp == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"User not found\"}")
                    .build();
        }
        return Response.ok(emp).build();
    }
    
}
