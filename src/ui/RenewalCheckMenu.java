package ui;

import java.util.InputMismatchException;
import java.util.Scanner;
import util.TerminalManager;

public class RenewalCheckMenu { 
	private Scanner sc;
	
	public RenewalCheckMenu(Scanner sc){
		this.sc = sc;
	}
	
	public void start() {
        boolean running = true;
		int choice = -1;

        while (running) {
            manageRenewalsMenu();
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
				System.out.println("Entering Renewal Check...");
			} else if (choice == 2){
				System.out.println("Searching Renewal Checks...");
			} else if (choice == 3){
				System.out.println("Editing Renewal Check...");
			} else if (choice == 4){
				System.out.println("Deleting Renewal Check...");
			} else if (choice == 0){
				System.out.println("Exiting...");
				running = false;
			} else {
				System.out.println("Invalid input.");
			}
            System.out.println();
		}
	}
	
	public void manageRenewalsMenu(){
			TerminalManager.clearScreen();
            System.out.println("Manage Renewal Checks: ");
            System.out.println("[1] Enter Renewal Check");
            System.out.println("[2] Search Renewal Checks");
            System.out.println("[3] Edit Renewal Check");
            System.out.println("[4] Delete Renewal Check");
            System.out.println("[0] Return");	
	}
	
	public static void displayApplicants() {
		//use service to retrieve applicants
	}
}