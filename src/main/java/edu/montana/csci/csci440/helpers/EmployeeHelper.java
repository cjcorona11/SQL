package edu.montana.csci.csci440.helpers;

import edu.montana.csci.csci440.model.Employee;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EmployeeHelper {

    public static String makeEmployeeTree() {
        // TODO, change this to use a single query operation to get all employees
        Employee employee = Employee.find(1); // root employee
        // and use this data structure to maintain reference information needed to build the tree structure
        Map<Long, List<Employee>> employeeMap = new HashMap<>();

        List<Employee> allEmployees = Employee.all();
        for (Employee currentEmployee : allEmployees) {
            Long reportsTo = currentEmployee.getReportsTo();
            List<Employee> employees = employeeMap.get(reportsTo);
            if(currentEmployee.getEmployeeId() != employee.getEmployeeId()) {
                if (employees == null) {
                    List<Employee> firstEmployee = new LinkedList<>();
                    firstEmployee.add(currentEmployee);
                    employeeMap.put(reportsTo, firstEmployee);
                } else {
                    employees.add(currentEmployee);
                    employeeMap.put(reportsTo, employees);
                }
            }
        }
        return "<ul>" + makeTree(employee, employeeMap) + "</ul>";
    }

    // TODO - currently this method just uses the employee.getReports() function, which
    //  issues a query.  Change that to use the employeeMap variable instead
    public static String makeTree(Employee employee, Map<Long, List<Employee>> employeeMap) {
        String list = "<li><a href='/employees" + employee.getEmployeeId() + "'>"
                + employee.getEmail() + "</a><ul>";
        //List<Employee> reports = employee.getReports();
        List<Employee> reports = employeeMap.get(employee.getEmployeeId());
        if(reports != null) {
            for (Employee report : reports) {
                list += makeTree(report, employeeMap);
            }
        }
        return list + "</ul></li>";
    }
}
