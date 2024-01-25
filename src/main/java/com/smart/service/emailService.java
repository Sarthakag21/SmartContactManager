package com.smart.service;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class emailService {

    private final Environment environment;

    public emailService(Environment environment) {
        this.environment = environment;
    }

    public boolean sendEmail(String message, String subject, String to) {
        boolean f=false;
        String from = environment.getProperty("USERNAME");
        String host="smtp.gmail.com";
        //get  System properties
        Properties pro=	System.getProperties();
        System.out.println("properties"+pro);
        //setting important info to prooperties object

        pro.put("mail.smtp.host",host);
        pro.put("mail.smtp.port","465");
        pro.put("mail.smtp.ssl.enable","true");
        pro.put("mail.smtp.auth","true");
        Session session=Session.getInstance(pro,new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(environment.getProperty("USERNAME"), environment.getProperty("PASSWORD"));
            }
        });
        session.setDebug(true);
        MimeMessage mm=new MimeMessage(session);
        try {
            mm.setFrom(from);
            mm.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            mm.setSubject(subject);
//            mm.setText(message);
            mm.setContent(message, "text/html");
            Transport.send(mm);
            f=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }
}
