package service;

import dao.*;
import enums.*;
import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class OfferingService {
	
	private OfferingDAO offeringDAO;
	
	public OfferingService(OfferingDAO offeringDAO) {
		this.offeringDAO = offeringDAO;
	}
	
	public OfferingModel getOfferingByOfferno(String offerno) throws SQLException {
		if(offerno == null || offerno.trim().isEmpty()) {
			return null;
		}
		
		return this.offeringDAO.retrieveOffering(offerno.trim());
	}
	
	public ArrayList<OfferingModel> getAllOfferings() throws SQLException {
		return this.offeringDAO.retrieveAllOfferings();
	}
	
	public ArrayList<OfferingModel> getOfferingsByProg(String progcode) throws SQLException {
		return this.getOfferingsBy(2, progcode, null, null);
	}
	
	//offerings that can accept a new application today:
	//status is Open and today is within the application window
	public ArrayList<OfferingModel> getOpenOfferings() throws SQLException {
		String today = LocalDate.now().toString();
		
		String query = OfferingDAO.BASE_SELECT + """
				WHERE o.status = 'Open'
				AND ? BETWEEN o.opendt AND o.closedt
				ORDER BY o.closedt, o.offerno
				""";
		
		return this.offeringDAO.getOfferingsBy(query, today);
	}
	
	//search fields required by the spec: reference, program, state,
	//application dates and coverage dates
	public ArrayList<OfferingModel> getOfferingsBy(
			int mode, String identifier, String from, String to) throws SQLException {
		
		String query;
		
		//modes 4 and 5 search by date range and don't use identifier
		if(mode != 4 && mode != 5) {
			if(identifier == null || identifier.trim().isEmpty()) {
				throw new IllegalArgumentException("Search value cannot be empty");
			}
			identifier = identifier.trim();
		}
		
		switch(mode) {
		
		case 1: //by offerno
			query = OfferingDAO.BASE_SELECT + """
					WHERE o.offerno = ?
					ORDER BY o.offerno
					""";
			break;
			
		case 2: //by progcode
			query = OfferingDAO.BASE_SELECT + """
					WHERE o.progcode = ?
					ORDER BY o.opendt, o.offerno
					""";
			break;
			
		case 3: //by status (Planned, Open, Closed, Cancelled)
			OfferingStatus status = OfferingStatus.fromDatabaseValue(identifier);
			identifier = status.toDatabaseValue();
			
			query = OfferingDAO.BASE_SELECT + """
					WHERE o.status = ?
					ORDER BY o.offerno
					""";
			break;
			
		case 4: //application window overlaps the date range
			String[] appRange = this.validateDateRange(from, to);
			
			//window starts on/before the range ends AND ends on/after the range starts
			query = OfferingDAO.BASE_SELECT + """
					WHERE o.opendt <= ? AND o.closedt >= ?
					ORDER BY o.opendt, o.offerno
					""";
			return this.offeringDAO.getOfferingsBy(query, appRange[1], appRange[0]);
			
		case 5: //coverage period overlaps the date range
			String[] coverRange = this.validateDateRange(from, to);
			
			query = OfferingDAO.BASE_SELECT + """
					WHERE o.coverst <= ? AND o.coverend >= ?
					ORDER BY o.coverst, o.offerno
					""";
			return this.offeringDAO.getOfferingsBy(query, coverRange[1], coverRange[0]);
			
		default:
			throw new IllegalArgumentException("Invalid Search mode!");
		}
		
		return this.offeringDAO.getOfferingsBy(query, identifier);
	}
	
	//checks both dates and their order; returns {start, end} as yyyy-MM-dd strings
	private String[] validateDateRange(String from, String to) {
		LocalDate start = this.parseDate(from);
		LocalDate end = this.parseDate(to);
		
		if(end.isBefore(start)) {
			throw new IllegalArgumentException("End date cannot be before start date");
		}
		
		return new String[] {start.toString(), end.toString()};
	}
	
	//parses a yyyy-MM-dd date, with a clear message on bad input
	private LocalDate parseDate(String value) {
		try {
			return LocalDate.parse(value.trim());
		}
		catch(NullPointerException | DateTimeParseException e) {
			throw new IllegalArgumentException("Dates must use the format YYYY-MM-DD");
		}
	}
}
