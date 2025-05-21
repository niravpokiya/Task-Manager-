package com.Spring.TaskManager.Controllers;

import com.Spring.TaskManager.Entities.User;
import com.Spring.TaskManager.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<User> Users() {
        return userService.usersList();
    }

    @DeleteMapping("/remove/{id}")
    public User removeUser(@PathVariable int id) {
        return userService.removeUser(id);
    }
}
