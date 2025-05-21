package com.Spring.TaskManager.Services;

import com.Spring.TaskManager.DTO.RegisterRequest;
import com.Spring.TaskManager.Entities.User;
import com.Spring.TaskManager.Enums.Role;
import com.Spring.TaskManager.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username already taken.";
        }
        if(userRepository.existsByEmail(request.getEmail())) {
            return "Email already taken.";
        }
        if(request.getUsername() == null
        || request.getUsername().isEmpty() || request.getPassword() == null || request.getPassword().isEmpty()||
        request.getEmail() == null || request.getEmail().isEmpty()) {
            return "Username or password or email cannot be empty!";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 🔹 Encrypt password
        user.setRole(Role.USER);
        user.setEmail(request.getEmail());
        userRepository.save(user);
        return "success";
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())) // 🔹 Convert role (USER → ROLE_USER)
        );
    }

    public User FindUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public List<User> usersList() {
        return userRepository.findAll();
    }

    public User removeUser(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if(user.getRole().name().equals("ADMIN")){
            throw new RuntimeException("You cannot delete admin !");
        }
        userRepository.delete(user);

        return user;
    }
}
