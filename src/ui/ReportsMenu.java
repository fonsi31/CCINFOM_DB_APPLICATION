package ui;

import java.util.Scanner;
import java.util.ArrayList;
import java.sql.SQLException;
import util.InputValidator;
import util.TerminalManager;
import service.ReportService;
import dao.ReportDAO.ApplicantParticipationRow;

public class ReportsMenu {
	
	private Scanner sc;
	private ReportService reportService;

	public ReportsMenu(Scanner sc, ReportService reportService) {
		this.sc = sc;
		this.reportService = reportService;
	}
	
	public void run() {
		
		int choice = 0;
		
		menuLoop:
		while (true) {
			TerminalManager.clearScreen();
			System.out.println("[1] Applicant Submission Participation");
			System.out.println("[2] Applications Received by Offering");
			System.out.println("[3] Application Evaluation Summary");
			System.out.println("[4] Document Submissions by Verification Result");
			System.out.println("[5] Back");
			System.out.print("Choice: ");
			choice = InputValidator.getValidInt(sc, 1, 5);
			
			switch(choice) {
				case 1:
					applicantParticipationReport();
					break;
					
				case 2:
					break;
					
				case 3:
					break;
					
				case 4:
					break;
					
				case 5:
					break menuLoop;
				
				default:
					System.out.println("Invalid Input!");
					TerminalManager.pause(sc);
					break;
			}
		}
	}
	
	private void applicantParticipationReport(){
		int year = -1;

		TerminalManager.clearScreen();

		System.out.println("Applicant Participation Report");
		System.out.println("========================================");
		System.out.print("Enter reporting year: ");
		year = InputValidator.getValidInt(sc, 1900, 9998);

		if (year == -1){
			System.out.println("Invalid year!");
			return;
		}

		try {
			ArrayList<ApplicantParticipationRow> rows = reportService.getApplicantSubmissionParticipation(year);

			if (rows.isEmpty()) {
				System.out.println("There is no applicant participation.");
			} else {
				System.out.printf("%-14s %-30s %12s%n", "Student Number", "Full Name", "Submissions");
				System.out.println("--------------------------------------------------------------");

				for (ApplicantParticipationRow row : rows){
					System.out.printf("%-14s %-30s %12d%n", row.studno(), row.fullname(), row.totalSubmissions());
				}

				System.out.println("--------------------------------------------------------------");
				System.out.println("Total applicants: " + rows.size());
			}

		} catch (SQLException | IllegalArgumentException e) {
			System.out.println("Error generating a report: " + e.getMessage());
		}

		TerminalManager.pause(sc);
	}
}	
