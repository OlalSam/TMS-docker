/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ignium.tms.taskmanagement;

import com.ignium.tms.MailUtility;
import com.ignium.tms.MessageUtility;
import com.ignium.tms.employee.Employee;
import com.ignium.tms.employee.EmployeeDao;
import com.ignium.tms.fleet.Vehicle;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.Flash;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.mail.MessagingException;
import jakarta.security.enterprise.SecurityContext;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author olal
 */
@Named("taskController")
@ViewScoped
public class TaskController implements Serializable {

    @Inject
    private MessageUtility message;

    @Inject
    private TaskDaoApi taskDao;

    @Inject
    private EmployeeDao userService;

    @Inject
    private MailUtility mailService;

    @Inject
    private Flash flash;

    @Inject
    private SecurityContext context;

    private List<Task> taskList;

    private List<Task> userTasks;

    private List<Employee> driversAvailable;
    private List<Vehicle> vehicles;

    private Task newTask;

    private Task selectedTask;

    @PostConstruct
    public void innit() {
        newTask = new Task();
        taskList = taskDao.findAll();
        driversAvailable = taskDao.drivers();
        vehicles = taskDao.vehicles();

        if (selectedTask == null) {
            selectedTask = (Task) flash.get("selectedTask");
        }
    }

    public String addTask() {
        boolean result = taskDao.saveTask(newTask);
        if (!result) {
            message.addErrorMessage("Error creating task. Please try again.");
            return "taskForm.xhtml";
        }

        // Success: send notification
        message.addSuccessMessage("Task created successfully");
        flash.setKeepMessages(true);

        var user = userService.getEmployeeById(newTask.getUserId());
        String to = user.getEmail();
        String subject = "New Task Assigned: " + newTask.getTitle();

        StringBuilder body = new StringBuilder();
        body.append("Hello ").append(user.getFirstName()).append(",\n\n")
                .append("A new task has just been created and assigned to you. Here are the details:\n\n")
                .append("  • Title: ").append(newTask.getTitle()).append("\n")
                .append("  • Notes: ").append(newTask.getNotes()).append("\n")
                .append("  • Pickup Location: ").append(newTask.getPickupDesination()).append("\n")
                .append("  • Destination: ").append(newTask.getDestinationLocation()).append("\n")
                .append("  • Status: ").append(newTask.getStatus()).append("\n\n")
                .append("Please log in to the Transport Management System to view and start this task.\n\n")
                .append("Regards,\n")
                .append("The TMS Team");

        try {
            mailService.send(to, subject, body.toString());
        } catch (MessagingException ex) {
            Logger.getLogger(TaskController.class.getName())
                    .log(Level.SEVERE, "Failed to send task email", ex);
        }

        return "taskManagement.xhtml?faces-redirect=true";
    }

    public void completeTask(Long taskId) {
        // Load and mark completed
        Task task = taskDao.findById(taskId);
        task.setStatus("COMPLETED");
        boolean updated = taskDao.updateTask(task);

        if (updated) {
            // 1) UI notification
            message.addSuccessMessage("Task \"" + task.getTitle() + "\" marked as completed.");
            flash.setKeepMessages(true);

            // 2) Notify admin by email
            Employee admin = userService.findAdmin();         // your existing helper
            String toAdmin = admin.getEmail();
            String subject = "Task Completed: " + task.getTitle();

            StringBuilder body = new StringBuilder()
                    .append("Hello ").append(admin.getFirstName()).append(",\n\n")
                    .append("The following task has just been completed:\n\n")
                    .append("  • Title: ").append(task.getTitle()).append("\n")
                    .append("  • Assigned To: ")
                    .append(userService.getEmployeeById(task.getUserId()).getUsername()).append("\n")
                    .append("  • Notes: ").append(task.getNotes()).append("\n\n")
                    .append("Please review the completed task in the TMS dashboard.\n\n")
                    .append("Regards,\n")
                    .append("The TMS Team");

            try {
                mailService.send(toAdmin, subject, body.toString());
            } catch (MessagingException ex) {
                Logger.getLogger(TaskController.class.getName())
                        .log(Level.SEVERE, "Failed to send completion email to admin", ex);
                // optionally add an error message to the UI:
                message.addErrorMessage("Task completed, but notification email failed.");
            }
        } else {
            // In case the update itself failed
            message.addErrorMessage("Unable to complete task. Please try again.");
        }
    }

    public String editTask(Long taskId) {
        selectedTask = taskDao.findById(taskId);
        if (selectedTask != null) {
            flash.put("selectedTask", selectedTask);
            return "taskUpdateForm.xhtml?faces-redirect=true";
        }
        message.addErrorMessage("Task not found.");
        return "taskManagement.xhtml?faces-redirect=true";
    }

// Update
    public String updateTask() {
        if (selectedTask != null) {
            boolean result = taskDao.updateTask(selectedTask);
            if (result) {
                message.addSuccessMessage("Task updated successfully");
                flash.setKeepMessages(true);
                return "taskManagement.xhtml?faces-redirect=true";
            }
            return "taskUpdateForm.xhtml";
        }
        message.addErrorMessage("Error updating task. Try again.");
        return "taskUpdateForm.xhtml";
    }

// Delete
    public void deleteTask(Long taskId) {
        taskDao.delete(taskId);
        message.addSuccessMessage("Task deleted.");
        flash.setKeepMessages(true);
    }

    //EMPLOYEE methods
    public List<Task> getUserTasks() {
        String username = context.getCallerPrincipal().getName();
        userTasks = taskDao.userTasks(username);
        return userTasks;
    }

    public void startTask(Long taskId) {
        selectedTask = taskDao.findById(taskId);
        selectedTask.setStatus("IN_PROGRESS");
        boolean result = taskDao.updateTask(selectedTask);
        if (result) {
            message.addSuccessMessage("Task Started");

        }
    }

    public void setUserTasks(List<Task> userTasks) {
        this.userTasks = userTasks;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public Task getNewTask() {
        return newTask;
    }

    public void setNewTask(Task newTask) {
        this.newTask = newTask;
    }

    public Task getSelectedTask() {
        return selectedTask;
    }

    public void setSelectedTask(Task selectedTask) {
        this.selectedTask = selectedTask;
    }

    public List<Employee> getDriversAvailable() {
        return driversAvailable;
    }

    public void setDriversAvailable(List<Employee> driversAvailable) {
        this.driversAvailable = driversAvailable;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

}
