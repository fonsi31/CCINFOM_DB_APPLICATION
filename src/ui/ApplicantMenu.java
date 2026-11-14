package ui;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ApplicantMenu { 
	private Scanner sc;
	
	public ApplicantMenu(Scanner sc){
		this.sc = sc;
	}
	
	public void start() {
        boolean running = true;
		int choice = -1;

        while (running) {
            manageApplicantsMenu();
			choice = -1;
            System.out.print("Enter choice: ");

			try {
				choice = sc.nextInt();
			} catch (InputMismatchException e){
				sc.nextLine();
				choice = -1;
			}
			
			if (choice == 1){
				System.out.println("Registering Applicant...");
			} else if (choice == 2){
				System.out.println("Searching Applicant...");
			} else if (choice == 3){
				System.out.println("Editing Applicant...");						
			} else if (choice == 4){
				System.out.println("Deleting Applicant...");
			} else if (choice == 0){
				System.out.println("Exiting...");
				running = false;
			} else {
				System.out.println("Invalid input.");
			}
            System.out.println();
		}
	}
	
	private void manageApplicantsMenu(){
            System.out.println("Manage Applicants: ");
            System.out.println("[1] Register Applicant");
            System.out.println("[2] Search Applicants");
            System.out.println("[3] Edit Applicant");
            System.out.println("[4] Delete Applicant");
            System.out.println("[0] Back to Main Menu");	
	}
}