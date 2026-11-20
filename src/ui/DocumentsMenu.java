package ui;
import java.util.Scanner;
import util.InputValidator;
import util.TerminalManager;
import service.ApplicationService;

public class DocumentsMenu {
	
	private Scanner sc;
	
	public DocumentsMenu(Scanner sc) {
		this.sc = sc;
	}
	
	public void run() {
		int appNo;
		
		System.out.println("============= Select an Application to Manage Documents for ============");
		ApplicationMenu.displayApplications();
		System.out.print("Enter Application Number: ");
	}
	
}
