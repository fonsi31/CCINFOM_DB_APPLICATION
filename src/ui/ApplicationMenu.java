package ui;
import java.util.Scanner;
import util.*;

public class ApplicationMenu {
	private Scanner sc;
	private CreateApplicationMenu createMenu;
	private ApplicationSearchMenu searchMenu;
	
	public ApplicationMenu(Scanner sc, CreateApplicationMenu createMenu, ApplicationSearchMenu searchMenu) {
		this.sc = sc;
		this.createMenu = createMenu;
		this.searchMenu = searchMenu;
	}
	
	public void run() {  
		
		int choice = 0;
		
		menuLoop:
		while (true) {
			TerminalManager.clearScreen();
			System.out.println("[1] Create Application");
			System.out.println("[2] Search Applications");
			System.out.println("[3] Edit Application");
			System.out.println("[4] Delete Application");
			System.out.println("[5] Display Applications");
			System.out.println("[6] Select Application");
			System.out.println("[7] Back");
			System.out.print("Enter choice: ");
			choice = InputValidator.getValidInt(this.sc, 1, 7);
			
			switch(choice) {
				case 1:
					this.createMenu.run();
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
	
	public static void displayApplications() {
		//use service to retrieve applications;
	}
	
	public static void displayOfferings() {
		//use service to retrieve offerings by application
	}
}
