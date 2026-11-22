package dao;

import util.*;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import enums.*;
import java.math.BigDecimal;

public class ProgramDAO {
	
	//common SELECT for program retrieval
	//joins the subtype tables so both FirstAward (entrymin, incomecp)
	//and Renewal (minunits, maxgpa) values are available,
	//and the agency table so searches can filter by agency name
	public static final String BASE_SELECT = """
			SELECT p.progcode, p.progname, p.agencode, p.progtype, p.active, p.revno,
			       fap.entrymin, fap.incomecp,
			       rp.minunits, rp.maxgpa
			FROM program p
			JOIN agency a
			     ON p.agencode = a.agencode
			LEFT JOIN first_award_program fap
			     ON p.progcode = fap.progcode
			LEFT JOIN renewal_program rp
			     ON p.progcode = rp.progcode
			""";
	
	private DBConnection dbConn;
	
	public ProgramDAO(DBConnection dbConn) {
		this.dbConn = dbConn;
	}
	
	//for retrieval: builds a ProgramModel from the current row of a BASE_SELECT result
	public ProgramModel instantiateProgram(ResultSet rs) throws SQLException {
		String progcode = rs.getString("progcode");
		String progname = rs.getString("progname");
		String agencode = rs.getString("agencode");
		ProgramType progtype = ProgramType.fromDatabaseValue(rs.getString("progtype"));
		
		int active = rs.getInt("active");
		int revno = rs.getInt("revno");
		
		//subtype values are NULL for the other program type
		BigDecimal entrymin = rs.getBigDecimal("entrymin");
		BigDecimal incomecp = rs.getBigDecimal("incomecp");
		Integer minunits = rs.getObject("minunits", Integer.class);
		BigDecimal maxgpa = rs.getBigDecimal("maxgpa");
		
		return new ProgramModel(
				progcode, progname, agencode, progtype,
				entrymin, incomecp, minunits, maxgpa,
				active, revno
		);
	}
	
	public ProgramModel retrieveProg(String progcode) throws SQLException{
		String query = BASE_SELECT + """
				WHERE p.progcode = ?
				""";
		
		ArrayList<ProgramModel> programs = this.getProgramsBy(query, progcode);
		
		if(programs.isEmpty()) {
			return null;
		}
		
		return programs.get(0);
	}
	
	public ArrayList<ProgramModel> retrieveAllPrograms() throws SQLException{
		String query = BASE_SELECT + """
				ORDER BY p.progcode
				""";
		
		ArrayList<ProgramModel> programs = new ArrayList<>();
		
		try(Connection conn = this.dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery()){
			
			while(rs.next()) {
				programs.add(this.instantiateProgram(rs));
			}
		}
		
		return programs;
	}
	
	public ArrayList<ProgramModel> getProgramsBy(String query, String identifier) throws SQLException{
		
		ArrayList<ProgramModel> programs = new ArrayList<>();
		
		try(Connection conn = this.dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			pstmt.setString(1, identifier);
			
			try(ResultSet rs = pstmt.executeQuery()){
				while(rs.next()) {
					programs.add(this.instantiateProgram(rs));
				}
				
				return programs;
			}
		}
	}
}
