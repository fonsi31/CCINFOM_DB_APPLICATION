package ui;
import java.util.Scanner;
import util.InputValidator;
import util.TerminalManager;

public class ReportsMenu {
	
	private MainMenu parentMenu;
	private Scanner sc;
	
	public ReportsMenu(MainMenu parentMenu, Scanner sc) {
		this.parentMenu = parentMenu;
		this.sc = sc;
	}
	
	public void run() {
		
		int choice = 0;
		
		menuLoop:
		while (choice == 0) {
			TerminalManager.clearScreen();
			System.out.println("[1] Applicant Submission Participation");
			System.out.println("[2] Applications Received by Offering");
			System.out.println("[3] Application Evaluation Summary");
			System.out.println("[4] Document Submissions by Verification Result");
			System.out.println("[5] Back");
			System.out.print("Choice: ");
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
					break menuLoop;
				
				default:
					System.out.println("Invalid Input!");
					TerminalManager.pause(sc);
					break;
			}
		}
	}
	
}
