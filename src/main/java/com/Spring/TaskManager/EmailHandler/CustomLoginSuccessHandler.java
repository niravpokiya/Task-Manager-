package com.Spring.TaskManager.EmailHandler;

import com.Spring.TaskManager.EmailHandler.EmailSender;
import com.Spring.TaskManager.Entities.User;
import com.Spring.TaskManager.Services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private UserService userService;
    public CustomLoginSuccessHandler(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private EmailSender emailSender;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        try {
            System.out.println("Login success. Sending mail to: " + user.getEmail());
            emailSender.sendMail(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("/user-tasks");
    }
}
