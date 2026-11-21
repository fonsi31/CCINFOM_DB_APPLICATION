package ui;

import service.*;
import java.time.LocalDate;
import java.util.Scanner;
import util.*;
import model.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class ApplicationSearchMenu {
	
	private ApplicationService applicationService;
	private Scanner sc;
	
	public ApplicationSearchMenu(ApplicationService applicationService,
			Scanner sc) {
		this.applicationService = applicationService;
		this.sc = sc;
	}
	
	public void run() {
		
		int choice;
		String identifier = "";
		
		//only used when the user selects 9 or 10
		String from = "";
		String to = "";
		
		//applications that fits the search filter/key will be stored here
		ArrayList<ApplicationModel> applications = new ArrayList<>();
		
		while(true) {
			TerminalManager.clearScreen();
			this.displayChoices();
			System.out.println();
			System.out.print("Enter choice: ");
			choice = InputValidator.getValidInt(sc, 1, 11);
			
			if(choice == -1) { //-1 indicates invalid input
				System.out.println("Invalid Input!");
				TerminalManager.pause(sc);
				continue;
			}
			
			if(choice == 11) {
				break;
			}
			
			if(choice == 9 || choice == 10) {
				System.out.println("Enter date bounds");
				System.out.print("from: ");
				from = this.sc.nextLine();
				System.out.print("to: ");
				to = this.sc.nextLine();
				
				from = from.trim();
				to = to.trim();
				
				if(!InputValidator.isValidDate(from) || !InputValidator.isValidDate(to)) {
					System.out.println("Invalid Date Inputs!");
					TerminalManager.pause(sc);
					continue;
				}	
			}
			else { //choice >=1 && choice <= 10
				System.out.print("Enter search identifier: ");
				identifier = this.sc.nextLine();
				identifier = identifier.trim();
			}
			
			try {
				applications = this.applicationService.getApplicationsBy(choice, identifier, from, to);
			}
			catch(IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			
			this.displayApplications(applications);
			System.out.println();
			TerminalManager.pause(sc);
		}
	}
	
	public void displayChoices() {
		System.out.println("Search by:");
		System.out.println("[1] application ref number");
		System.out.println("[2] student number");
		System.out.println("[3] student name");
		System.out.println("[4] offering (offerno)");
		System.out.println("[5] program (progcode)");
		System.out.println("[6] agency (agencode)");
		System.out.println("[7] program type (progtype)");
		System.out.println("[8] status");
		System.out.println("[9] submission date (yyyy-mm-dd)");
		System.out.println("[10] evaluation date (yyyy-mm-dd)");
		System.out.println("[11] back");  
	}
	
	public void displayApplications(ArrayList<ApplicationModel> applications) {
		if(applications.isEmpty()) {
			System.out.print("No applications found");
			return;
		}
		
		int i = 1;
		
		for (ApplicationModel application: applications) {
			System.out.print(i + ". Application Number: " + application.getAppno());
			System.out.println("   Student Number: " + application.getStudno());
			System.out.println("   Submitted at: " + application.getSubmitdt().toString());
			System.out.println("   Status: " + application.getStatus());
			i++;
		}
	}
}
