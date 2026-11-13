
package com.scholarship.ui;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MainMenu {

    private Scanner sc = new Scanner(System.in);

    public void start() {
        boolean running = true;
		int choice = -1;

        while (running) {
            displayMenu();
			choice = -1;
            System.out.print("Enter choice: ");

			try {
				choice = sc.nextInt();
			} catch (InputMismatchException e){
				sc.nextLine();
				choice = -1;
			}
			
			if (choice == 1){
				ApplicantMenu applicantMenu = new ApplicantMenu(sc);
				applicantMenu.start();								
			} else if (choice == 2){
				System.out.println("Managing Programs...");
			} else if (choice == 3){
				System.out.println("Managing Applications...");				
			} else if (choice == 4){
				System.out.println("Managing Documents...");
			} else if (choice == 5){
				System.out.println("Managing Reports...");
			} else if (choice == 0){
				System.out.println("Exiting...");
				running = false;
			} else {
				System.out.println("Invalid input.");
			}
            System.out.println();
		}
    }

    private void displayMenu() {
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
