package com.Spring.TaskManager.DTO;

import com.Spring.TaskManager.Enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
}
