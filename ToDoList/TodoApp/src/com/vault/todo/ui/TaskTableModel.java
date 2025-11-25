package com.vault.todo.ui;

import com.vault.todo.model.Task;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for displaying tasks in JTable
 */
public class TaskTableModel extends AbstractTableModel {
    private List<Task> tasks;
    private final String[] columnNames = {"ID", "Title", "Description", "Priority", "Due Date", "Status", "Created At"};
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TaskTableModel() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Set tasks and convert from LinkedList to ArrayList
     */
    public void setTasks(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
        fireTableDataChanged();
    }

    /**
     * Add a task
     */
    public void addTask(Task task) {
        tasks.add(task);
        fireTableRowsInserted(tasks.size() - 1, tasks.size() - 1);
    }

    /**
     * Remove a task
     */
    public void removeTask(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < tasks.size()) {
            tasks.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    /**
     * Update a task
     */
    public void updateTask(int rowIndex, Task task) {
        if (rowIndex >= 0 && rowIndex < tasks.size()) {
            tasks.set(rowIndex, task);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }

    /**
     * Get task at row index
     */
    public Task getTask(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < tasks.size()) {
            return tasks.get(rowIndex);
        }
        return null;
    }

    /**
     * Get all tasks
     */
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task task = tasks.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                return task.getId();
            case 1:
                return task.getTitle();
            case 2:
                return task.getDescription();
            case 3:
                return task.getPriority();
            case 4:
                return task.getDueDate() != null ? task.getDueDate().format(dateFormatter) : "";
            case 5:
                return task.getStatus();
            case 6:
                return task.getCreatedAt() != null ? task.getCreatedAt().format(dateTimeFormatter) : "";
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Integer.class;
            default:
                return String.class;
        }
    }
}


