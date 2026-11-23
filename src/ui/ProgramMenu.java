package ui;

import java.util.InputMismatchException;
import java.util.Scanner;
import util.TerminalManager;
import service.ProgramService;
import java.util.ArrayList;
import java.sql.SQLException;
import model.ProgramModel;

public class ProgramMenu { 
	private Scanner sc;
	private OfferingMenu offeringMenu;
	private RenewalCheckMenu renewalCheckMenu;
	private ProgramService programService;
	
	public ProgramMenu(Scanner sc, OfferingMenu offeringMenu, RenewalCheckMenu renewalCheckMenu, ProgramService programService) {
		this.sc = sc;
		this.offeringMenu = offeringMenu;
		this.renewalCheckMenu = renewalCheckMenu;
		this.programService = programService;
	}
	
	public void start() {
        boolean running = true;
		int choice = -1;

        while (running) {
            manageProgramMenu();
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
				createProgramScreen();
			} else if (choice == 2){
				searchProgramScreen();
			} else if (choice == 3){
				System.out.println("Editing Program...");						
			} else if (choice == 4){
				System.out.println("Deleting Program...");
			} else if (choice == 5){
				selectProgramMenu();
			} else if (choice == 0){
				System.out.println("Returning...");
				running = false;
			} else {
				System.out.println("Invalid input.");
			}
            System.out.println();
		}
	}
	
	private void selectProgramMenu(){
		boolean selecting = true;
		int selectMenuChoice = -1;

		while (selecting) {
			selectingMenuDecision();
			selectMenuChoice = -1;
			System.out.print("Enter choice: ");

			try {
				selectMenuChoice = sc.nextInt();
				sc.nextLine();
			} catch (InputMismatchException e) {
				sc.nextLine();
				selectMenuChoice = -1;
			}

			if (selectMenuChoice == 1){
				offeringMenu.start();
			} else if (selectMenuChoice == 2){
				renewalCheckMenu.start();
			} else if (selectMenuChoice == 0){
				selecting = false;
			} else {
				System.out.println("Invalid input.");
			}
		}
	}	
	
	public void manageProgramMenu(){
			TerminalManager.clearScreen();
            System.out.println("Manage Programs: ");
            System.out.println("[1] Create Program");
            System.out.println("[2] Search Programs");
            System.out.println("[3] Edit Program");
            System.out.println("[4] Delete Program");
            System.out.println("[5] Select Program");	
            System.out.println("[0] Return to Main Menu");	
	}
	
	private void selectingMenuDecision(){
			TerminalManager.clearScreen();		
            System.out.println("Manage Programs: ");
            System.out.println("[1] Manage Offerings");
            System.out.println("[2] Manage Renewal Checks");		
            System.out.println("[0] Return");		
	}
	
	private void createProgramScreen() {	
		String minAvg = "";
		String incomeCeiling = "";
		String minUnits = "";
		String maxGPA = "";
		String progcode = "";
		String progname = "";
		String agencode = "";
		String agenname = "";
		String progtype = "";
	
		TerminalManager.clearScreen();
		
        System.out.println("==========================================");
        System.out.println("              CREATE PROGRAM              ");
        System.out.println("==========================================");

		System.out.print("Program Code: ");
		progcode = sc.nextLine();

		System.out.print("Program Name: ");
		progname = sc.nextLine();

		System.out.print("Agency Code: ");
		agencode = sc.nextLine();

		System.out.print("Agency Name: ");
		agenname = sc.nextLine();
		
		while (true) {
			System.out.println("Select the program type: ");
			System.out.println(" - First Award");
			System.out.println(" - Renewal");
			System.out.print("Program Type: ");
			progtype = sc.nextLine();

			if (progtype.equalsIgnoreCase("First Award")) {
				System.out.print("Minimum Entry Average: ");
				minAvg = sc.nextLine();

				System.out.print("Annual Income Ceiling: ");
				incomeCeiling = sc.nextLine();
				break;

			} else if (progtype.equalsIgnoreCase("Renewal")) {
				System.out.print("Minimum Completed Units: ");
				minUnits = sc.nextLine();

				System.out.print("Maximum Acceptable GPA: ");
				maxGPA = sc.nextLine();
				break;

			} else {
				System.out.println("Invalid program type. Please try again.\n");
			}
		}
	}
	
	public static void displayPrograms() {
		//use service to retrieve applicants
	}
	
	private void searchProgramScreen() {
		boolean searching = true;
		int choice = -1;

		while (searching) {
			TerminalManager.clearScreen();

			System.out.println("==========================================");
			System.out.println("              SEARCH PROGRAM              ");
			System.out.println("==========================================");
			System.out.println("[1] Search by Program Code");
			System.out.println("[2] Search by Program Name");
			System.out.println("[3] Search by Agency Code");
			System.out.println("[4] Search by Agency Name");
			System.out.println("[5] Search by Program Type");
			System.out.println("[6] Search by Active Status");
			System.out.println("[0] Return");
			System.out.print("Enter choice: ");

			choice = -1;

			try {
				choice = sc.nextInt();
				sc.nextLine();
			} catch (InputMismatchException e) {
				sc.nextLine();
			}

			if (choice == 0) {
				searching = false;
			} else if (choice >= 1 && choice <= 6) {
				System.out.print("Enter search value: ");
				String identifier = sc.nextLine();

				try {
					ArrayList<ProgramModel> programs = programService.getProgramsBy(choice, identifier);

					if (programs.isEmpty()) {
						System.out.println("No programs found.");
					} else {
						for (ProgramModel program : programs) {
							System.out.println("------------------------------------------");
							System.out.println("Program Code: " + program.getProgcode());
							System.out.println("Program Name: " + program.getProgname());
							System.out.println("Agency Code: " + program.getAgencode());
							System.out.println("Program Type: " + program.getProgtype().toDatabaseValue());
							System.out.println("Active: " + program.isActive());
						}
					}

				} catch (SQLException | IllegalArgumentException e) {
					System.out.println("Error: " + e.getMessage());
				}

				System.out.println();
				System.out.print("Press Enter to continue...");
				sc.nextLine();

			} else {
				System.out.println("Invalid input.");
			}
		}
	}	
}