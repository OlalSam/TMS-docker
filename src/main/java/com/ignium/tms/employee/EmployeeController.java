package com.ignium.tms.employee;

import com.ignium.tms.MessageUtility;
import com.ignium.tms.MessageUtility;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.Flash;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

/**
 * Controller for managing employees.
 */
@Named("employeeController")
@ViewScoped
public class EmployeeController extends LazyDataModel<Employee> implements Serializable {

    @Inject
    private MessageUtility message;

    @Inject
    private EmployeeDao employeeDao;

    @Inject
    private Flash flash;

    @Inject
    private SecurityContext securityContext;

    private Employee newEmployee;
    private Employee selectedEmployee;

    @PostConstruct
    public void init() {
        newEmployee = new Employee();
        if (selectedEmployee == null) {
            selectedEmployee = (Employee) flash.get("selectedEmployee");
        }
    }

    @Override
    public int count(Map<String, FilterMeta> filters) {
        return 0; // Implement this method in EmployeeDao
    }

    @Override
    public List<Employee> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filters) {
        var employeeList = employeeDao.getAllEmployee(offset, pageSize);
        int count = employeeList.isEmpty() ? 0 : employeeList.size();
        setRowCount(count); // Update total record count
        return employeeList;
    }

    public Employee getNewEmployee() {
        return newEmployee;
    }

    public void setNewEmployee(Employee newEmployee) {
        this.newEmployee = newEmployee;
    }

    public Employee getSelectedEmployee() {
        return selectedEmployee;
    }

    public void setSelectedEmployee(Employee selectedEmployee) {
        this.selectedEmployee = selectedEmployee;
    }

    public String addEmployee() {
        boolean result = employeeDao.saveEmployee(newEmployee);
        if (result) {
            message.addSuccessMessage("Employee added successfully");
            flash.setKeepMessages(true);
            return "employee.xhtml?faces-redirect=true";
        }
        message.addErrorMessage("Error adding employee. Try again.");
        return "employeeForm.xhtml";
    }

    public String editEmployee(int employeeId) {
        selectedEmployee = employeeDao.getEmployeeById(employeeId);
        System.out.println("EmpployeeId" + employeeId);
        System.out.println("Selected employee " + selectedEmployee.toString());
        if (selectedEmployee != null) {
            flash.put("selectedEmployee", selectedEmployee);
            return "employeeUpdateForm.xhtml?faces-redirect=true";
        } else {
            message.addErrorMessage("Employee not found.");
            return "employee.xhtml?faces-redirect=true";
        }
    }

    public String newEmployee() {
        return "employeeForm?faces-redirect=true";
    }

    public String updateEmployee() {
        boolean result = employeeDao.updateEmployee(selectedEmployee);
        if (result) {
            message.addSuccessMessage("Employee updated successfully");
            flash.setKeepMessages(true);
            return "employee.xhtml?faces-redirect=true";
        }
        message.addErrorMessage("Error updating employee. Try again.");
        return "employeeForm.xhtml";
    }

    public void deleteEmployee(int employeeId) {
        employeeDao.deleteEmployee(employeeId);
        message.addSuccessMessage("Employee marked as inactive.");
        flash.setKeepMessages(true);
    }

}
