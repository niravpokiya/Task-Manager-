package com.Spring.TaskManager.Services;

import com.Spring.TaskManager.DTO.TaskRequest;
import com.Spring.TaskManager.Entities.Status;
import com.Spring.TaskManager.Entities.Task;
import com.Spring.TaskManager.Entities.User;
import com.Spring.TaskManager.Repositories.StatusRepository;
import com.Spring.TaskManager.Repositories.TaskRepository;
import com.Spring.TaskManager.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class TaskServices {

    @Autowired
    private TaskRepository tasks;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    public Task saveTask(TaskRequest taskRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Status status = statusRepository.findByName(taskRequest.getStatus());
        if (status == null) {
            throw new RuntimeException("Status not found please provide a valid status.");
        }
        Task newTask = new Task();
        newTask.setTitle(taskRequest.getTitle());
        newTask.setUser(user);
        newTask.setStatus(status);
        newTask.setDescription(taskRequest.getDescription());

        return tasks.save(newTask);
    }

    @Transactional
    public String updateTask(int id, TaskRequest updaterequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "User not found";
        }
        Task task = tasks.findById(id).orElse(null);
        if(task == null) {
            return "Task not found";
        }

        if (task.getUser().getId() != user.getId()) {
            return "You are not allowed to update this task";
        }

        // ✅ Validate and set status
        Status status = statusRepository.findByName(updaterequest.getStatus());
        if (status == null) {
            return "Status not found please provide a valid status.";
        }

        task.setTitle(updaterequest.getTitle());
        task.setDescription(updaterequest.getDescription());
        task.setStatus(status);
        task.setUpdatedTime(new Date());

        return "success";
    }

    public List<Task> getTasksByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return tasks.findByUser(user);
    }
    public List<Task> getTasks(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return tasks.findByUser(user);
    }
    @Transactional
    public String deleteTask(int id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "User not found";
        }
        Task task = tasks.findById(id).orElse(null);

        if (task == null) {
            return "Task not found";
        }
        if (user.getId() != task.getUser().getId()) {
            return "You cannot delete this task.";
        }
        tasks.delete(task);
        return "success";
    }
    public Task getTaskById(int id) {
        return taskRepository.findById(id).orElse(null);
    }
}
