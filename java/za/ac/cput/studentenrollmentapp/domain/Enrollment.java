package za.ac.cput.studentenrollmentapp.domain;

import java.io.Serializable;

public class Enrollment implements Serializable {
    private int enrollmentId;
    private String studentNumber;
    private String courseCode;
    
    // Default constructor
    public Enrollment() {
    }
    
    
    public Enrollment(String studentNumber, String courseCode) {
        this.studentNumber = studentNumber;
        this.courseCode = courseCode;
    }
    
    // Full constructor
    public Enrollment(int enrollmentId, String studentNumber, String courseCode) {
        this.enrollmentId = enrollmentId;
        this.studentNumber = studentNumber;
        this.courseCode = courseCode;
    }
    
    // Getters and setters
    public int getEnrollmentId() {
        return enrollmentId;
    }
    
    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }
    
    public String getStudentNumber() {
        return studentNumber;
    }
    
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
    
    public String getCourseCode() {
        return courseCode;
    }
    
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
    
    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentId +
                ", studentNumber='" + studentNumber + '\'' +
                ", courseCode='" + courseCode + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Enrollment that = (Enrollment) o;
        
        if (enrollmentId != that.enrollmentId) return false;
        if (!studentNumber.equals(that.studentNumber)) return false;
        return courseCode.equals(that.courseCode);
    }
    
    @Override
    public int hashCode() {
        int result = enrollmentId;
        result = 31 * result + studentNumber.hashCode();
        result = 31 * result + courseCode.hashCode();
        return result;
    }
}
