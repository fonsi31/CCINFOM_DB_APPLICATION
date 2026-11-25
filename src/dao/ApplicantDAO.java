package dao;

import util.*;
import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ApplicantDAO {
	
	//common SELECT for applicant searches
	//ap = applicant, c = college (joined so searches can use the college name)
	//only the applicant's columns (ap.*) are returned, so column names are never ambiguous
	public static final String BASE_SELECT = """
			SELECT ap.*
			FROM applicant ap
			JOIN college c ON c.collcode = ap.collcode
			""";
	
	private DBConnection dbConn;
	
	public ApplicantDAO(DBConnection dbConn) {
		this.dbConn = dbConn;
	}
	
	//for retrieval: builds an ApplicantModel from the current row of a BASE_SELECT result
	public ApplicantModel instantiateApplicant(ResultSet rs) throws SQLException {
		String studno = rs.getString("studno");
		String fullname = rs.getString("fullname");
		String email = rs.getString("email");
		String mobile = rs.getString("mobile"); //may be NULL
		LocalDate regdate = rs.getDate("regdate").toLocalDate();
		String collcode = rs.getString("collcode");
		String course = rs.getString("course");
		int revno = rs.getInt("revno");
		
		return new ApplicantModel(
				studno, fullname, email, mobile,
				regdate, collcode, course, revno
		);
	}
	
	//returns the applicant with this student number, or null if none exists
	public ApplicantModel retrieveApplicant(String studno) throws SQLException {
		String query = BASE_SELECT + """
				WHERE ap.studno = ?
				""";
		
		ArrayList<ApplicantModel> applicants = this.getApplicantsBy(query, studno);
		
		if(applicants.isEmpty()) {
			return null;
		}
		
		return applicants.get(0);
	}
	
	public ArrayList<ApplicantModel> retrieveAllApplicants() throws SQLException {
		String query = BASE_SELECT + """
				ORDER BY ap.studno
				""";
		
		return this.getApplicantsBy(query);
	}
	
	//runs a BASE_SELECT-based query; params are bound to the ? placeholders in order
	public ArrayList<ApplicantModel> getApplicantsBy(String query, String... params) throws SQLException {
		
		ArrayList<ApplicantModel> applicants = new ArrayList<>();
		
		try(Connection conn = this.dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			for(int i = 0; i < params.length; i++) {
				pstmt.setString(i + 1, params[i]);
			}
			
			try(ResultSet rs = pstmt.executeQuery()){
				while(rs.next()) {
					applicants.add(this.instantiateApplicant(rs));
				}
				
				return applicants;
			}
		}
	}
}
