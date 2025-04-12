    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.websocket;

import com.ignium.tms.MessageUtility;
import com.ignium.tms.LocationUpdate;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author olal
 */
@ServerEndpoint(value = "/tracking",
        configurator = SecurityConfigurator.class)
public class TrackingEndpoint {

    // Store driver sessions with driverId as key
    private static final Map<String, Session> driverSessions
            = new ConcurrentHashMap<>();

    // Store admin sessions
    private static final Set<Session> adminSessions
            = Collections.synchronizedSet(new HashSet<>());

   

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        Map<String, Object> props = config.getUserProperties();
        String username = (String) props.get("username");
        String role = (String) props.get("role");

        if (username != null && role != null) {
            if ("USER".equals(role)) {
                driverSessions.put(username, session);
                System.out.println("User session loaded for: " + username);
            } else if ("ADMIN".equals(role)) {
                adminSessions.add(session);
                System.out.println("Admin session loaded for: " + username);
            }
        } else {
            System.out.println("Username or role not found in session during onOpen.");
        }

    }

    @OnMessage
    public void onMessage(Session session, String message) {
        String username = (String) session.getUserProperties().get("username");
        String role = (String) session.getUserProperties().get("role");

        if (username == null || !"USER".equals(role)) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY,
                        "Unauthorized message sender"));
                return;
            } catch (IOException ex) {
                System.out.println("Error");
            }
        }

        LocationUpdate update = MessageUtility.jsonToLocation(message);
        System.out.println(update);
        update.setDriverId(username);

        // JDBC logging
        // Broadcast to admins
        String adminMessage = MessageUtility.locationToJson(update);
        broadcastToAdmins(adminMessage);
        System.out.println(adminMessage);
    }

    @OnClose
    public void onClose(Session session) {
        String username = (String) session.getUserProperties().get("username");
        String role = (String) session.getUserProperties().get("role");
        if (username != null && role != null) {
            if ("USER".equals(role)) {
                driverSessions.remove(username);
                System.out.println("User session closed for: " + username);
            } else if ("ADMIN".equals(role)) {
                adminSessions.remove(session);
                System.out.println("Admin session closed for: " + username);
            }
        } else {
            System.out.println("Username or role not found during onClose.");
        }
    }

    private void broadcastToAdmins(String message) {
        adminSessions.forEach(s -> {
            if (s.isOpen()) {
                s.getAsyncRemote().sendText(message);
                System.out.println("Broadcast to admins");
            }
        });
    }

}
