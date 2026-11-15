package service;
import java.util.ArrayList;
import java.sql.*;
import dao.*;
import model.*;

public class ApplicationService {
	
	private ApplicationDAO applicationDAO;
	
	public ApplicationService(ApplicationDAO applicationDAO) {
		this.applicationDAO = applicationDAO;
	}
	
	public List<ApplicationModel> getAllApplications() {
		
	}
	
	public void createApplication(ApplicantModel applicant, OfferingModel offering) throws SQLException{
		//search for the the database if this applicant already applied for this offering
		String studno = applicant.getStudno();
		String offerno = offering.getOfferno();
		
		//check if a student has a current application to the offering
		if(this.applicationDAO.existsByStudnoAndOfferno(studno, offerno)) {
			throw new IllegalArgumentException(
				 	"Student has already applied to this offering");
		}
		
		//check if a student has applied to the program before but is applying for first award
		
		//check if a student has never applied to the program before but is applying for renewal
		
		applicationDAO.insertApplication();
	}
}
