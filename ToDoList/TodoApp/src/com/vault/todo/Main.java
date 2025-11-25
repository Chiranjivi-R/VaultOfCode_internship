package com.vault.todo;

import com.formdev.flatlaf.FlatLightLaf;
import com.vault.todo.ui.TodoGUI;

import javax.swing.*;

/**
 * Main entry point for the To-Do List application
 */
public class Main {
    public static void main(String[] args) {
        // Set FlatLaf Light theme
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
            ex.printStackTrace();
        }

        // Create and show the GUI
        SwingUtilities.invokeLater(() -> {
            TodoGUI gui = new TodoGUI();
            gui.setVisible(true);
        });
    }
}


