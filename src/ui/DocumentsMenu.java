package ui;
import java.util.Scanner;
import util.InputValidator;
import util.TerminalManager;


public class DocumentsMenu {
	
	private MainMenu parentMenu;
	private Scanner sc;
	
	public DocumentsMenu(MainMenu parentMenu, Scanner sc) {
		this.parentMenu = parentMenu;
		this.sc = sc;
	}
	
	public void run() {
		int appNo;
		
		System.out.println("============= Select an Application to Manage Documents for ============");
		ApplicationMenu.displayAllApplications();
		System.out.print("Enter Application Number: ");
	}
	
}
