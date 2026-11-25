package service;

import model.ApplicantModel;
import dao.*;
import java.sql.*;
import java.util.ArrayList;

public class ApplicantService {
	
	private ApplicantDAO applicantDAO;
	
	public ApplicantService(ApplicantDAO applicantDAO) {
		this.applicantDAO = applicantDAO;
	}
	
	//returns the applicant with this student number, or null if none exists
	public ApplicantModel findByStudno(String studno) throws SQLException {
		if(studno == null || studno.trim().isEmpty()) {
			return null;
		}
		
		return this.applicantDAO.retrieveApplicant(studno.trim());
	}
	
	public ArrayList<ApplicantModel> getAllApplicants() throws SQLException {
		return this.applicantDAO.retrieveAllApplicants();
	}
	
	//search fields required by the spec: student number, name, email, mobile, college, course
	public ArrayList<ApplicantModel> getApplicantsBy(int mode, String identifier) throws SQLException {
		
		if(identifier == null || identifier.trim().isEmpty()) {
			throw new IllegalArgumentException("Search value cannot be empty");
		}
		
		identifier = identifier.trim();
		String query;
		
		switch(mode) {
		
		case 1: //by studno
			query = ApplicantDAO.BASE_SELECT + """
					WHERE ap.studno = ?
					ORDER BY ap.studno
					""";
			break;
			
		case 2: //by name (partial match)
			query = ApplicantDAO.BASE_SELECT + """
					WHERE ap.fullname LIKE ?
					ORDER BY ap.fullname, ap.studno
					""";
			identifier = "%" + identifier + "%";
			break;
			
		case 3: //by email (partial match)
			query = ApplicantDAO.BASE_SELECT + """
					WHERE ap.email LIKE ?
					ORDER BY ap.studno
					""";
			identifier = "%" + identifier + "%";
			break;
			
		case 4: //by mobile (partial match)
			query = ApplicantDAO.BASE_SELECT + """
					WHERE ap.mobile LIKE ?
					ORDER BY ap.studno
					""";
			identifier = "%" + identifier + "%";
			break;
			
		case 5: //by college: exact college code or partial college name
			query = ApplicantDAO.BASE_SELECT + """
					WHERE c.collcode = ? OR c.collname LIKE ?
					ORDER BY ap.studno
					""";
			return this.applicantDAO.getApplicantsBy(query, identifier, "%" + identifier + "%");
			
		case 6: //by course (partial match)
			query = ApplicantDAO.BASE_SELECT + """
					WHERE ap.course LIKE ?
					ORDER BY ap.studno
					""";
			identifier = "%" + identifier + "%";
			break;
			
		default:
			throw new IllegalArgumentException("Invalid Search mode!");
		}
		
		return this.applicantDAO.getApplicantsBy(query, identifier);
	}
}
