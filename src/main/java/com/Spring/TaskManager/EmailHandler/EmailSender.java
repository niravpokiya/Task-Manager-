package com.Spring.TaskManager.EmailHandler;
import com.Spring.TaskManager.Entities.User;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class EmailSender {
    public void sendMail(User u) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("freelancingbynp@gmail.com", "viyrhptybsvljxcx");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("freelancingbynp@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(u.getEmail()));
        message.setSubject("Login detected !");
        message.setText("Hey dear " + u.getUsername() +" ! new device logged in into NotesCatcher app!");

        try {
            Transport.send(message);
            System.out.println("Mail sent!!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
}
