package service;
import java.util.ArrayList;
import java.time.LocalDate;
import dao.*;
import model.*;
import util.InputValidator;
import java.math.BigDecimal;
import java.util.Scanner;
import util.*;

public class ApplicationService {
	
	private ApplicationDAO applicationDAO;
	private Scanner sc;
	
	public ApplicationService(ApplicationDAO applicationDAO, Scanner sc) {
		this.applicationDAO = applicationDAO;
		this.sc = sc;
	}
	
	public List<ApplicationModel> getAllApplications() {
		
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
		
		String appno = this.generateAppno();
		LocalDate submitdt = LocalDate.now();
		BigDecimal entryavg = null;
		BigDecimal income = null;
		ProgramModel program = ProgramService.getProgByProgcode(offering.getProgcode());
		
		if (program.getProgtype().equalsIgnoreCase("firstaward")) {
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
		
		ApplicationModel application = new ApplicationModel(
				appno, studno, offerno, submitdt, entryavg, income);
		
		applicationDAO.insertApplication(application);
	}
	
	public void validateOffering(OfferingModel offering) {
		LocalDate today = LocalDate.now();
		
		if(!(offering.getStatus().equalsIgnoreCase("Open"))) {
			throw new IllegalArgumentException(
					"Offering is not Open");
			
		}
		
		if(today.isBefore(offering.getOpendt()) || today.isAfter(offering.getClosedt())){
			throw new IllegalArgumentException(
					"Already outside the application window");
		}
	}
	
	public String generateAppno() {
		//generate unique application number for a new application
	}
}
