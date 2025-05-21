package com.Spring.TaskManager.DTO;

import com.Spring.TaskManager.Entities.Status;
import lombok.Data;

@Data
public class TaskRequest {
    public String title;
    public String description;
    public String status;
}
