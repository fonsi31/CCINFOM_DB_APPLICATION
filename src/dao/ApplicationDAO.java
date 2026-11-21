package dao;

import util.*;
import java.util.ArrayList;
import model.*;
import enums.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class ApplicationDAO {
	
	private DBConnection dbConn;
	
	public ApplicationDAO(DBConnection dbConn) {
		this.dbConn = dbConn;
	}
	
	//for retrieval
	public ApplicationModel instantiateApplication(ResultSet rs) throws SQLException {
		String appno = rs.getString("appno");
        String studno = rs.getString("studno");
        String offerno = rs.getString("offerno");

        LocalDate submitdt = rs.getDate("submitdt").toLocalDate();

        BigDecimal entryavg = rs.getBigDecimal("entryavg");
        BigDecimal income = rs.getBigDecimal("income");
        BigDecimal score = rs.getBigDecimal("score");

        LocalDate evaldate = rs.getDate("evaldate") != null
                ? rs.getDate("evaldate").toLocalDate() : null;

        String evalby = rs.getString("evalby");

        ApplicationStatus status =
                ApplicationStatus.valueOf(
                        rs.getString("status").toUpperCase()
                );

        LocalDate decdate = rs.getDate("decdate") != null
                ? rs.getDate("decdate").toLocalDate() : null;

        String remarks = rs.getString("remarks");
        int revno = rs.getInt("revno");

        ApplicationModel application = new ApplicationModel(
                appno, studno, offerno, submitdt,
                entryavg, income, score, evaldate,
                evalby, status, decdate, remarks, revno
        );
        
        return application;
	}
	
	public boolean existsByStudnoAndOfferno(String studno, String offerno) throws SQLException{
		String query = """
				SELECT COUNT (*)
				FROM application
				where studno = ?
				and offerno = ?
				""";
		
		try (Connection conn = this.dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			pstmt.setString(1, studno);
			pstmt.setString(2, offerno);
			
			try(ResultSet rs = pstmt.executeQuery()){
				rs.next();
				return rs.getInt(1) > 0;
			}
		}
		
	}
	
	public boolean insertApplication(ApplicationModel application) throws SQLException{
		String query = """
				INSERT INTO application
				(appno, studno, offerno, submitdt, entryavg, income)
				VALUES (?, ?, ?, ?, ?)
				""";
				
			try(Connection conn = this.dbConn.connect();
				PreparedStatement pstmt = conn.prepareStatement(query)){
				
				pstmt.setString(1, application.getAppno());
				pstmt.setString(2, application.getStudno());
				pstmt.setString(3, application.getOfferno());
				pstmt.setDate(4, Date.valueOf(application.getSubmitdt()));
				
				if(application.getEntryavg() == null) {
					pstmt.setNull(5, Types.DECIMAL);
				}
				else {
					pstmt.setBigDecimal(5, application.getEntryavg());
				}
				
				if (application.getIncome() == null) {
		            pstmt.setNull(6, Types.DECIMAL);
		        } else {
		            pstmt.setBigDecimal(6, application.getIncome());
		        }
				
				return pstmt.executeUpdate() > 0;
			}
	}
	
	public String getLatestAppno() throws SQLException{
		String query = """
				SELECT appno
				FROM application
				WHERE appno LIKE CONCAT('AP', 'YEAR(CURRDATE())', '%')
				ORDER BY appno DESC
				LIMIT 1
				""";
		
		try(Connection conn = this.dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			try(ResultSet rs = pstmt.executeQuery()){
				if(rs.next()) {
					return rs.getNString("appno");
				}
				return null;
			}
		}		
	}
	
	public ArrayList<ApplicationModel> retrieveAllApplications() throws SQLException {
	    String query = """
	            SELECT *
	            FROM application
	            """;

	    ArrayList<ApplicationModel> applications = new ArrayList<>();

	    try (Connection conn = dbConn.connect();
	         PreparedStatement pstmt = conn.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {

	            ApplicationModel application = this.instantiateApplication(rs);

	            applications.add(application);
	        }
	    }

	    return applications;
	}
	
	public ArrayList<ApplicationModel> getApplicationBy(
			String query, String identifier, String from, String to, int mode) throws SQLException{
		
		ArrayList<ApplicationModel> applications = new ArrayList<>();
		
		try(Connection conn = this.dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			if(mode == 9 || mode == 10) { //search filter is by date
				pstmt.setString(1, from);
				pstmt.setString(2, to);
			}
			else { //search filter is by a table identifier
				pstmt.setString(1, identifier);
			}
			
			try(ResultSet rs = pstmt.executeQuery()){
				while(rs.next()) {
					ApplicationModel application = this.instantiateApplication(rs);
					applications.add(application);
				}
				
				return applications;
			}
		}
	}
}