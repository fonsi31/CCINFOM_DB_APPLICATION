package ui;
import java.util.Scanner;
import service.*;
import model.*;
import util.*;
import java.sql.*;

public class CreateApplicationMenu {
	
	private ApplicationMenu parentMenu;
	private Scanner sc;
	private ApplicantService applicantService;
	private OfferingService offeringService;
	private ApplicationService applicationService;
	
	public CreateApplicationMenu(ApplicationMenu parentMenu, Scanner sc, ApplicantService applicantService,
			OfferingService offeringService, ApplicationService applicationService) {
		this.parentMenu = parentMenu;
		this.sc = sc;
		this.applicantService = applicantService;
		this.offeringService = offeringService;
		this.applicationService = applicationService;
	}
	
	public void run() {
		
		String applicantno;
		String offerno;
		
		while (true) {
			TerminalManager.clearScreen();
			System.out.println("=========== Applicant Selection ============");
			ApplicantMenu.displayApplicants();
			System.out.print("Please enter applicant no (0 to go back): ");
			applicantno = sc.nextLine();
			
			if (applicantno.equals("0")) {
				break;
			}
			
			ApplicantModel applicant = this.applicantService.findByStudno(applicantno);
			
			if(applicant == null) {
				System.out.println("No applicant found!");
				TerminalManager.pause(sc);
				continue;
			}
			
			System.out.println("Select an offering to apply on (0 to go back): ");
			ApplicationMenu.displayOfferings();
			System.out.print("Please enter offer no: ");
			offerno = sc.nextLine();
			
			if (offerno.equals("0")) {
				break;
			}
			
			OfferingModel offering = this.offeringService.getOfferingByOfferno(offerno);
			
			if(offering == null) {
				System.out.println("No offering found!");
				TerminalManager.pause(sc);
				continue;
			}
			
			try {
				this.applicationService.createApplication(applicant, offering);
			}
			catch(IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
			catch(SQLException e) {
				System.out.println("Database error " + e.getMessage());
			}
			
		}
	}
}