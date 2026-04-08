// Polymorphism means same method name but different behavior
// Here displayDetails() is defined in Student and overridden in GraduateStudent
// We are using parent reference (Student) but object of GraduateStudent
// At runtime Java calls the child class method (GraduateStudent)
// This is called runtime polymorphism (method overriding)

package Shrinivas_java_training.session1.oop;

public class PolymorphismDemo {

    public static void main(String[] args) {

        // parent reference, child object
        Student s = new GraduateStudent(
                "Shrinivas",
                101,
                "CSE",
                "NIT AP",
                "shrinivas@email.com",
                2026,
                8.5f);

        // method call - runtime polymorphism
        s.displayDetails();
    }
}