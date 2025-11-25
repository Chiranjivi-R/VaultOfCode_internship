package com.vault.todo.ui;

import com.vault.todo.model.Priority;
import com.vault.todo.model.Status;
import com.vault.todo.model.Task;
import com.vault.todo.storage.StorageUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main GUI class for the To-Do List application
 */
public class TodoGUI extends JFrame {
    private LinkedList<Task> tasks;
    private TaskTableModel tableModel;
    private JTable taskTable;
    private JLabel totalTasksLabel;
    private JLabel pendingTasksLabel;
    private JLabel completedTasksLabel;
    private JLabel overdueTasksLabel;
    private JComboBox<String> filterComboBox;
    private JComboBox<String> sortComboBox;
    private int nextId = 1;

    public TodoGUI() {
        tasks = StorageUtils.loadTasks();
        
        // Find the highest ID to set nextId
        for (Task task : tasks) {
            if (task.getId() >= nextId) {
                nextId = task.getId() + 1;
            }
        }

        initializeGUI();
        updateTable();
        updateDashboard();
    }

    /**
     * Initialize the GUI components
     */
    private void initializeGUI() {
        setTitle("Ultra-Professional To-Do List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        content.setBackground(new Color(0xF7F7F9)); // Main background
        setContentPane(content);

        // Top: Dashboard Panel
        add(createDashboardPanel(), BorderLayout.NORTH);

        // Center: Table with scroll pane
        add(createTablePanel(), BorderLayout.CENTER);

        // Right: Action Buttons
        add(createButtonPanel(), BorderLayout.EAST);

        // Bottom: Filter and Sort controls
        add(createFilterSortPanel(), BorderLayout.SOUTH);

        setSize(1200, 700);
        setLocationRelativeTo(null);
    }

    /**
     * Create dashboard panel with statistics
     */
    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel(new GridLayout(1, 4, 10, 10));
        dashboard.setBorder(BorderFactory.createTitledBorder("Dashboard"));
        dashboard.setBackground(new Color(0xF7F7F9));

        totalTasksLabel = new JLabel("Total: 0", JLabel.CENTER);
        totalTasksLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        totalTasksLabel.setOpaque(true);
        totalTasksLabel.setBackground(new Color(0xE3F2FD)); // Light blue
        totalTasksLabel.setBorder(BorderFactory.createRaisedBevelBorder());

        pendingTasksLabel = new JLabel("Pending: 0", JLabel.CENTER);
        pendingTasksLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        pendingTasksLabel.setOpaque(true);
        pendingTasksLabel.setBackground(new Color(0xFFF3E0)); // Soft orange
        pendingTasksLabel.setBorder(BorderFactory.createRaisedBevelBorder());

        completedTasksLabel = new JLabel("Completed: 0", JLabel.CENTER);
        completedTasksLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        completedTasksLabel.setOpaque(true);
        completedTasksLabel.setBackground(new Color(0xE8F5E9)); // Soft green
        completedTasksLabel.setBorder(BorderFactory.createRaisedBevelBorder());

        overdueTasksLabel = new JLabel("Overdue: 0", JLabel.CENTER);
        overdueTasksLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        overdueTasksLabel.setOpaque(true);
        overdueTasksLabel.setBackground(new Color(0xFFEBEE)); // Soft red
        overdueTasksLabel.setBorder(BorderFactory.createRaisedBevelBorder());

        dashboard.add(totalTasksLabel);
        dashboard.add(pendingTasksLabel);
        dashboard.add(completedTasksLabel);
        dashboard.add(overdueTasksLabel);

        return dashboard;
    }

    /**
     * Create table panel with custom renderer
     */
    private JPanel createTablePanel() {
        tableModel = new TaskTableModel();
        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setRowHeight(25);
        taskTable.getTableHeader().setReorderingAllowed(false);
        taskTable.setSelectionBackground(new Color(0x42A5F5)); // Blue accent for selection

        // Custom cell renderer for color coding
        taskTable.setDefaultRenderer(Object.class, new TaskTableCellRenderer());

        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Tasks"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xF7F7F9));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Create button panel with all action buttons
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        buttonPanel.setBackground(new Color(0xF2E7FE)); // Soft purple sidebar
        buttonPanel.add(Box.createVerticalStrut(10));

        JButton addButton = new JButton("Add Task");
        addButton.setBackground(new Color(0x66BB6A)); // Green
        addButton.setForeground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.addActionListener(e -> showAddTaskDialog());
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createVerticalStrut(5));

        JButton editButton = new JButton("Edit Task");
        editButton.setBackground(new Color(0x42A5F5)); // Blue
        editButton.setForeground(Color.WHITE);
        editButton.setOpaque(true);
        editButton.setBorderPainted(false);
        editButton.addActionListener(e -> showEditTaskDialog());
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createVerticalStrut(5));

        JButton deleteButton = new JButton("Delete Task");
        deleteButton.setBackground(new Color(0xEF5350)); // Red
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.addActionListener(e -> deleteSelectedTask());
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createVerticalStrut(5));

        JButton completeButton = new JButton("Mark Complete");
        completeButton.setBackground(new Color(0x2E7D32)); // Deep green
        completeButton.setForeground(Color.WHITE);
        completeButton.setOpaque(true);
        completeButton.setBorderPainted(false);
        completeButton.addActionListener(e -> markTaskComplete());
        buttonPanel.add(completeButton);
        buttonPanel.add(Box.createVerticalStrut(5));

        JButton importButton = new JButton("Import JSON");
        importButton.setBackground(new Color(0xFB8C00)); // Orange
        importButton.setForeground(Color.WHITE);
        importButton.setOpaque(true);
        importButton.setBorderPainted(false);
        importButton.addActionListener(e -> importTasks());
        buttonPanel.add(importButton);
        buttonPanel.add(Box.createVerticalStrut(5));

        JButton exportButton = new JButton("Export JSON");
        exportButton.setBackground(new Color(0x8E24AA)); // Purple
        exportButton.setForeground(Color.WHITE);
        exportButton.setOpaque(true);
        exportButton.setBorderPainted(false);
        exportButton.addActionListener(e -> exportTasks());
        buttonPanel.add(exportButton);

        buttonPanel.add(Box.createVerticalGlue());

        return buttonPanel;
    }

    /**
     * Create filter and sort panel
     */
    private JPanel createFilterSortPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Filter & Sort"));
        panel.setBackground(new Color(0xF7F7F9));

        panel.add(new JLabel("Filter:"));
        String[] filterOptions = {"All Tasks", "Only Pending", "Only Completed", "Only Overdue", "Due Today"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.addActionListener(e -> applyFilterAndSort());
        panel.add(filterComboBox);

        panel.add(Box.createHorizontalStrut(20));
        panel.add(new JLabel("Sort:"));
        String[] sortOptions = {"By Priority", "By Due Date", "By Title"};
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.addActionListener(e -> applyFilterAndSort());
        panel.add(sortComboBox);

        return panel;
    }

    /**
     * Show dialog to add a new task
     */
    private void showAddTaskDialog() {
        JDialog dialog = createTaskDialog(null, "Add New Task");
        dialog.setVisible(true);
    }

    /**
     * Show dialog to edit selected task
     */
    private void showEditTaskDialog() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Task task = tableModel.getTask(selectedRow);
        if (task != null) {
            JDialog dialog = createTaskDialog(task, "Edit Task");
            dialog.setVisible(true);
        }
    }

    /**
     * Create task input dialog
     */
    private JDialog createTaskDialog(Task taskToEdit, String title) {
        JDialog dialog = new JDialog(this, title, true);
        JPanel dlgContent = new JPanel(new BorderLayout(10, 10));
        dlgContent.setBorder(new EmptyBorder(10, 10, 10, 10));
        dialog.setContentPane(dlgContent);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField titleField = new JTextField(20);
        if (taskToEdit != null) titleField.setText(taskToEdit.getTitle());
        formPanel.add(titleField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        if (taskToEdit != null) descArea.setText(taskToEdit.getDescription());
        formPanel.add(new JScrollPane(descArea), gbc);

        // Priority
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        formPanel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JComboBox<Priority> priorityCombo = new JComboBox<>(Priority.values());
        if (taskToEdit != null) priorityCombo.setSelectedItem(taskToEdit.getPriority());
        formPanel.add(priorityCombo, gbc);

        // Due Date
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField dateField = new JTextField(20);
        if (taskToEdit != null && taskToEdit.getDueDate() != null) {
            dateField.setText(taskToEdit.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        formPanel.add(dateField, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String titleText = titleField.getText().trim();
            if (titleText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String description = descArea.getText().trim();
            Priority priority = (Priority) priorityCombo.getSelectedItem();
            LocalDate dueDate = null;

            String dateText = dateField.getText().trim();
            if (!dateText.isEmpty()) {
                try {
                    dueDate = LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid date format! Use YYYY-MM-DD", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (taskToEdit == null) {
                // Add new task
                Task newTask = new Task(nextId++, titleText, description, priority, dueDate);
                tasks.add(newTask);
            } else {
                // Edit existing task
                taskToEdit.setTitle(titleText);
                taskToEdit.setDescription(description);
                taskToEdit.setPriority(priority);
                taskToEdit.setDueDate(dueDate);
            }

            StorageUtils.saveTasks(tasks);
            updateTable();
            updateDashboard();
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    /**
     * Delete selected task
     */
    private void deleteSelectedTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Task task = tableModel.getTask(selectedRow);
        if (task != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete task: " + task.getTitle() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                tasks.remove(task);
                StorageUtils.saveTasks(tasks);
                updateTable();
                updateDashboard();
            }
        }
    }

    /**
     * Mark selected task as complete
     */
    private void markTaskComplete() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to complete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Task task = tableModel.getTask(selectedRow);
        if (task != null) {
            if (task.getStatus() == Status.COMPLETED) {
                task.setStatus(Status.PENDING);
            } else {
                task.setStatus(Status.COMPLETED);
            }
            StorageUtils.saveTasks(tasks);
            updateTable();
            updateDashboard();
        }
    }

    /**
     * Apply filter and sort to the table
     */
    private void applyFilterAndSort() {
        List<Task> filteredTasks = new LinkedList<>(tasks);

        // Apply filter
        String filter = (String) filterComboBox.getSelectedItem();
        if (filter != null) {
            switch (filter) {
                case "Only Pending":
                    filteredTasks = filteredTasks.stream()
                        .filter(t -> t.getStatus() == Status.PENDING)
                        .collect(Collectors.toList());
                    break;
                case "Only Completed":
                    filteredTasks = filteredTasks.stream()
                        .filter(t -> t.getStatus() == Status.COMPLETED)
                        .collect(Collectors.toList());
                    break;
                case "Only Overdue":
                    filteredTasks = filteredTasks.stream()
                        .filter(Task::isOverdue)
                        .collect(Collectors.toList());
                    break;
                case "Due Today":
                    filteredTasks = filteredTasks.stream()
                        .filter(Task::isDueToday)
                        .collect(Collectors.toList());
                    break;
            }
        }

        // Apply sort
        String sort = (String) sortComboBox.getSelectedItem();
        if (sort != null) {
            switch (sort) {
                case "By Priority":
                    filteredTasks.sort(Comparator.comparing(Task::getPriority, 
                        Comparator.comparing(Priority::ordinal).reversed()));
                    break;
                case "By Due Date":
                    filteredTasks.sort(Comparator.comparing(Task::getDueDate, 
                        Comparator.nullsLast(Comparator.naturalOrder())));
                    break;
                case "By Title":
                    filteredTasks.sort(Comparator.comparing(Task::getTitle));
                    break;
            }
        }

        tableModel.setTasks(filteredTasks);
    }

    /**
     * Update the table with current tasks
     */
    private void updateTable() {
        applyFilterAndSort();
    }

    /**
     * Update dashboard statistics
     */
    private void updateDashboard() {
        int total = tasks.size();
        int pending = (int) tasks.stream().filter(t -> t.getStatus() == Status.PENDING).count();
        int completed = (int) tasks.stream().filter(t -> t.getStatus() == Status.COMPLETED).count();
        int overdue = (int) tasks.stream().filter(Task::isOverdue).count();

        totalTasksLabel.setText("Total: " + total);
        pendingTasksLabel.setText("Pending: " + pending);
        completedTasksLabel.setText("Completed: " + completed);
        overdueTasksLabel.setText("Overdue: " + overdue);
    }

    /**
     * Import tasks from JSON file
     * Validates JSON before parsing and shows clear error messages
     */
    private void importTasks() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON Files", "json"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, 
                    "The selected file does not exist.", 
                    "Import Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                // Load tasks directly from selected file
                LinkedList<Task> importedTasks = StorageUtils.loadTasks(file.getAbsolutePath());
                
                if (importedTasks == null) {
                    importedTasks = new LinkedList<>();
                }
                
                // Replace current tasks with imported tasks
                tasks = importedTasks;
                
                // Update nextId
                nextId = 1;
                for (Task task : tasks) {
                    if (task.getId() >= nextId) {
                        nextId = task.getId() + 1;
                    }
                }
                
                // Save to default location
                StorageUtils.saveTasks(tasks);
                
                // Update UI
                updateTable();
                updateDashboard();
                
                JOptionPane.showMessageDialog(this, 
                    "Tasks imported successfully!\nLoaded " + tasks.size() + " task(s).", 
                    "Import Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IllegalArgumentException e) {
                // Invalid JSON structure
                JOptionPane.showMessageDialog(this, 
                    "Invalid JSON file format.\n\n" +
                    "Expected format: [{...}, {...}] or {\"tasks\": [...]}\n\n" +
                    "Error: " + e.getMessage(), 
                    "Import Error - Invalid JSON", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (RuntimeException e) {
                // Other parsing errors
                String errorMsg = e.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "Unknown error occurred while parsing JSON.";
                }
                JOptionPane.showMessageDialog(this, 
                    "Error importing tasks from JSON file.\n\n" +
                    "The file may be malformed or corrupted.\n\n" +
                    "Error: " + errorMsg, 
                    "Import Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                // General errors (file I/O, etc.)
                JOptionPane.showMessageDialog(this, 
                    "Error reading file: " + e.getMessage() + "\n\n" +
                    "Please ensure the file is accessible and not corrupted.", 
                    "Import Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Export tasks to JSON file
     * Exports as valid JSON array format with UTF-8 encoding
     */
    private void exportTasks() {
        if (tasks == null || tasks.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No tasks to export.", 
                "Export Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON Files", "json"));
        fileChooser.setSelectedFile(new File("tasks_export.json"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String filePath = file.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".json")) {
                    filePath += ".json";
                }
                
                // Check if file exists and is writable (or if directory is writable for new file)
                if (file.exists() && !file.canWrite()) {
                    JOptionPane.showMessageDialog(this, 
                        "Cannot write to the selected file.\nThe file may be read-only or locked.", 
                        "Export Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Export tasks
                StorageUtils.saveTasks(tasks, filePath);
                
                JOptionPane.showMessageDialog(this, 
                    "Tasks exported successfully!\n\n" +
                    "Exported " + tasks.size() + " task(s) to:\n" + filePath, 
                    "Export Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (RuntimeException e) {
                // Storage errors
                String errorMsg = e.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "Unknown error occurred while exporting.";
                }
                JOptionPane.showMessageDialog(this, 
                    "Error exporting tasks to JSON file.\n\n" +
                    "Error: " + errorMsg + "\n\n" +
                    "Please ensure you have write permissions for the selected location.", 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                // General errors
                JOptionPane.showMessageDialog(this, 
                    "Error exporting tasks: " + e.getMessage() + "\n\n" +
                    "Please ensure the file path is valid and you have write permissions.", 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Custom cell renderer for color coding
     */
    private class TaskTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            Task task = tableModel.getTask(row);
            if (task == null) return c;

            // Alternate row colors
            if (!isSelected) {
                if (row % 2 == 0) {
                    c.setBackground(new Color(0xFAFAFA)); // Even rows
                } else {
                    c.setBackground(new Color(0xFFFFFF)); // Odd rows
                }
            }

            // Reset styles
            c.setForeground(Color.BLACK);
            c.setFont(c.getFont().deriveFont(Font.PLAIN));

            // Priority column (column 3)
            if (column == 3) {
                switch (task.getPriority()) {
                    case HIGH:
                        c.setForeground(new Color(0xD32F2F)); // Red
                        break;
                    case MEDIUM:
                        c.setForeground(new Color(0xF57C00)); // Orange
                        break;
                    case LOW:
                        c.setForeground(new Color(0x388E3C)); // Green
                        break;
                }
            }

            // Status column (column 5) and row styling
            if (column == 5) {
                if (task.getStatus() == Status.COMPLETED) {
                    c.setForeground(new Color(0x2E7D32)); // Green
                } else {
                    c.setForeground(new Color(0xC62828)); // Red
                }
            }

            // Completed tasks - italic
            if (task.getStatus() == Status.COMPLETED) {
                c.setFont(c.getFont().deriveFont(Font.ITALIC));
            }

            // Overdue tasks - bold red
            if (task.isOverdue() && task.getStatus() == Status.PENDING) {
                c.setForeground(new Color(0xD32F2F)); // Red
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            }

            // Selection background with accent color
            if (isSelected) {
                c.setBackground(new Color(0x42A5F5)); // Blue accent
                c.setForeground(Color.WHITE);
            }

            // Focus border
            if (hasFocus) {
                ((JComponent) c).setBorder(BorderFactory.createLineBorder(new Color(0x42A5F5), 2));
            } else {
                ((JComponent) c).setBorder(BorderFactory.createEmptyBorder());
            }

            return c;
        }
    }
}

