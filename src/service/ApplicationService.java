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
	private ApplicantService applicantService;
	private Scanner sc;
	
	public ApplicationService(ApplicationDAO applicationDAO,
			Scanner sc, ProgramService programService, OfferingService offeringService,
			ApplicantService applicantService) {
		this.applicationDAO = applicationDAO;
		this.sc = sc;
		this.programService = programService;
		this.offeringService = offeringService;
		this.applicantService = applicantService;
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
		if(applicationOffering == null) {
			throw new IllegalStateException("The application's offering was not found");
		}
		
		try {
			submitdt = LocalDate.parse(input.trim());
		}
		catch(DateTimeParseException e) {
			throw new IllegalArgumentException("Dates must use the format YYYY-MM-DD");
		}
		
		LocalDate present = LocalDate.now();
		
		//check if submit date is in the future
		if(submitdt.isAfter(present)) {
			throw new IllegalArgumentException("Submit date can't be in the future");
		}
		
		//check if submitdt is before applicant's regdate
		ApplicantModel applicant = this.applicantService.findByStudno(application.getStudno());
		if(applicant == null) {
			throw new IllegalStateException("The application's applicant was not found");
		}
		if(submitdt.isBefore(applicant.getRegdate())) {
			throw new IllegalArgumentException(
					"Submit date cannot be before the applicant's registration date (" + applicant.getRegdate() + ")");
		}
		
		//check if application's submit date is after the reception dates of its documents
		if(!this.applicationDAO.isBeforeAllDocsRecvdateDates(application.getAppno(), submitdt)) {
			throw new IllegalArgumentException(
					"Submit date must be before the reception date of all its documents");
		}
		
		//check if submit date is within the application window
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
	//ordinary edits: submission facts only, and only while the application is Submitted
	//modes: 1 submit date, 2 entry average (FirstAward), 3 income (FirstAward), 4 remarks
	//evaluation fields change through evaluate()/reopenEvaluation(),
	//status and decision date through recordDecision()
	//application = the record the staff member loaded and is editing
	public void editApplication(
			ApplicationModel application, int mode, String newVal) throws SQLException{
		
		//ordinary corrections are made while the application is submitted
		if(application.getStatus() != ApplicationStatus.SUBMITTED) {
			throw new IllegalArgumentException(
				"Cannot edit: only Submitted applications can be edited (current status: "
				+ application.getStatus().toDatabaseValue() + ")."
				+ " To correct an evaluation, reopen it first.");
		}
		
		newVal = (newVal == null) ? "" : newVal.trim();
		String column;
		
		switch(mode) {
			case 1: //submit date
				this.validateSubmitdt(newVal, application);
				column = "submitdt";
				break;
				
			case 2: //entry average (FirstAward only, 0-100)
				this.requireFirstAward(application);
				BigDecimal entryavg = this.parseAmount(newVal, "Entry average");
				if(entryavg.compareTo(new BigDecimal("100")) > 0) {
					throw new IllegalArgumentException("Entry average must be between 0 and 100");
				}
				newVal = entryavg.toPlainString();
				column = "entryavg";
				break;
			
			case 3: //declared annual household income (FirstAward only, >= 0)
				this.requireFirstAward(application);
				BigDecimal income = this.parseAmount(newVal, "Income");
				if(income.compareTo(new BigDecimal("9999999999.99")) > 0) { //DECIMAL(12,2) limit
					throw new IllegalArgumentException("Income is too large");
				}
				newVal = income.toPlainString();
				column = "income";
				break;
			
			case 4: //remarks (optional; blank clears them)
				if(newVal.length() > 255) {
					throw new IllegalArgumentException("Remarks must be at most 255 characters");
				}
				if(newVal.isEmpty()) {
					newVal = null;
				}
				column = "remarks";
				break;
			
			default:
				throw new IllegalArgumentException("Invalid edit mode!");
		}
		
		//status = 'Submitted' in the query too, in case it was evaluated or decided since it was loaded
		String query = "UPDATE application\n"
				+ "SET revno = revno + 1, " + column + " = ?\n"
				+ "WHERE appno = ?\n"
				+ "AND revno = ?\n"
				+ "AND status = 'Submitted'";
		
		boolean isSuccess = this.applicationDAO.updateApplication(query, application, newVal, mode);
		
		if(!isSuccess) {
			throw new IllegalArgumentException("You are editing an outdated record. Please reload and try again");
		}
	}
	
	//entry average and income only apply to FirstAward applications
	private void requireFirstAward(ApplicationModel application) throws SQLException{
		OfferingModel offering = this.offeringService.getOfferingByOfferno(application.getOfferno());
		if(offering == null) {
			throw new IllegalStateException("The application's offering was not found");
		}
		
		ProgramModel program = this.programService.getProgByProgCode(offering.getProgcode());
		if(program == null) {
			throw new IllegalStateException("The offering's program was not found");
		}
		
		if(program.getProgtype() != ProgramType.FIRSTAWARD) {
			throw new IllegalArgumentException(
					"Entry average and income only apply to First Award applications");
		}
	}
	
	//parses a non-negative amount with at most 2 decimal places (zero is a real value)
	private BigDecimal parseAmount(String input, String label) {
		BigDecimal value;
		try {
			value = new BigDecimal(input);
		}
		catch(NumberFormatException e) {
			throw new IllegalArgumentException(label + " must be a number");
		}
		
		if(value.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException(label + " cannot be negative");
		}
		if(value.stripTrailingZeros().scale() > 2) {
			throw new IllegalArgumentException(label + " can have at most 2 decimal places");
		}
		
		return value;
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
	//controlled correction: an Evaluated application whose evaluation is no longer valid
	//(evidence changed, or the evaluation was wrong) has its evaluation cleared and returns
	//to Submitted, so it can be evaluated again with all checks re-run
	//application = the record the staff member loaded and confirmed
	public void reopenEvaluation(ApplicationModel application) throws SQLException{
		
		if(application.getStatus() != ApplicationStatus.EVALUATED) {
			throw new IllegalArgumentException(
				"Cannot reopen evaluation: only Evaluated applications can be reopened (current status: "
				+ application.getStatus().toDatabaseValue() + ")");
		}
		
		boolean isSuccess = this.applicationDAO.resetEvalFields(
				application.getAppno(), application.getRevno());
		
		if(!isSuccess) {
			throw new IllegalArgumentException(
				"Cannot reopen evaluation: the application was changed by another user. Please reload and try again");
		}
	}
	
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