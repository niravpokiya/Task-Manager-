package com.Spring.TaskManager.Controllers;

import com.Spring.TaskManager.DTO.RegisterRequest;
import com.Spring.TaskManager.DTO.TaskRequest;
import com.Spring.TaskManager.Entities.Status;
import com.Spring.TaskManager.Entities.Task;
import com.Spring.TaskManager.Entities.User;
import com.Spring.TaskManager.Repositories.StatusRepository;
import com.Spring.TaskManager.Services.TaskServices;
import com.Spring.TaskManager.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
public class WebController {
    @Autowired
    UserService userService;
    @Autowired
    TaskServices taskServices;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    StatusRepository statusRepository;

    @GetMapping("/Login")
    public String Login() {
        return "Login";
    }
    @PostMapping("/Login-success")
    public String LoginSuccess(@ModelAttribute("user") User us) {
        return "user-tasks";
    }
    @PostMapping("/logout")
    public String logout(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        return "redirect:/Login";
    }
    @GetMapping("/Register")
    public String Register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "Register";
    }

    @PostMapping("/RegisterUser")
    public String RegisterUser(@ModelAttribute RegisterRequest registerRequest,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {

        String response = userService.registerUser(registerRequest);

        if (response.equals("success")) {
            UserDetails userDetails = userService.loadUserByUsername(registerRequest.getUsername());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, registerRequest.getPassword(), userDetails.getAuthorities());

            Authentication auth = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 🔑 This is what makes it actually persist the login
            request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return "redirect:/user-tasks";
        } else {
            redirectAttributes.addFlashAttribute("Response", response);
            redirectAttributes.addFlashAttribute("registerRequest", registerRequest);
            return "redirect:Register";
        }
    }

    @GetMapping("/user-tasks")
    public String userTasks(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        model.addAttribute("username", username);
        List<Task> tasks = taskServices.getTasks(username);
        User user = userService.findByUsername(username);
        tasks.sort(Comparator.comparing(task -> task.getStatus().getId()));
        model.addAttribute("user", user);
        model.addAttribute("tasks", tasks);
        return "user-tasks";
    }

    @GetMapping("/add-task")
    public String addTask(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("task", new TaskRequest());
        List<Status> statuses = statusRepository.findAll();
        model.addAttribute("statuses", statuses);
        return "add-task";
    }
    @PostMapping("/create-task")
    public String createTask(@ModelAttribute TaskRequest taskRequest, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        taskServices.saveTask(taskRequest);
        return "redirect:/user-tasks";
    }
    @GetMapping("/delete-task")
    public String deleteTask(@AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes
                            , @RequestParam int id) {
        String response = taskServices.deleteTask(id);
        if(response.equals("success")) {
            return "redirect:/user-tasks";
        }
        else {
            redirectAttributes.addFlashAttribute("Response", response);
//            redirectAttributes.addFlashAttribute("deleteRequest", id);
            return "redirect:/user-tasks";
        }
    }

    @GetMapping("/update-request")
    public String updateTask(@AuthenticationPrincipal UserDetails userDetails, Model model, @RequestParam int id, RedirectAttributes redirectAttributes) {
        Task task = taskServices.getTaskById(id);
        if(task == null) {
            redirectAttributes.addFlashAttribute("Response", "Task not found");
            return "redirect:/user-tasks";
        }
        List<Status> statuses = statusRepository.findAll();
        model.addAttribute("statuses", statuses);
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle(task.getTitle());
        taskRequest.setDescription(task.getDescription());
        taskRequest.setStatus(task.getStatus().getName());
        model.addAttribute("id", id);
        model.addAttribute("taskRequest", taskRequest);
        return "update-task";
    }
    @PostMapping("/update-task")
    public String UpdateTask(@ModelAttribute TaskRequest taskRequest, RedirectAttributes redirectAttributes,@RequestParam int id) {
        String Response = taskServices.updateTask(id, taskRequest);
        if(Response.equals("success")) {
            redirectAttributes.addFlashAttribute("success", "success");
            return "redirect:/user-tasks";
        }
        else {
            redirectAttributes.addFlashAttribute("Response", Response);
            return "redirect:/user-tasks";
        }
    }

    @GetMapping("/view-task")
    public String viewTask(@RequestParam int id,  Model model) {
        Task task = taskServices.getTaskById(id);
        model.addAttribute("task", task);
        return "view-task";
    }
}
