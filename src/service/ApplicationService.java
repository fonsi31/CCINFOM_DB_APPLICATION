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
	
	public ArrayList<ApplicationModel> getApplicationBy(
			int mode, String identifier) throws SQLException{
		
		String query;
		
		switch(mode) {
		
		//by appno
		case 1: 
			query = """
					SELECT *
					FROM application
					WHERE appno = ?
					""";
			
			return this.applicationDAO.getApplicationBy(query, identifier);
			
		//by studno
		case 2: 
			query = """
			SELECT *
			FROM application
			WHERE studno = ?
			""";
			
			return this.applicationDAO.getApplicationBy(query, identifier);
			
		case 3: //by studname
			query = """
			SELECT *
			FROM applicant JOIN application
							on applicant.studno = application.studno
			WHERE studname = ?
			""";
			
			return this.applicationDAO.getApplicationBy(query, identifier);
	
		case 4: //by offerno
			query = """
			SELECT *
			FROM application
			WHERE offerno = ?
			""";
			
			return this.applicationDAO.getApplicationBy(query, identifier);
			
		case 5: //by progcode
			query = """
			SELECT *
			FROM application
			WHERE offerno = ?
			""";
			
			return this.applicationDAO.getApplicationBy(query, identifier);
			
		case 6: //by agencode
			break;
		case 7: //by progtype
			break;
		case 8: //by status
			break;
		case 9: //by submission date
			break;
		case 10: //by evaluation date range
			break;
		default:
			break;
		}
	}
}