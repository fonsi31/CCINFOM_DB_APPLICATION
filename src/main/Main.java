package main;
import ui.*;
import service.*;
import dao.*;
import util.*;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		//Scanner
		Scanner sc = new Scanner(System.in);
		
		//database connectivity instantiation (see util.DBConnection)
		String url = "jdbc:mysql://localhost:3306/g08_scholapp";
		String user = "root";
		String pw = "mightyoten2006";
		DBConnection dbconn = new DBConnection(url, user, pw);
		
		//daos
		ApplicationDAO applicationDAO = new ApplicationDAO(dbconn);
		ProgramDAO programDAO = new ProgramDAO(dbconn);
		
		//services
		ApplicantService applicantService = new ApplicantService();
		OfferingService offeringService = new OfferingService();
		ProgramService programService = new ProgramService(programDAO, sc);
		ApplicationService applicationService = new ApplicationService(applicationDAO, sc, programService);
		
		//menus/uis (built innermost first, then passed to the menus that open them)
		OfferingMenu offeringMenu = new OfferingMenu(sc);
		RenewalCheckMenu renewalCheckMenu = new RenewalCheckMenu(sc);
		ProgramMenu programMenu = new ProgramMenu(sc, offeringMenu, renewalCheckMenu);
		
		CreateApplicationMenu createApplicationMenu = new CreateApplicationMenu(sc, applicantService,
				offeringService, applicationService);
		ApplicationSearchMenu applicationSearchMenu = new ApplicationSearchMenu();
		ApplicationMenu applicationMenu = new ApplicationMenu(sc, createApplicationMenu, applicationSearchMenu);
		
		ApplicantMenu applicantMenu = new ApplicantMenu(sc);
		DocumentsMenu documentsMenu = new DocumentsMenu(sc);
		ReportsMenu reportsMenu = new ReportsMenu(sc);
		
		MainMenu mainMenu = new MainMenu(sc, applicantMenu, programMenu,
				applicationMenu, documentsMenu, reportsMenu);
		mainMenu.start();
	}

}
