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
			
			//get appno
			System.out.println();
			System.out.print("Enter appno of the application you want to edit (0 to go back): ");
			appno = this.sc.nextLine();
			
			appno = appno.trim();
			
			if(appno.equals("0")) {
				break;
			}
			
			//load the application once; its revno is what the stale-edit check compares against
			ApplicationModel application;
			try {
				application = this.applicationService.findByAppno(appno);
			}
			catch(SQLException e) {
				System.out.println("Database error " + e.getMessage());
				TerminalManager.pause(sc);
				continue;
			}
			
			if(application == null) {
				System.out.println("No application found!");
				TerminalManager.pause(sc);
				continue;
			}
			
			//only Submitted applications can be edited (the service checks this again on save)
			if(application.getStatus() != enums.ApplicationStatus.SUBMITTED) {
				System.out.println("Only Submitted applications can be edited (current status: "
						+ application.getStatus().toDatabaseValue() + ").");
				System.out.println("To correct an evaluation, reopen it first.");
				TerminalManager.pause(sc);
				continue;
			}
			
			this.fieldSelectionAndNewValEntry(application);
		}
	}
	
	public void fieldSelectionAndNewValEntry(ApplicationModel application) {
		int field;
		while(true) {
			System.out.println();
			System.out.println("Editing " + application.getAppno()
					+ " (student " + application.getStudno()
					+ ", status " + application.getStatus() + ")");
			System.out.println("[1] Submit date        (current: " + application.getSubmitdt() + ")");
			System.out.println("[2] Entry average      (current: " + application.getEntryavg() + ")  - First Award only");
			System.out.println("[3] Income             (current: " + application.getIncome() + ")  - First Award only");
			System.out.println("[4] Remarks            (current: " + application.getRemarks() + ")");
			System.out.println("[5] Back");
			
			System.out.println();
			
			System.out.print("Enter field: ");
			field = InputValidator.getValidInt(sc, 1, 5);
			
			if(field == -1) {
				System.out.println("Invalid Input");
				TerminalManager.pause(sc);
				continue;
			}
			
			if(field == 5) {
				break;
			}
			
			String newVal;
			
			if(field == 1) {
				System.out.print("New submit date (YYYY-MM-DD): ");
			}
			else if(field == 4) {
				System.out.print("New remarks (leave blank to clear): ");
			}
			else {
				System.out.print("New value: ");
			}
			newVal = this.sc.nextLine();
			
			try {
				this.applicationService.editApplication(application, field, newVal);
			}
			catch(IllegalArgumentException | IllegalStateException e) {
				System.out.println(e.getMessage());
				TerminalManager.pause(sc);
				continue;
			}
			catch(SQLException e) {
				System.out.println("Database error " + e.getMessage());
				TerminalManager.pause(sc);
				continue;
			}
			
			System.out.println("Changes Saved!");
			TerminalManager.pause(sc);
			
			//reload after our own successful save, so the next edit uses the new revno
			try {
				application = this.applicationService.findByAppno(application.getAppno());
			}
			catch(SQLException e) {
				System.out.println("Database error " + e.getMessage());
				TerminalManager.pause(sc);
				break;
			}
			
			if(application == null) {
				System.out.println("The application no longer exists.");
				TerminalManager.pause(sc);
				break;
			}
		}	
	}
}
