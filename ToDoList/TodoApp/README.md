# Ultra-Professional To-Do List Application

A modern, feature-rich To-Do List GUI application built with Java Swing and FlatLaf.

## Features

- ✅ **Modern UI**: Beautiful FlatLaf Light theme
- ✅ **Task Management**: Add, edit, delete, and complete tasks
- ✅ **Priority System**: LOW, MEDIUM, HIGH priority levels with color coding
- ✅ **Due Dates**: Track task deadlines
- ✅ **Status Tracking**: PENDING and COMPLETED statuses
- ✅ **Filtering**: Filter by All, Pending, Completed, Overdue, or Due Today
- ✅ **Sorting**: Sort by Priority, Due Date, or Title
- ✅ **Dashboard**: Real-time statistics (Total, Pending, Completed, Overdue)
- ✅ **JSON Storage**: Save and load tasks from JSON files
- ✅ **Import/Export**: Import from or export to JSON files
- ✅ **Color Coding**: Visual indicators for priority and status
  - HIGH priority → Red
  - MEDIUM priority → Orange
  - LOW priority → Black
  - COMPLETED tasks → Green with italic
  - Overdue tasks → Bold red

## Project Structure

```
TodoApp/
├── src/
│   └── com/
│       └── vault/
│           └── todo/
│               ├── Main.java                    # Entry point
│               ├── model/
│               │   ├── Task.java                # Task model class
│               │   ├── Priority.java            # Priority enum
│               │   └── Status.java              # Status enum
│               ├── storage/
│               │   └── StorageUtils.java        # JSON save/load utilities
│               └── ui/
│                   ├── TodoGUI.java             # Main GUI class
│                   └── TaskTableModel.java      # Table model for JTable
├── libs/
│   └── flatlaf-3.4.jar                         # FlatLaf library
├── tasks.json                                   # Task storage file
└── README.md                                    # This file
```

## Prerequisites

- Java JDK 8 or higher
- VS Code with Java Extension Pack
- FlatLaf JAR file (already included in `libs/flatlaf-3.4.jar`)

## Setup Instructions

### 1. Verify FlatLaf JAR
Ensure `libs/flatlaf-3.4.jar` exists in your project.

### 2. Configure VS Code
The workspace settings are already configured in `.vscode/settings.json`:
```json
{
    "java.project.referencedLibraries": [
        "libs/**/*.jar"
    ]
}
```

### 3. Compile the Project

**Option A: Using VS Code**
1. Open the project in VS Code
2. Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac)
3. Type "Java: Compile Workspace"
4. Select it to compile

**Option B: Using Command Line**
```bash
# Navigate to project root
cd TodoApp

# Compile all Java files
javac -cp "libs/flatlaf-3.4.jar" -d bin src/com/vault/todo/**/*.java
```

### 4. Run the Application

**Option A: Using VS Code**
1. Open `src/com/vault/todo/Main.java`
2. Click the "Run" button above the `main` method
3. Or press `F5` to debug/run

**Option B: Using Command Line**
```bash
# Run from project root
java -cp "bin;libs/flatlaf-3.4.jar" com.vault.todo.Main

# On Linux/Mac, use colon instead of semicolon:
java -cp "bin:libs/flatlaf-3.4.jar" com.vault.todo.Main
```

## Usage Guide

### Adding a Task
1. Click **"Add Task"** button
2. Fill in the form:
   - **Title** (required)
   - **Description** (optional)
   - **Priority** (LOW, MEDIUM, HIGH)
   - **Due Date** (format: YYYY-MM-DD, optional)
3. Click **"Save"**

### Editing a Task
1. Select a task in the table
2. Click **"Edit Task"** button
3. Modify the fields
4. Click **"Save"**

### Completing a Task
1. Select a task in the table
2. Click **"Mark Complete"** button
3. The task status will toggle between PENDING and COMPLETED

### Deleting a Task
1. Select a task in the table
2. Click **"Delete Task"** button
3. Confirm the deletion

### Filtering Tasks
Use the **Filter** dropdown to show:
- All Tasks
- Only Pending
- Only Completed
- Only Overdue
- Due Today

### Sorting Tasks
Use the **Sort** dropdown to sort by:
- By Priority (HIGH → LOW)
- By Due Date (earliest first)
- By Title (alphabetical)

### Importing Tasks
1. Click **"Import JSON"** button
2. Select a JSON file
3. Tasks will be loaded and replace current tasks

### Exporting Tasks
1. Click **"Export JSON"** button
2. Choose a location and filename
3. Tasks will be saved to the selected file

## JSON File Format

Tasks are stored in `tasks.json` with the following structure:

```json
{
  "tasks": [
    {
      "id": 1,
      "title": "Task Title",
      "description": "Task Description",
      "priority": "HIGH",
      "dueDate": "2024-12-31",
      "status": "PENDING",
      "createdAt": "2024-12-01T10:00:00",
      "completedAt": ""
    }
  ]
}
```

## Screenshots

To take screenshots for documentation:

1. **Dashboard View**: Show the main window with dashboard statistics
2. **Add Task Dialog**: Capture the task creation dialog
3. **Filtered View**: Show tasks filtered by status
4. **Color Coding**: Display tasks with different priorities and statuses
5. **Export Dialog**: Show the file chooser dialog

## Technical Details

- **Framework**: Java Swing
- **Look and Feel**: FlatLaf Light Theme
- **Data Structure**: LinkedList internally, ArrayList for table display
- **Storage**: Manual JSON parsing (no external JSON libraries)
- **Architecture**: Clean OOP with proper package structure

## Troubleshooting

### Application won't start
- Ensure FlatLaf JAR is in `libs/flatlaf-3.4.jar`
- Check that VS Code settings.json includes the library path
- Verify Java version is 8 or higher

### Tasks not saving
- Check file permissions in the project directory
- Ensure `tasks.json` is writable

### Import/Export not working
- Verify JSON file format matches the expected structure
- Check file permissions

## License

This project is provided as-is for educational and personal use.
