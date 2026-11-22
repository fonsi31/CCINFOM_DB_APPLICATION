package dao;

import util.*;
import model.*;
import enums.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class OfferingDAO {
	
	//common SELECT for offering searches
	//every search field (reference, program, state, application and coverage dates)
	//is a column of offering itself, so no joins are needed
	public static final String BASE_SELECT = """
			SELECT o.*
			FROM offering o
			""";
	
	private DBConnection dbConn;
	
	public OfferingDAO(DBConnection dbConn) {
		this.dbConn = dbConn;
	}
	
	//for retrieval: builds an OfferingModel from the current row of a BASE_SELECT result
	public OfferingModel instantiateOffering(ResultSet rs) throws SQLException {
		String offerno = rs.getString("offerno");
		String progcode = rs.getString("progcode");
		String title = rs.getString("title");
		
		LocalDate opendt = rs.getDate("opendt").toLocalDate();
		LocalDate closedt = rs.getDate("closedt").toLocalDate();
		LocalDate coverstart = rs.getDate("coverst").toLocalDate();
		LocalDate coverend = rs.getDate("coverend").toLocalDate();
		
		int slots = rs.getInt("slots");
		OfferingStatus status = OfferingStatus.fromDatabaseValue(rs.getString("status"));
		String remarks = rs.getString("remarks");
		int revno = rs.getInt("revno");
		
		return new OfferingModel(
				offerno, progcode, title,
				opendt, closedt, coverstart, coverend,
				slots, status, remarks, revno
		);
	}
	
	public OfferingModel retrieveOffering(String offerno) throws SQLException {
		String query = BASE_SELECT + """
				WHERE o.offerno = ?
				""";
		
		ArrayList<OfferingModel> offerings = this.getOfferingsBy(query, offerno);
		
		if(offerings.isEmpty()) {
			return null;
		}
		
		return offerings.get(0);
	}
	
	public ArrayList<OfferingModel> retrieveAllOfferings() throws SQLException {
		String query = BASE_SELECT + """
				ORDER BY o.offerno
				""";
		
		return this.getOfferingsBy(query);
	}
	
	//runs a BASE_SELECT-based query; params are bound to the ? placeholders in order
	public ArrayList<OfferingModel> getOfferingsBy(String query, String... params) throws SQLException {
		
		ArrayList<OfferingModel> offerings = new ArrayList<>();
		
		try(Connection conn = this.dbConn.connect();
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			for(int i = 0; i < params.length; i++) {
				pstmt.setString(i + 1, params[i]);
			}
			
			try(ResultSet rs = pstmt.executeQuery()){
				while(rs.next()) {
					offerings.add(this.instantiateOffering(rs));
				}
				
				return offerings;
			}
		}
	}
}
