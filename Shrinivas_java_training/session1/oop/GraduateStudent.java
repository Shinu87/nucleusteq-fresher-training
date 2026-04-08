package Shrinivas_java_training.session1.oop;

public class GraduateStudent extends Student {

    private int graduateYear;
    private float graduateCgpa;

    // constructor with all fields
    public GraduateStudent(String name, int rollNumber, String department, String collegeName, String email,
            int graduateYear, float graduateCgpa) {
        super(name, rollNumber, department, collegeName, email); // call parent constructor
        this.graduateYear = graduateYear;
        this.graduateCgpa = graduateCgpa;
    }

    public int getGraduateYear() {
        return graduateYear;
    }

    public float getGraduateCgpa() {
        return graduateCgpa;
    }

    // override method from Student
    public void displayDetails() {
        super.displayDetails(); // call parent method
        System.out.println("Graduation Year: " + graduateYear);
        System.out.println("CGPA: " + graduateCgpa);
    }

    public static void main(String[] args) {

        // creating object of GraduateStudent
        GraduateStudent gs = new GraduateStudent(
                "Shrinivas",
                101,
                "CSE",
                "NIT AP",
                "shrinivas@email.com",
                2026,
                8.5f);

        // calling method
        gs.displayDetails();
    }
}