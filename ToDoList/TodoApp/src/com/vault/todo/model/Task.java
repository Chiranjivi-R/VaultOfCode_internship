package com.vault.todo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Task model class representing a single to-do item
 */
public class Task {
    private int id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDate dueDate;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    /**
     * Constructor for creating a new task
     */
    public Task(int id, String title, String description, Priority priority, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
        this.completedAt = null;
    }

    /**
     * Full constructor for loading from JSON
     */
    public Task(int id, String title, String description, Priority priority, LocalDate dueDate,
                Status status, LocalDateTime createdAt, LocalDateTime completedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setStatus(Status status) {
        this.status = status;
        if (status == Status.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        } else if (status == Status.PENDING) {
            this.completedAt = null;
        }
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    /**
     * Check if task is overdue
     */
    public boolean isOverdue() {
        return status == Status.PENDING && dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    /**
     * Check if task is due today
     */
    public boolean isDueToday() {
        return dueDate != null && dueDate.equals(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                ", status=" + status +
                '}';
    }
}


