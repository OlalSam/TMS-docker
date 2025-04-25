package com.ignium.tms;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

@ApplicationScoped
public class MailUtility {

    private final Session mailSession;

    private static final String COMPANY_SIGNATURE =
        "\n\n—\nTransport Management System\n123 Main St, Nairobi\nwww.transportms.co.ke";

    public MailUtility() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        final String username = "inyanzamutsotso255@gmail.com";
        final String password = "dsbxmzqaazkuexpg"; // Replace with your Gmail App Password

        mailSession = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void send(String to, String subject, String body) throws MessagingException {
        MimeMessage msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress("inyanzamutsotso255@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.setText(body); // plain text
        Transport.send(msg);
    }

    public void sendWithSignOff(String to, String body) throws MessagingException {
        String defaultSubject = "Notification from Transport Management System";
        String bodyWithSignOff = body + COMPANY_SIGNATURE;
        send(to, defaultSubject, bodyWithSignOff);
    }

    public void sendHtmlWithSignOff(String to, String subject, String htmlBody) throws MessagingException {
        String htmlSignature = "<br/><br/>—<br/><strong>Transport Management System</strong><br/>123 Main St, Nairobi<br/>"
                             + "<a href=\"https://www.transportms.co.ke\">www.transportms.co.ke</a>";
        MimeMessage msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress("inyanzamutsotso255@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);
        msg.setContent(htmlBody + htmlSignature, "text/html; charset=UTF-8");
        Transport.send(msg);
    }
}
