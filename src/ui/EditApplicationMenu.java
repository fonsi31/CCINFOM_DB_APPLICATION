package ui;

import service.*;
import util.*;
import model.*;
import java.util.Scanner;
import java.sql.*;

public class EditApplicationMenu {
	
	private ApplicationService applicationService;
	private Scanner sc;
	
	public EditApplicationMenu(ApplicationService applicationService, Scanner sc) {
		this.applicationService = applicationService;
		this.sc = sc;
	}
	
	public void run() {
		ApplicationModel application;
		String appno;
		
		while (true) {
			TerminalManager.clearScreen();
			try {
				ApplicationMenu.displayApplications(this.applicationService.getAllApplications());
			}
			catch(SQLException e) {
				e.printStackTrace();
				TerminalManager.pause(sc);
				break;
			}
			
			System.out.println();
			System.out.print("Enter appno of the application you want to edit (0 to go back): ");
			appno = this.sc.nextLine();
			
			if(appno.equals("0")) {
				break;
			}
			
			try {
				application = this.applicationService.getApplicationsBy(1, appno, "", "").get(0);
			}
			catch(IllegalArgumentException e) {
				e.printStackTrace();
				TerminalManager.pause(sc);
				continue;
			}
			catch(SQLException e) {
				e.printStackTrace();
				TerminalManager.pause(sc);
				break;
			}
			
			System.out.println("Choose a field to edit (0 to go back): ");
			
			/**
			 * if application is of renewal type and the user enters 4 or 5
			 * throw an invalid input
			 */
			if() {
				
			}
			
			/**
			 * Editing happens here
			 */
			
			
		}
	}
	
	public void displayOptions() {
		System.out.println("[1] submit date");
		System.out.println("[2] entryavg" );
		System.out.println("[3] income");
		System.out.println("[4] score");
		System.out.println("[5] evaldate");
		System.out.println("[6] evalby");
		System.out.println("[7] status");
		System.out.println("[8] decdate");
		System.out.println("[9] remarks");
	}
	
}
