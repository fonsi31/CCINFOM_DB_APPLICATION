package ui;

import service.*;
import java.util.Scanner;
import util.*;

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
		String identifier;
		
		//only used when the user selects 9 or 10
		String from = "";
		String to = "";
		
		while(true) {
			TerminalManager.clearScreen();
			this.display();
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
				
				if(!InputValidator.isValidDate(from) || !InputValidator.isValidDate(to)) {
					System.out.println("Invalid Date Inputs!");
					TerminalManager.pause(sc);
					continue;
				}
			}
			
			
			
			
		}
	}
	
	public void display() {
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
}
