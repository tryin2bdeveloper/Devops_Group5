package com.napier.devops;

import java.sql.*;
import java.util.ArrayList;

public class App
{
    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect(String location, int delay) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(delay);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://" + location
                                + "/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " +                                  Integer.toString(i));
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Gets all the current employees and salaries.
     * @return A list of all employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getAllSalaries()
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, salaries "
                            + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                            + "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    /**
     * Prints a list of employees.
     * @param employees The list of employees to print.
     */
    public void printSalaries(ArrayList<Employee> employees)
    {
        // Check employees is not null
        if (employees == null)
        {
            System.out.println("No employees");
            return;
        }
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees)
        {
            if (emp == null)
                continue;
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }
    public Department getDepartment(String dept_name) {
        try {
            // Create a statement for SQL query execution
            Statement stmt = con.createStatement();

            // Query to retrieve the department by its name
            String strSelect = "SELECT d.dept_no, d.dept_name, dm.emp_no "
                    + "FROM departments d "
                    + "LEFT JOIN dept_manager dm ON d.dept_no = dm.dept_no "
                    + "WHERE d.dept_name = '" + dept_name + "'";

            // Execute query
            ResultSet rset = stmt.executeQuery(strSelect);

            // If department is found
            if (rset.next()) {
                Department dept = new Department();
                dept.dept_no = rset.getString("dept_no");
                dept.dept_name = rset.getString("dept_name");

                // Add the manager's employee number if it exists
                int manager_emp_no = rset.getInt("emp_no");
                if (!rset.wasNull()) {
                    dept.manager_emp_no = manager_emp_no;  // Assuming Department has a manager_emp_no field
                }

                return dept;
            } else {
                System.out.println("Department not found.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error retrieving department: " + e.getMessage());
            return null;
        }
    }
    public ArrayList<Employee> getSalariesByDepartment(Department dept) {
        try {
            // Create a statement for SQL query execution
            Statement stmt = con.createStatement();

            // SQL query to get employee salaries for the specified department
            String strSelect = "SELECT e.emp_no, e.first_name, e.last_name, s.salary "
                    + "FROM employees e "
                    + "JOIN salaries s ON e.emp_no = s.emp_no "
                    + "JOIN dept_emp de ON e.emp_no = de.emp_no "
                    + "WHERE de.dept_no = '" + dept.dept_no + "' "
                    + "AND s.to_date = '9999-01-01' "
                    + "ORDER BY e.emp_no ASC";

            // Execute the SQL query
            ResultSet rset = stmt.executeQuery(strSelect);

            // Create an array list to store the employees
            ArrayList<Employee> employees = new ArrayList<>();

            // Extract employee details from result set
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.salary = rset.getInt("salary");
                employees.add(emp);
            }

            return employees;
        } catch (Exception e) {
            System.out.println("Error retrieving salaries: " + e.getMessage());
            return null;
        }
    }
    public static void main(String[] args) {
        // Create new Application and connect to database
        App a = new App();

        if(args.length < 1){
            a.connect("localhost:33060", 30000);
        }else{
            a.connect(args[0], Integer.parseInt(args[1]));
        }

        Department dept = a.getDepartment("Development");
        ArrayList<Employee> employees = a.getSalariesByDepartment(dept);


        // Print salary report
        a.printSalaries(employees);

        // Disconnect from database
        a.disconnect();
    }
}