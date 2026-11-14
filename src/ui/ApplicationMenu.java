package ui;
import java.util.Scanner;
import util.TerminalManager;
import util.InputValidator;

public class ApplicationMenu {
	private MainMenu parentMenu;
	private Scanner sc;
	
	public ApplicationMenu(MainMenu parentMenu, Scanner sc) {
		this.parentMenu = parentMenu;
		this.sc = sc;
	}
	
	public void run() {
		
		int choice;
		
		while (true) {
			TerminalManager.clearScreen();
			System.out.println("1. Register Applicant");
			System.out.println("2. Search Applicants");
			System.out.println("3. Edit Applicant");
			System.out.println("4. Delete Applicant");
			System.out.println("5. Back");
			
			System.out.print("Enter choice: ");
			choice = InputValidator.getValidInt(sc, 5);
			
			switch(choice) {
				case 1:
					break;
					
				case 2:
					break;
					
				case 3:
					break;
					
				case 4:
					break;
					
				case 5:
					break;
					
				default:
					System.out.println("Invalid Input!");
					TerminalManager.pause(sc);
			}
					
		}
	}
}
