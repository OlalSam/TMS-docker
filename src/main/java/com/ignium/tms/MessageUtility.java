/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;


/**
 *
 * @author olal
 */
@Named
@RequestScoped
public class MessageUtility {

    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesMessage facesMsg = new FacesMessage(severity, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
    }

    public void addErrorMessage(String msg) {
        addMessage(FacesMessage.SEVERITY_ERROR, "Error", msg);
    }

    public void addSuccessMessage(String msg) {
        addMessage(FacesMessage.SEVERITY_INFO, "Success", msg);
    }

    public void addInfoMessage(String msg) {
        addMessage(FacesMessage.SEVERITY_INFO, "Info", msg);
    }

    public void addWarningMessage(String msg) {
        addMessage(FacesMessage.SEVERITY_WARN, "Warning", msg);
    }

    private static final Jsonb jsonb = JsonbBuilder.create();

    public static String locationToJson(LocationUpdate update) {
        return jsonb.toJson(update);
    }

    public static LocationUpdate jsonToLocation(String json) {
        return jsonb.fromJson(json, LocationUpdate.class);
    }
}
