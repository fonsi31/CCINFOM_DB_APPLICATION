package ui;

import java.util.InputMismatchException;
import java.util.Scanner;
import service.*;
import util.TerminalManager;

public class MainMenu {

    private Scanner sc;
	private ApplicantService applicantService;
	private OfferingService offeringService;
	private ApplicationService applicationService;    
	
    public MainMenu(Scanner sc, ApplicantService applicantService, OfferingService offeringService, ApplicationService applicationService) {
		this.sc = sc;
		this.applicantService = applicantService;
		this.offeringService = offeringService;
		this.applicationService = applicationService;
    }

    public void start() {
        boolean running = true;
		int choice = -1;

        while (running) {
            displayMenu();
			choice = -1;
            System.out.print("Enter choice: ");

			try {
				choice = sc.nextInt();
				sc.nextLine();
			} catch (InputMismatchException e){
				sc.nextLine();
				choice = -1;
			}
			
			if (choice == 1){
				ApplicantMenu applicantMenu = new ApplicantMenu(sc);
				applicantMenu.start();								
			} else if (choice == 2){
				ProgramMenu programMenu = new ProgramMenu(sc);
				programMenu.start();
			} else if (choice == 3){
				ApplicationMenu applicationMenu = new ApplicationMenu(this, sc, applicantService, offeringService, applicationService);
				applicationMenu.run();			
			} else if (choice == 4){
				DocumentsMenu documentsMenu = new DocumentsMenu(this, sc);
				documentsMenu.run();
			} else if (choice == 5){
				ReportsMenu reportsMenu = new ReportsMenu(this, sc);
				reportsMenu.run();
			} else if (choice == 0){
				System.out.println("Exiting...");
				running = false;
			} else {
				System.out.println("Invalid input.");
			}
            System.out.println();
		}
    }

    public void displayMenu() {
		TerminalManager.clearScreen();			
        System.out.println("==========================================");
        System.out.println("    SCHOLARSHIP MANAGEMENT SYSTEM         ");
        System.out.println("==========================================");
        System.out.println("[1] Manage Applicants");
        System.out.println("[2] Manage Programs");
        System.out.println("[3] Manage Applications and Decisions");
        System.out.println("[4] Manage Documents");
        System.out.println("[5] Reports");
        System.out.println("[0] Exit");
        System.out.println("==========================================");
    }
}
