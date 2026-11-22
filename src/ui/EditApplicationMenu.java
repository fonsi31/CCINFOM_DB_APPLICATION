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
			
			if(appno.equals("0")) {
				break;
			}
			
			this.fieldSelectionAndNewValEntry(appno);
		}
	}
	
	public void fieldSelectionAndNewValEntry(String appno) {
		int field;
		while(true) {
			System.out.println("[1] submit date");
			System.out.println("[2] entryavg" );
			System.out.println("[3] income");
			System.out.println("[4] score");
			System.out.println("[5] evaldate");
			System.out.println("[6] evalby");
			System.out.println("[7] status");
			System.out.println("[8] decdate");
			System.out.println("[9] remarks");
			System.out.println("[10] back");
			
			System.out.println();
			
			System.out.print("Enter field: ");
			field = InputValidator.getValidInt(sc, 1, 10);
			
			if(field == -1) {
				System.out.println("Invalid Input");
				TerminalManager.pause(sc);
				continue;
			}
			
			if(field == 10) {
				break;
			}
			
			String newVal;
			
			System.out.print("Set new value: ");
			newVal = this.sc.nextLine();
			
			try {
				this.applicationService.editApplication(appno, field, newVal);
			}
			catch(IllegalArgumentException e) {
				e.printStackTrace();
				TerminalManager.pause(sc);
			}
			catch(SQLException e) {
				e.printStackTrace();
				TerminalManager.pause(sc);
			}
			
			System.out.println("Changes Saved!");
			TerminalManager.pause(sc);
		}	
	}
}
