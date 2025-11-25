package com.vault.todo.storage;

import com.vault.todo.model.Priority;
import com.vault.todo.model.Status;
import com.vault.todo.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for saving and loading tasks to/from JSON file
 * Uses manual JSON building without external libraries
 */
public class StorageUtils {
    private static final String TASKS_FILE = "tasks.json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Save tasks to JSON file (default location)
     */
    public static void saveTasks(List<Task> tasks) {
        saveTasks(tasks, TASKS_FILE);
    }

    /**
     * Save tasks to a specific JSON file
     * Exports as a JSON array: [{...}, {...}]
     * Uses UTF-8 encoding with pretty formatting
     */
    public static void saveTasks(List<Task> tasks, String filePath) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("[\n");

            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                json.append("  {\n");
                json.append("    \"id\": ").append(task.getId()).append(",\n");
                json.append("    \"title\": \"").append(escapeJson(task.getTitle() != null ? task.getTitle() : "")).append("\",\n");
                json.append("    \"description\": \"").append(escapeJson(task.getDescription() != null ? task.getDescription() : "")).append("\",\n");
                json.append("    \"priority\": \"").append(task.getPriority() != null ? task.getPriority().toString() : "LOW").append("\",\n");
                json.append("    \"dueDate\": \"").append(task.getDueDate() != null ? task.getDueDate().format(DATE_FORMATTER) : "").append("\",\n");
                json.append("    \"status\": \"").append(task.getStatus() != null ? task.getStatus().toString() : "PENDING").append("\",\n");
                json.append("    \"createdAt\": \"").append(task.getCreatedAt() != null ? task.getCreatedAt().format(DATETIME_FORMATTER) : "").append("\",\n");
                json.append("    \"completedAt\": \"").append(task.getCompletedAt() != null ? task.getCompletedAt().format(DATETIME_FORMATTER) : "").append("\"\n");
                json.append("  }");
                if (i < tasks.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }

            json.append("]\n");

            // Write with UTF-8 encoding
            Files.write(Paths.get(filePath), json.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
            throw new RuntimeException("Failed to save tasks: " + e.getMessage(), e);
        }
    }

    /**
     * Load tasks from JSON file (default location)
     */
    public static LinkedList<Task> loadTasks() {
        return loadTasks(TASKS_FILE);
    }

    /**
     * Load tasks from a specific JSON file
     * Supports both array format [{...}, {...}] and object format {"tasks": [...]}
     * Validates JSON before parsing and handles errors gracefully
     */
    public static LinkedList<Task> loadTasks(String filePath) {
        LinkedList<Task> tasks = new LinkedList<>();
        
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return tasks; // Return empty list if file doesn't exist
            }

            // Read file with UTF-8 encoding
            String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            content = content.trim();

            // Validate JSON structure
            if (!isValidJsonStructure(content)) {
                throw new IllegalArgumentException("Invalid JSON structure. Expected array format [{...}, {...}] or object format {\"tasks\": [...]}");
            }

            // Extract tasks array - support both formats
            String tasksArray = extractTasksArray(content);
            
            if (tasksArray == null || tasksArray.trim().isEmpty()) {
                return tasks; // Empty array
            }

            // Parse tasks from array
            parseTasksFromArray(tasksArray, tasks);
            
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
            throw new RuntimeException("Failed to read file: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Error parsing tasks: " + e.getMessage());
            throw new RuntimeException("Failed to parse JSON: " + e.getMessage(), e);
        }

        return tasks;
    }

    /**
     * Validate JSON structure
     */
    private static boolean isValidJsonStructure(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        
        content = content.trim();
        
        // Check if it's an array format: [{...}, {...}]
        if (content.startsWith("[") && content.endsWith("]")) {
            return true;
        }
        
        // Check if it's an object format: {"tasks": [...]}
        if (content.startsWith("{") && content.endsWith("}") && content.contains("\"tasks\"")) {
            return true;
        }
        
        return false;
    }

    /**
     * Extract tasks array from JSON content
     * Supports both array format and object format
     */
    private static String extractTasksArray(String content) {
        content = content.trim();
        
        // If it's already an array format: [{...}, {...}]
        if (content.startsWith("[") && content.endsWith("]")) {
            return content.substring(1, content.length() - 1).trim();
        }
        
        // If it's an object format: {"tasks": [...]}
        if (content.startsWith("{") && content.contains("\"tasks\"")) {
            int tasksStart = content.indexOf("[");
            int tasksEnd = content.lastIndexOf("]");
            
            if (tasksStart != -1 && tasksEnd != -1 && tasksEnd > tasksStart) {
                return content.substring(tasksStart + 1, tasksEnd).trim();
            }
        }
        
        return null;
    }

    /**
     * Parse tasks from array string
     */
    private static void parseTasksFromArray(String tasksArray, LinkedList<Task> tasks) {
        if (tasksArray.trim().isEmpty()) {
            return;
        }

        // Split by task objects - handle nested braces properly
        java.util.List<String> taskStrings = splitTaskObjects(tasksArray);
        
        for (String taskStr : taskStrings) {
            taskStr = taskStr.trim();
            if (taskStr.isEmpty()) {
                continue;
            }
            
            // Remove outer braces
            if (taskStr.startsWith("{")) {
                taskStr = taskStr.substring(1);
            }
            if (taskStr.endsWith("}")) {
                taskStr = taskStr.substring(0, taskStr.length() - 1);
            }

            Task task = parseTask(taskStr);
            if (task != null) {
                tasks.add(task);
            }
        }
    }

    /**
     * Split task objects from array string, handling nested braces
     */
    private static java.util.List<String> splitTaskObjects(String arrayContent) {
        java.util.List<String> tasks = new LinkedList<>();
        int braceCount = 0;
        int start = 0;
        
        for (int i = 0; i < arrayContent.length(); i++) {
            char c = arrayContent.charAt(i);
            if (c == '{') {
                if (braceCount == 0) {
                    start = i;
                }
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    tasks.add(arrayContent.substring(start, i + 1));
                }
            }
        }
        
        return tasks;
    }

    /**
     * Parse a single task from JSON string
     * Handles missing fields with defaults, converts lowercase priority, handles invalid dates gracefully
     */
    private static Task parseTask(String taskStr) {
        try {
            // Extract values with defaults
            int id = extractIntValue(taskStr, "id");
            if (id <= 0) {
                id = 1; // Default ID
            }
            
            String title = extractStringValue(taskStr, "title");
            if (title == null || title.isEmpty()) {
                title = "Untitled Task"; // Default title
            }
            
            String description = extractStringValue(taskStr, "description");
            if (description == null) {
                description = ""; // Default description
            }
            
            // Priority - convert lowercase to uppercase, default to LOW
            String priorityStr = extractStringValue(taskStr, "priority");
            Priority priority = Priority.LOW; // Default
            if (priorityStr != null && !priorityStr.isEmpty()) {
                try {
                    // Convert to uppercase to handle lowercase input
                    priorityStr = priorityStr.toUpperCase().trim();
                    priority = Priority.valueOf(priorityStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid priority '" + priorityStr + "', using LOW as default");
                    priority = Priority.LOW;
                }
            }
            
            // Status - default to PENDING
            String statusStr = extractStringValue(taskStr, "status");
            Status status = Status.PENDING; // Default
            if (statusStr != null && !statusStr.isEmpty()) {
                try {
                    statusStr = statusStr.toUpperCase().trim();
                    status = Status.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid status '" + statusStr + "', using PENDING as default");
                    status = Status.PENDING;
                }
            }
            
            // Due Date - handle invalid dates gracefully
            String dueDateStr = extractStringValue(taskStr, "dueDate");
            LocalDate dueDate = null;
            if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
                try {
                    dueDate = LocalDate.parse(dueDateStr.trim(), DATE_FORMATTER);
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid dueDate '" + dueDateStr + "', skipping date for task: " + title);
                    dueDate = null; // Skip invalid date
                }
            }
            
            // Created At - default to now if missing or invalid
            String createdAtStr = extractStringValue(taskStr, "createdAt");
            LocalDateTime createdAt = LocalDateTime.now(); // Default
            if (createdAtStr != null && !createdAtStr.trim().isEmpty()) {
                try {
                    createdAt = LocalDateTime.parse(createdAtStr.trim(), DATETIME_FORMATTER);
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid createdAt '" + createdAtStr + "', using current time for task: " + title);
                    createdAt = LocalDateTime.now();
                }
            }
            
            // Completed At - null if missing or invalid
            String completedAtStr = extractStringValue(taskStr, "completedAt");
            LocalDateTime completedAt = null;
            if (completedAtStr != null && !completedAtStr.trim().isEmpty()) {
                try {
                    completedAt = LocalDateTime.parse(completedAtStr.trim(), DATETIME_FORMATTER);
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid completedAt '" + completedAtStr + "', setting to null for task: " + title);
                    completedAt = null;
                }
            }

            return new Task(id, title, description, priority, dueDate, status, createdAt, completedAt);
        } catch (Exception e) {
            System.err.println("Error parsing task: " + e.getMessage());
            // Return null to skip this task instead of crashing
            return null;
        }
    }

    /**
     * Extract integer value from JSON string
     */
    private static int extractIntValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(-?\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    /**
     * Extract string value from JSON string
     */
    private static String extractStringValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return unescapeJson(m.group(1));
        }
        return "";
    }

    /**
     * Escape special characters for JSON
     */
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * Unescape JSON special characters
     */
    private static String unescapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\\"", "\"")
                  .replace("\\\\", "\\")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t");
    }
}

