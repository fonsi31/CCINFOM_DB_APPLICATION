package ui;

import java.util.InputMismatchException;
import java.util.Scanner;
import util.TerminalManager;

public class OfferingMenu { 
	private Scanner sc;
	
	public OfferingMenu(Scanner sc){
		this.sc = sc;
	}
	
	public void start() {
        boolean running = true;
		int choice = -1;

        while (running) {
            manageOfferingsMenu();
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
				System.out.println("Creating Offering...");
			} else if (choice == 2){
				System.out.println("Searching Offering...");
			} else if (choice == 3){
				System.out.println("Editing Offering...");						
			} else if (choice == 4){
				System.out.println("Deleting Offering...");
			} else if (choice == 0){
				System.out.println("Exiting...");
				running = false;
			} else {
				System.out.println("Invalid input.");
			}
            System.out.println();
		}
	}
	
	public void manageOfferingsMenu(){
			TerminalManager.clearScreen();
            System.out.println("Manage Offerings: ");
            System.out.println("[1] Create Offering");
            System.out.println("[2] Search Offerings");
            System.out.println("[3] Edit Offerings");
            System.out.println("[4] Delete Offerings");
            System.out.println("[0] Return");	
	}
	
	public static void displayOfferings() {
		//use service to display offerings
	}
}