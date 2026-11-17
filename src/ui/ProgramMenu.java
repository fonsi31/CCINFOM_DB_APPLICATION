package ui;

import java.util.InputMismatchException;
import java.util.Scanner;
import util.TerminalManager;

public class ProgramMenu { 
	private Scanner sc;
	
	public ProgramMenu(Scanner sc){
		this.sc = sc;
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
				System.out.println("Searching Program...");
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
				OfferingMenu offeringMenu = new OfferingMenu(sc);
				offeringMenu.start();
			} else if (selectMenuChoice == 2){
				RenewalCheckMenu renewalCheckMenu = new RenewalCheckMenu(sc);
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
}