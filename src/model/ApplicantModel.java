package model;
import java.time.LocalDate;

public class ApplicantModel {
	
	private final String studno;
	private String fullname;
	private String email;
	private String mobile;
	private LocalDate regdate;
	private String collcode;
	private String course;
	private int revno;
	
	// Constructor for creation
    public ApplicantModel(
            String studno,
            String fullname,
            String email,
            LocalDate regdate,
            String collcode,
            String course) {

        this.studno = studno;
        this.fullname = fullname;
        this.email = email;
        this.regdate = regdate;
        this.collcode = collcode;
        this.course = course;
        this.revno = 1;
    }
    
    //Constructor for retrieval
    public ApplicantModel(
    		String studno,
            String fullname,
            String email,
            String mobile,
            LocalDate regdate,
            String collcode,
            String course,
            int revno) {
    	
    	this.studno = studno;
        this.fullname = fullname;
        this.email = email;
        this.mobile = mobile;
        this.regdate = regdate;
        this.collcode = collcode;
        this.course = course;
        this.revno = revno;
    }
    
    
 // Getters
    public String getStudno() {
        return studno;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public LocalDate getRegdate() {
        return regdate;
    }

    public String getCollcode() {
        return collcode;
    }

    public String getCourse() {
        return course;
    }

    public int getRevno() {
        return revno;
    }

    // Setters
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setRegdate(LocalDate regdate) {
        this.regdate = regdate;
    }

    public void setCollcode(String collcode) {
        this.collcode = collcode;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setRevno(int revno) {
        this.revno = revno;
    }
}