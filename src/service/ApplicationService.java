package service;

import java.util.ArrayList;
import enums.*;
import java.time.LocalDate;
import dao.*;
import model.*;
import util.InputValidator;
import java.math.BigDecimal;
import java.util.Scanner;
import util.*;
import java.sql.*;

public class ApplicationService {
	
	private ApplicationDAO applicationDAO;
	private ProgramService programService;
	private Scanner sc;
	
	public ApplicationService(ApplicationDAO applicationDAO, Scanner sc, ProgramService programService) {
		this.applicationDAO = applicationDAO;
		this.sc = sc;
		this.programService = programService;
	}
	
	public ArrayList<ApplicationModel> getAllApplications() throws SQLException{
		return applicationDAO.retrieveAllApplications();
	}
	
	public void createApplication(ApplicantModel applicant, OfferingModel offering) throws SQLException{
		String studno = applicant.getStudno();
		String offerno = offering.getOfferno();
		
		//check if the offering is still open and the current date is within the application window
		this.validateOffering(offering);
		
		//check if a student has a current application to the offering
		if(this.applicationDAO.existsByStudnoAndOfferno(studno, offerno)) {
			throw new IllegalArgumentException(
				 	"Student has already applied to this offering");
		}
		
		//prepare the application's attributes
		String appno = this.generateAppno();
		LocalDate submitdt = LocalDate.now();
		BigDecimal entryavg = null;
		BigDecimal income = null;
		ProgramModel program = this.programService.getProgByProgCode(offering.getProgcode());
		
		if(program == null) {
			throw new IllegalArgumentException(
					"No program found!");
		}
		
		//ask for entryavg and income if offering is of firstaward progtype
		if (program.getProgtype() == ProgramType.FIRSTAWARD) {
			//ask user to enter entryavg and income
			while (true) {
				entryavg = InputValidator.getValidEntryavg(sc);
				income = InputValidator.getValidIncome(sc);
				
				if (entryavg != null && income != null) {
					break;
				}
				
				System.out.println("Invalid Input/s!");
				TerminalManager.pause(sc);
			}
		}
		
		//instantiate an application object
		ApplicationModel application = new ApplicationModel(
				appno, studno, offerno, submitdt, entryavg, income);
		
		//insert to db
		boolean success = applicationDAO.insertApplication(application);
		
		if(success) {
			System.out.println("Application successfully created!");
		}
		else {
			System.out.println("Application was not created");
		}
	}
	
	//checks if offering is still open and application is still within the application window
	public void validateOffering(OfferingModel offering) {
		LocalDate today = LocalDate.now();
		
		if(offering.getStatus() != OfferingStatus.OPEN) {
			throw new IllegalArgumentException(
					"Offering is not Open");
			
		}
		
		if(today.isBefore(offering.getOpendt()) || today.isAfter(offering.getClosedt())){
			throw new IllegalArgumentException(
					"Already outside the application window");
		}
	}
	
	//generates unique appno
	public String generateAppno() throws SQLException{
		//generate unique application number for a new application
		String currentYear = String.valueOf(LocalDate.now().getYear());
		String latestAppno = this.applicationDAO.getLatestAppno();
		
		if(latestAppno == null) {
			return "AP" + currentYear + "0001";
		}
	
		int latestAppnoSeq = Integer.parseInt(latestAppno.substring(6));
		latestAppnoSeq++;
		
		String formattedSeq = String.format("%04d", latestAppnoSeq);
		
		return "AP" + currentYear + formattedSeq;
	}
	
	public ArrayList<ApplicationModel> getApplicationsBy(
			int mode, String identifier, String from, String to) throws SQLException{
		
		String query;
		
		//modes 9 and 10 search by date range and don't use identifier
		if(mode != 9 && mode != 10) {
			if(identifier == null || identifier.trim().isEmpty()) {
				throw new IllegalArgumentException("Search value cannot be empty");
			}
			identifier = identifier.trim();
		}
		
		switch(mode) {
		
		case 1: //by appno
			query = ApplicationDAO.BASE_SELECT + """
					WHERE a.appno = ?
					ORDER BY a.appno
					""";
			break;
			
		case 2: //by studno
			query = ApplicationDAO.BASE_SELECT + """
					WHERE a.studno = ?
					ORDER BY a.appno
					""";
			break;
			
		case 3: //by applicant name (partial match)
			query = ApplicationDAO.BASE_SELECT + """
					WHERE ap.fullname LIKE ?
					ORDER BY ap.fullname, a.appno
					""";
			identifier = "%" + identifier + "%";
			break;
	
		case 4: //by offerno
			query = ApplicationDAO.BASE_SELECT + """
					WHERE a.offerno = ?
					ORDER BY a.appno
					""";
			break;
			
		case 5: //by progcode
			query = ApplicationDAO.BASE_SELECT + """
					WHERE p.progcode = ?
					ORDER BY a.appno
					""";
			break;
			
		case 6: //by agencode
			query = ApplicationDAO.BASE_SELECT + """
					WHERE p.agencode = ?
					ORDER BY a.appno
					""";
			break;
			
		case 7: //by progtype (accepts "First Award", "FirstAward", "Renewal")
			ProgramType progtype = ProgramType.fromDatabaseValue(identifier.replace(" ", ""));
			identifier = progtype.toDatabaseValue();
			
			query = ApplicationDAO.BASE_SELECT + """
					WHERE p.progtype = ?
					ORDER BY a.appno
					""";
			break;
			
		case 8: //by status
			ApplicationStatus status = ApplicationStatus.fromDatabaseValue(identifier);
			identifier = status.toDatabaseValue();
			
			query = ApplicationDAO.BASE_SELECT + """
					WHERE a.status = ?
					ORDER BY a.appno
					""";
			break;
			
		case 9: //by submission date range
			validateDateRange(from, to);
			from = from.trim();
			to = to.trim();
			
			query = ApplicationDAO.BASE_SELECT + """
					WHERE a.submitdt BETWEEN ? AND ?
					ORDER BY a.submitdt, a.appno
					""";
			break;
			
		case 10: //by evaluation date range
			validateDateRange(from, to);
			from = from.trim();
			to = to.trim();
			
			query = ApplicationDAO.BASE_SELECT + """
					WHERE a.evaldate BETWEEN ? AND ?
					ORDER BY a.evaldate, a.appno
					""";
			break;
			
		default:
			throw new IllegalArgumentException("Invalid Search mode!");
		}
		
		return this.applicationDAO.getApplicationsBy(query, identifier, from, to, mode);
	}
	
	//checks that both dates are valid yyyy-MM-dd and the end is not before the start
	private void validateDateRange(String from, String to) {
		LocalDate start;
		LocalDate end;
		
		try {
			start = LocalDate.parse(from.trim());
			end = LocalDate.parse(to.trim());
		}
		catch(NullPointerException | java.time.format.DateTimeParseException e) {
			throw new IllegalArgumentException("Dates must use the format YYYY-MM-DD");
		}
		
		if(end.isBefore(start)) {
			throw new IllegalArgumentException("End date cannot be before start date");
		}
	}
}