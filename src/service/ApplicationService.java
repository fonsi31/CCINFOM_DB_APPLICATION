package service;

import java.util.ArrayList;
import enums.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
	private OfferingService offeringService;
	private Scanner sc;
	
	public ApplicationService(ApplicationDAO applicationDAO,
			Scanner sc, ProgramService programService, OfferingService offeringService) {
		this.applicationDAO = applicationDAO;
		this.sc = sc;
		this.programService = programService;
		this.offeringService = offeringService;
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
			ApplicationStatus status = ApplicationStatus.fromDatabaseValue(identifier.replace(" ", ""));
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
			
			return this.applicationDAO.getApplicationsBy(query, from, to);
			
		case 10: //by evaluation date range
			validateDateRange(from, to);
			from = from.trim();
			to = to.trim();
			
			query = ApplicationDAO.BASE_SELECT + """
					WHERE a.evaldate BETWEEN ? AND ?
					ORDER BY a.evaldate, a.appno
					""";
			
			return this.applicationDAO.getApplicationsBy(query, from, to);
			
		default:
			throw new IllegalArgumentException("Invalid Search mode!");
		}
		
		return this.applicationDAO.getApplicationsBy(query, identifier);
	}
	
	//checks that both dates are valid yyyy-mm-dd and the end is not before the start
	private void validateDateRange(String from, String to) {
		LocalDate start;
		LocalDate end;
		
		try {
			start = LocalDate.parse(from.trim());
			end = LocalDate.parse(to.trim());
		}
		catch(NullPointerException | DateTimeParseException e) {
			throw new IllegalArgumentException("Dates must use the format YYYY-MM-DD");
		}
		
		if(end.isBefore(start)) {
			throw new IllegalArgumentException("End date cannot be before start date");
		}
	}
	
	private void validateSubmitdt(String input, ApplicationModel application) throws SQLException{
		LocalDate submitdt;
		
		OfferingModel applicationOffering = this.offeringService.getOfferingByOfferno(application.getOfferno());
		
		try {
			submitdt = LocalDate.parse(input.trim());
		}
		catch(DateTimeParseException e) {
			throw new IllegalArgumentException("Dates must use the format YYYY-MM-DD");
		}
		
		if(submitdt.isBefore(applicationOffering.getOpendt()) ||
			submitdt.isAfter(applicationOffering.getClosedt())) {
			throw new IllegalArgumentException("Submit date must be within the offering's application window");
		}
	}
	
	private void validateEvalDate(String input, ApplicationModel application) throws SQLException{
		LocalDate evalDate;
		
		try {
			evalDate = LocalDate.parse(input.trim());
		}
		catch(DateTimeParseException e) {
			throw new IllegalArgumentException("Dates must use the format YYYY-MM-DD");
		}
		
		if(evalDate.isBefore(application.getSubmitdt())) {
			throw new IllegalArgumentException("Evaluation must happen on or after submission");
		}
	}
	
	private void validateDecDate(String input, ApplicationModel application) throws SQLException{
		LocalDate decDate;
		
		try {
			decDate = LocalDate.parse(input.trim());
		}
		catch(DateTimeParseException e) {
			throw new IllegalArgumentException("Dates must use the format YYYY-MM-DD");
		}
		
		if(decDate.isBefore(application.getEvaldate())) {
			throw new IllegalArgumentException("Decision must happen on or after evaluation");
		}
	}
	
	//returns the application with this appno, or null if none exists
	public ApplicationModel findByAppno(String appno) throws SQLException{
		if(appno == null || appno.trim().isEmpty()) {
			return null;
		}
		
		ArrayList<ApplicationModel> result = this.getApplicationsBy(1, appno.trim(), null, null);
		
		if(result.isEmpty()) {
			return null;
		}
		
		return result.get(0);
	}
	
	//application = the record the staff member loaded and is editing
	//(its revno is used for the stale-edit check, so it must not be reloaded here)
	public void editApplication(
			ApplicationModel application, int mode, String newVal) throws SQLException{
		
		
		//check if newVal is valid for entryavg, income, score
		if(mode == 2 || mode == 3 || mode == 4) {
			if(!InputValidator.isValidBigDecimal(newVal)) {
				throw new IllegalArgumentException("Invalid input");
			}
			
			OfferingModel offeringOfApp = this.offeringService.getOfferingByOfferno(application.getOfferno());
			ProgramModel programOfApp = this.programService.getProgByProgCode(offeringOfApp.getProgcode());
			
			/**
			 * if the field the user/staff wants to edit is entryavg or income
			 * but the progtype is renewal
			 */
			if(mode != 4 && programOfApp.getProgtype() == ProgramType.RENEWAL) {
				throw new IllegalArgumentException("Access Denied. This is a renewal application");
			}
		}

		String header = """
						UPDATE application
						SET revno = revno + 1,""";
		
		String trailer = """
						WHERE appno = ?
						AND revno = ?
						""";
		String query;
		
		switch(mode) {
			case 1: //edit submitdt
				this.validateSubmitdt(newVal, application);
				query = header + " submitdt = ?\n" + trailer;
				break;
				
			case 2: //edit entryavg
				query = header + " entryavg = ?\n" + trailer;
				break;
			
			case 3: //edit income
				query = header + " income = ?\n" + trailer;
				break;
				
			case 4: //score
				query = header + " score = ?\n" + trailer;
				break;
				
			case 5: //evaldate
				this.validateEvalDate(newVal, application);
				query = header + " evaldate = ?\n" + trailer;
				break;
				
			case 6: //evalby
				query = header + " evalby = ?\n" + trailer;
				break;
			
			case 7: //status
				ApplicationStatus status = ApplicationStatus.fromDatabaseValue(newVal.replace(" ", ""));
				newVal = status.toDatabaseValue();
				
				query = header + " status = ?\n" + trailer;
				break;
				
			case 8: //decdate
				this.validateDecDate(newVal, application);	
				query = header + " decdate = ?\n" + trailer;
				break;
			
			case 9: //remarks
				query = header + " remarks = ?\n" + trailer;
				break;
			
			default:
				throw new IllegalArgumentException("Invalid edit mode!");
		}
		
		boolean isSuccess = this.applicationDAO.updateApplication(query, application, newVal, mode);
		
		if(!isSuccess) {
			throw new IllegalArgumentException("You are editing an outdated record. Please reload and try again");
		}
	}
	
	//application = the record the staff member was shown and confirmed
	public void deleteApplication(ApplicationModel application) throws SQLException{
		String appno = application.getAppno();
		
		//check if status != submitted
		if(application.getStatus() != ApplicationStatus.SUBMITTED) {
			throw new IllegalArgumentException(
				"Cannot delete: only Submitted applications can be deleted (current status: "
				+ application.getStatus().toDatabaseValue() + ")");	
		}

		boolean isSuccess;
		
		try {
			isSuccess = this.applicationDAO.deleteApplication(appno, application.getRevno());
		}
		catch(SQLIntegrityConstraintViolationException e) {
			throw new IllegalStateException(
					"Cannot delete: application has documents or a renewal check");
		}
		
		if(!isSuccess) {
			throw new IllegalArgumentException(
				"Deletion Blocked (stale record): please reload and try again");
		}
	}
	
	//application = the record the staff member loaded and is evaluating
	//evaldate is set to today; score, evaldate, evalby and the Evaluated state are saved together
	public void evaluate(
			ApplicationModel application, BigDecimal score, String evalby) throws SQLException{
		
		String appno = application.getAppno();
		
		//score: required, 0-100 (zero is a valid score)
		if(score == null) {
			throw new IllegalArgumentException("Score is required");
		}
		if(score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("100")) > 0) {
			throw new IllegalArgumentException(
					"Score should be within 0-100");
		}
		
		//evaluator: required, fits evalby VARCHAR(100)
		if(evalby == null || evalby.trim().isEmpty()) {
			throw new IllegalArgumentException("Evaluator name is required");
		}
		evalby = evalby.trim();
		if(evalby.length() > 100) {
			throw new IllegalArgumentException("Evaluator name must be at most 100 characters");
		}
		
		//an application shouldnt have an evaluation or a recorded decision yet
		if(application.getStatus() != ApplicationStatus.SUBMITTED) {
			throw new IllegalArgumentException(
				"Cannot evaluate: only Submitted applications can be evaluated (current status: "
				+ application.getStatus().toDatabaseValue() + ")");	
		}
		
		//every required document must be submitted and Accepted
		if(!this.applicationDAO.hasCompleteAcceptedDocs(appno)) {
			throw new IllegalArgumentException(
				"Cannot evaluate: a required document is missing, pending or rejected");
		}
		
		OfferingModel offering = offeringService.getOfferingByOfferno(application.getOfferno());
		if(offering == null) {
			throw new IllegalStateException("Cannot evaluate: the application's offering was not found");
		}
		
		ProgramModel program = programService.getProgByProgCode(offering.getProgcode());
		if(program == null) {
			throw new IllegalStateException("Cannot evaluate: the offering's program was not found");
		}
		
		LocalDate evalDate = LocalDate.now();
		
		//eligibility depends on the program type
		if(program.getProgtype() == ProgramType.RENEWAL) {
			//renewal: needs its renewal check to be Eligible
			if(!this.applicationDAO.hasEligibleRenewcheck(appno)) {
				throw new IllegalArgumentException(
						"Cannot evaluate: renewal application has no eligible renewal check");
			}
			
			//evaluation cannot precede the renewal check it relies on
			LocalDate checkdt = this.applicationDAO.getRenewcheckDate(appno);
			if(checkdt != null && evalDate.isBefore(checkdt)) {
				throw new IllegalArgumentException(
						"Cannot evaluate: evaluation date is before the renewal check date (" + checkdt + ")");
			}
		}
		else {
			//first award: submitted figures must meet the program thresholds
			if(application.getEntryavg() == null || application.getIncome() == null) {
				throw new IllegalStateException(
						"Cannot evaluate: first award application is missing its entry average or income");
			}
			if(application.getEntryavg().compareTo(program.getEntrymin()) < 0) {
				throw new IllegalArgumentException(
						"Cannot evaluate: entry average " + application.getEntryavg()
						+ " is below the program minimum of " + program.getEntrymin());
			}
			if(application.getIncome().compareTo(program.getIncomecp()) > 0) {
				throw new IllegalArgumentException(
						"Cannot evaluate: income " + application.getIncome()
						+ " exceeds the program ceiling of " + program.getIncomecp());
			}
		}
		
		//chronology: evaluation cannot precede submission or document verification
		if(evalDate.isBefore(application.getSubmitdt())) {
			throw new IllegalArgumentException(
					"Cannot evaluate: evaluation date is before the submission date (" + application.getSubmitdt() + ")");
		}
		
		LocalDate latestVerdate = this.applicationDAO.getLatestDocVerdate(appno);
		if(latestVerdate != null && evalDate.isBefore(latestVerdate)) {
			throw new IllegalArgumentException(
					"Cannot evaluate: evaluation date is before a document verification date (" + latestVerdate + ")");
		}
		
		//save score, evaldate, evalby and status together (stale-edit and status checked in the query)
		boolean isSuccess = this.applicationDAO.saveEvaluationDetails(
				appno, application.getRevno(), score, evalDate, evalby);
		
		if(!isSuccess) {
			throw new IllegalArgumentException(
					"Cannot evaluate: the application was changed by another user. Please reload and try again");
		}
	}
	
	//application = the record the staff member loaded
	//decision: Approved, Rejected or Withdrawn; decdate is set to today
	//remarks: required for Rejected and Withdrawn, optional for Approved
	public void recordDecision(
		ApplicationModel application, ApplicationStatus decision, String remarks) throws SQLException{
		
		ApplicationStatus currStatus = application.getStatus();	
		String appno = application.getAppno();
		
		//only final states are decisions
		if(decision != ApplicationStatus.APPROVED
			&& decision != ApplicationStatus.REJECTED
			&& decision != ApplicationStatus.WITHDRAWN) {
			throw new IllegalArgumentException(
					"Cannot record decision: a decision must be Approved, Rejected or Withdrawn");
		}
		
		//remarks: blank becomes null; Rejected/Withdrawn need a meaningful reason; fits VARCHAR(255)
		if(remarks != null) {
			remarks = remarks.trim();
			if(remarks.isEmpty()) {
				remarks = null;
			}
		}
		if(decision != ApplicationStatus.APPROVED && remarks == null) {
			throw new IllegalArgumentException(
					"Cannot record decision: a reason is required for " + decision.toDatabaseValue());
		}
		if(remarks != null && remarks.length() > 255) {
			throw new IllegalArgumentException(
					"Cannot record decision: remarks must be at most 255 characters");
		}
		
		//recording a decision needs that the application isnt alr final
		if(currStatus != ApplicationStatus.SUBMITTED
			&& currStatus != ApplicationStatus.EVALUATED) {
			throw new IllegalArgumentException(
					"Cannot record decision: the application already has a final decision ("
					+ currStatus.toDatabaseValue() + ")");
		}
		
		//approving needs a completed evaluation and an offering that isn't cancelled
		//(free slots are checked by the DAO inside the approval transaction)
		if(decision == ApplicationStatus.APPROVED) {
			if(currStatus != ApplicationStatus.EVALUATED) {
				throw new IllegalArgumentException(
					"Cannot approve application: the application needs to be evaluated first");
			}
			
			OfferingModel appOffering = this.offeringService.getOfferingByOfferno(application.getOfferno());
			if(appOffering == null) {
				throw new IllegalStateException("Cannot approve application: offering not found");
			}
			if(appOffering.getStatus() == OfferingStatus.CANCELED) {
				throw new IllegalArgumentException(
					"Cannot approve application: the offering was cancelled");
			}
		}
		
		//decision date is today; it cannot precede submission or evaluation
		LocalDate decdate = LocalDate.now();
		
		if(decdate.isBefore(application.getSubmitdt())) {
			throw new IllegalArgumentException(
					"Cannot record decision: decision date is before the submission date ("
					+ application.getSubmitdt() + ")");
		}
		if(application.getEvaldate() != null && decdate.isBefore(application.getEvaldate())) {
			throw new IllegalArgumentException(
					"Cannot record decision: decision date is before the evaluation date ("
					+ application.getEvaldate() + ")");
		}
		
		//save (approval runs the slot check + save in one locked transaction)
		boolean isSuccess;
		if(decision == ApplicationStatus.APPROVED) {
			isSuccess = this.applicationDAO.approveApplication(
					application.getOfferno(), decdate, appno, application.getRevno(), remarks);
		}
		else {
			isSuccess = this.applicationDAO.saveDecision(
					decision.toDatabaseValue(), decdate, remarks, appno, application.getRevno());
		}
		
		if(!isSuccess) {
			throw new IllegalArgumentException(
					"Cannot record decision: the application was changed by another user. Please reload and try again");
		}
	}
}