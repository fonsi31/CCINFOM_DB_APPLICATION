package dao;

import util.*;
import java.util.ArrayList;
import model.*;
import enums.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class ApplicationDAO {
	
	//common SELECT for application searches
	//a = application, ap = applicant, o = offering, p = program
	//only the application's columns (a.*) are returned, so column names are never ambiguous
	public static final String BASE_SELECT = """
			SELECT a.*
			FROM application a
			JOIN applicant ap ON ap.studno  = a.studno
			JOIN offering  o  ON o.offerno  = a.offerno
			JOIN program   p  ON p.progcode = o.progcode
			""";
	
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
				VALUES (?, ?, ?, ?, ?, ?)
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
	
	public ArrayList<ApplicationModel> getApplicationsBy(
			String query, String... params) throws SQLException{
		
		ArrayList<ApplicationModel> applications = new ArrayList<>();
		
		try(Connection conn = this.dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			for(int i = 0; i < params.length; i++) {
				pstmt.setString(i + 1, params[i]);
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
	
	public boolean updateApplication(
			String query, ApplicationModel app, String newVal, int mode) throws SQLException{
		
		try(Connection conn = this.dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			if(mode == 1 || mode == 5 || mode == 8) {
				pstmt.setObject(1, LocalDate.parse(newVal.trim()));
			}
			else if(mode == 2 || mode == 3 || mode == 4) {
				pstmt.setBigDecimal(1, new BigDecimal(newVal));
			}
			else {
				pstmt.setString(1, newVal);
			}
			
			
			pstmt.setString(2, app.getAppno());
			pstmt.setInt(3, app.getRevno());
			
			int rows = pstmt.executeUpdate();
			
			return rows > 0;
		}
	}
	
	public boolean deleteApplication(String appno, int revno) throws SQLException{
		String query = """
						DELETE FROM application
						WHERE appno = ?
						AND revno = ?
						AND status = 'Submitted'
						""";
		
		try(Connection conn = this.dbConn.connect();
				PreparedStatement pstmt = conn.prepareStatement(query)){
				
				pstmt.setString(1, appno);
				pstmt.setInt(2, revno);
				
				return pstmt.executeUpdate() > 0;
		}
	}
	
	public boolean hasCompleteAcceptedDocs(String appno) throws SQLException{
		String query = """
						select count(*)
						from program_document pd join program p
	                    on pd.progtype = p.progtype                    
						join offering o
	                    on p.progcode = o.progcode
	                    join application a
	                    on o.offerno = a.offerno
	                    left join document d
	                    on a.appno = d.appno
	                    and d.doctype = pd.doctype
						where a.appno = ?
						and (d.appno is null or d.result != 'Accepted')
						""";
		
		try(Connection conn = this.dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			pstmt.setString(1, appno);
			
			try(ResultSet rs = pstmt.executeQuery()){
				rs.next();
				int count = rs.getInt(1);
				
				return count == 0;
			}
		}
	}
	
	public boolean hasEligibleRenewcheck(String appno) throws SQLException{
		String query = """
						SELECT count(*)
						FROM renewcheck
						where appno = ?
						and result = 'Eligible'
						""";
		
		try(Connection conn = this.dbConn.connect();
				PreparedStatement pstmt = conn.prepareStatement(query)){
				
				pstmt.setString(1, appno);
				
				try(ResultSet rs = pstmt.executeQuery()){
					rs.next();
					int count = rs.getInt(1);
					
					return count != 0;
				}
			}
	}
	
	//latest verification date among the application's documents (null if none verified)
	public LocalDate getLatestDocVerdate(String appno) throws SQLException{
		String query = """
						SELECT MAX(verdate)
						FROM document
						WHERE appno = ?
						""";
		
		try(Connection conn = this.dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			pstmt.setString(1, appno);
			
			try(ResultSet rs = pstmt.executeQuery()){
				rs.next();
				Date latest = rs.getDate(1);
				return latest == null ? null : latest.toLocalDate();
			}
		}
	}
	
	//date of the application's renewal check (null if it has none)
	public LocalDate getRenewcheckDate(String appno) throws SQLException{
		String query = """
						SELECT checkdt
						FROM renewcheck
						WHERE appno = ?
						""";
		
		try(Connection conn = this.dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			pstmt.setString(1, appno);
			
			try(ResultSet rs = pstmt.executeQuery()){
				if(rs.next()) {
					return rs.getDate(1).toLocalDate();
				}
				return null;
			}
		}
	}
	
	//saves score, evaluation date, evaluator and the Evaluated state together in one statement
	//returns false if the record is stale (revno changed) or no longer Submitted
	public boolean saveEvaluationDetails(String appno, int revno, BigDecimal score,
			LocalDate evaldate, String evalby) throws SQLException{
		String query = """
						UPDATE application
						SET score = ?,
						    evaldate = ?,
						    evalby = ?,
						    status = 'Evaluated',
						    revno = revno + 1
						WHERE appno = ?
						AND revno = ?
						AND status = 'Submitted'
						""";
		
		try(Connection conn = this.dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			pstmt.setBigDecimal(1, score);
			pstmt.setDate(2, Date.valueOf(evaldate));
			pstmt.setString(3, evalby);
			pstmt.setString(4, appno);
			pstmt.setInt(5, revno);
			
			return pstmt.executeUpdate() == 1;
		}
	}
	
	/**
	 * approves an Evaluated application if its offering still has a free slot
	* lock, count and save run in ONE transaction on ONE connection, so two approvals
	* for the same offering take turns and cannot both claim the last slot
	* returns false if the record is stale or no longer Evaluated; throws if the offering is full
	*/
	public boolean approveApplication(
			String offerno, LocalDate decdate, String appno, int revno, String remarks) throws SQLException{
		String query = """
				SELECT slots
				from offering
				where offerno = ?
				FOR UPDATE
				""";
		
		try(Connection conn = this.dbConn.connect()){
			conn.setAutoCommit(false);
			
			try(PreparedStatement pstmt = conn.prepareStatement(query)){
				pstmt.setString(1, offerno);
				
				try(ResultSet rs = pstmt.executeQuery()){
					if(!rs.next()) {
						conn.rollback();
						throw new IllegalArgumentException("Cannot approve: offering not found");
					}
					
					int slots = rs.getInt(1);
					int takenSlots = this.getTakenSlots(offerno, conn);
					
					//no slots left
					if(takenSlots >= slots) {
						conn.rollback();
						throw new IllegalArgumentException("Cannot approve: slots are already full");
					}
					
					boolean isSuccess = this.saveDecision(conn, "Approved", decdate, remarks, appno, revno);
					
					if(isSuccess) {
						conn.commit();
						return true;
					}
					
					conn.rollback();
					return false;
				}
				catch(SQLException | RuntimeException e) {
					conn.rollback();
					throw e;
				}
			}
		}
	}
	
	private int getTakenSlots(String offerno, Connection conn) throws SQLException{
		String query = """
				select count(*)
				from application
				where offerno = ?
				and status = 'Approved'
				""";
		
		try(PreparedStatement pstmt = conn.prepareStatement(query)){
			pstmt.setString(1, offerno);
			
			try(ResultSet rs = pstmt.executeQuery()){
				rs.next();
				return rs.getInt(1);
			}
		}
		
	}
	
	private boolean saveDecision(Connection conn, String status, LocalDate decdate,
			String remarks, String appno, int revno) throws SQLException{
		
		String allowedFrom = status.equals("Approved") ? "and status = 'Evaluated'" :
											"and status in ('Submitted', 'Evaluated')";
		
		String query = """
				UPDATE application
				SET status = ?,
				decdate = ?,
				remarks = COALESCE(?, remarks),
				revno = revno + 1
				where appno = ?
				and revno = ?
				""" + allowedFrom;
		
		
		
		try(PreparedStatement pstmt = conn.prepareStatement(query)){
			pstmt.setString(1, status);
			pstmt.setDate(2, Date.valueOf(decdate));
			pstmt.setString(3, remarks);
			pstmt.setString(4, appno);
			pstmt.setInt(5,  revno);
			
			return pstmt.executeUpdate() == 1;
		}
	}
	
	public boolean saveDecision(String status, LocalDate decdate,
			String remarks, String appno, int revno) throws SQLException{
	
		try(Connection conn = this.dbConn.connect()){

			return this.saveDecision(conn, status, decdate, remarks, appno, revno);
		}
	}
}
	
	
	