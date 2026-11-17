package dao;
import util.*;
import model.*;
import java.sql.*;

public class ApplicationDAO {
	
	private DBConnection dbConn;
	
	public ApplicationDAO(DBConnection dbConn) {
		this.dbConn = dbConn;
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
}