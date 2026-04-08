package Shrinivas_java_training.session1.oop;

public class Student {

    private String name;
    private int rollNumber;
    private String department;
    private String collegeName;
    private String email;

    // constructor to initialize values
    public Student(String name, int rollNumber, String department, String collegeName, String email) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.department = department;
        this.collegeName = collegeName;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public String getDepartment() {
        return department;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public String getEmail() {
        return email;
    }

    public void displayDetails() {
        System.out.println("Name: " + name);
        System.out.println("Roll No: " + rollNumber);
        System.out.println("Department: " + department);
        System.out.println("College: " + collegeName);
    }
}