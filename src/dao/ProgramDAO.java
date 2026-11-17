package dao;

import util.*;
import model.*;
import java.sql.*;
import enums.*;
import java.math.BigDecimal;

public class ProgramDAO {
	
	private DBConnection dbConn;
	
	public ProgramDAO(DBConnection dbConn) {
		this.dbConn = dbConn;
	}
	
	public ProgramModel retrieveProg(String progcode) throws SQLException{
		String query = """
				SELECT *
				FROM program p left join renewal_progam rp
							on p.progcode = rp.progcode
							left join first_award_program fap
							on p.progcode = fap.progcode
				WHERE progcode = ?
				""";
		
		try(Connection conn = dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			pstmt.setString(1, progcode);
			
			try(ResultSet rs = pstmt.executeQuery()){
				
				if(rs.next()) {
					
					String progname = rs.getNString("progname");
					String agencode = rs.getNString("agencode");
					String progtypeInDB = rs.getNString("progtype");
					
					int active = rs.getInt("active");
					int revno = rs.getInt("revno");
					
					int minunits = rs.getInt("minunits");
				    BigDecimal maxgpa = rs.getBigDecimal("maxgpa");

				    BigDecimal entrymin = rs.getBigDecimal("entrymin");
				    BigDecimal incomecp = rs.getBigDecimal("incomecp");
					
					
					ProgramType progtype;
					
					if (progtypeInDB.equals("Renewal")) {
						progtype = ProgramType.RENEWAL;
					}
					else {
						progtype = ProgramType.FIRSTAWARD;
					}
					
					return new ProgramModel(
					        progcode, progname, agencode, progtype,
					        entrymin, incomecp, minunits, maxgpa,
					        active, revno
					    );
				}
				
				return null;
			}
		}

	}
}