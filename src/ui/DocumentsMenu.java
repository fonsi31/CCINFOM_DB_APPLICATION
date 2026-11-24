package ui;
import java.util.Scanner;

import util.InputValidator;
import util.TerminalManager;
import service.*;
import java.sql.*;

public class DocumentsMenu {
	
	
	private Scanner sc;
	private ApplicationService applicationService;
	
	public DocumentsMenu(Scanner sc, ApplicationService applicationService) {
		this.sc = sc;
		this.applicationService = applicationService;
	}
	
	public void run() {
		int appNo;
		
		while (true) {
			System.out.println("============= Select an Application to Manage Documents for ============");
			try {
				ApplicationMenu.displayApplications(this.applicationService.getAllApplications());
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			System.out.print("Enter Application Number: ");
		}
		
	}
	
}
