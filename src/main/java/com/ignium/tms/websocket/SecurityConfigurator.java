/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.websocket;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

/**
 *
 * @author olal
 */
public class SecurityConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec,
            HandshakeRequest request,
            HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        sec.getUserProperties().put("session", httpSession);
        if (httpSession != null) {
            Object username = httpSession.getAttribute("username");
            Object role = httpSession.getAttribute("role");
            if (username != null) {
                sec.getUserProperties().put("username", username.toString());
            }
            if (role != null) {
                sec.getUserProperties().put("role", role.toString());
            }
        }
    }
}
