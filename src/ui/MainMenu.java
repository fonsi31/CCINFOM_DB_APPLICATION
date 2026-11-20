package ui;

import java.util.InputMismatchException;
import java.util.Scanner;
import util.TerminalManager;

public class MainMenu {

    private Scanner sc;
	private ApplicantMenu applicantMenu;
	private ProgramMenu programMenu;
	private ApplicationMenu applicationMenu;
	private DocumentsMenu documentsMenu;
	private ReportsMenu reportsMenu;
	
    public MainMenu(Scanner sc, ApplicantMenu applicantMenu, ProgramMenu programMenu,
    		ApplicationMenu applicationMenu, DocumentsMenu documentsMenu, ReportsMenu reportsMenu) {
		this.sc = sc;
		this.applicantMenu = applicantMenu;
		this.programMenu = programMenu;
		this.applicationMenu = applicationMenu;
		this.documentsMenu = documentsMenu;
		this.reportsMenu = reportsMenu;
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
				applicantMenu.start();								
			} else if (choice == 2){
				programMenu.start();
			} else if (choice == 3){
				applicationMenu.run();			
			} else if (choice == 4){
				documentsMenu.run();
			} else if (choice == 5){
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
