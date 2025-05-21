package com.Spring.TaskManager.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(length = 500, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdTime = new Date();
        this.updatedTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedTime = new Date();
    }
}
