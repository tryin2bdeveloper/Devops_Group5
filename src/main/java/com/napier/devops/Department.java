package com.napier.devops;

public class Department {
    public String dept_no;
    public String dept_name;
    public int manager_emp_no; // Optional if you are storing the manager's employee number

    // You can also add a constructor, getters, setters, and other methods as needed
    public Department() {
    }

    public Department(String dept_no, String dept_name) {
        this.dept_no = dept_no;
        this.dept_name = dept_name;
    }

    // You can add more methods or override toString for better output
}
