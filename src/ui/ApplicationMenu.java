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
		
		int choice = 0;
		
		menuLoop:
		while (choice == 0) {
			TerminalManager.clearScreen();
			System.out.println("[1] Create Application");
			System.out.println("[2] Search Applications");
			System.out.println("[3] Edit Application");
			System.out.println("[4] Delete Application");
			System.out.println("[5] Display Applications");
			System.out.println("[6] Select Application");
			System.out.println("[7] Back");
			System.out.print("Enter choice: ");
			choice = InputValidator.getValidInt(this.sc, 7);
			
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
				
				case 6:
					break;
				
				case 7:
					break menuLoop;
					
				default:
					System.out.println("Invalid Input!");
					TerminalManager.pause(sc);
					break;
			}
					
		}
	}
	
	public static void displayAllApplications() {
		//
	}
}
