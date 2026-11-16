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
	
	public void insertApplication(ApplicationModel application) {
		//
	}
}
