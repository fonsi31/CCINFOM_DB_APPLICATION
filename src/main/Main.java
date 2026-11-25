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
		OfferingDAO offeringDAO = new OfferingDAO(dbconn);
		ApplicantDAO applicantDAO = new ApplicantDAO(dbconn);
		
		//services
		ApplicantService applicantService = new ApplicantService(applicantDAO);
		OfferingService offeringService = new OfferingService(offeringDAO);
		ProgramService programService = new ProgramService(programDAO);
		ApplicationService applicationService = new ApplicationService(applicationDAO, sc, programService,
				offeringService, applicantService);
		
		//menus/uis (built innermost first, then passed to the menus that open them)
		OfferingMenu offeringMenu = new OfferingMenu(sc);
		RenewalCheckMenu renewalCheckMenu = new RenewalCheckMenu(sc);
		ProgramMenu programMenu = new ProgramMenu(sc, offeringMenu, renewalCheckMenu);
		
		CreateApplicationMenu createApplicationMenu = new CreateApplicationMenu(sc, applicantService,
				offeringService, applicationService);
		ApplicationSearchMenu applicationSearchMenu = new ApplicationSearchMenu(applicationService, sc);
		EditApplicationMenu editApplicationMenu = new EditApplicationMenu(applicationService, sc);
		ApplicationMenu applicationMenu = new ApplicationMenu(sc, createApplicationMenu, applicationSearchMenu,
				editApplicationMenu);
		
		ApplicantMenu applicantMenu = new ApplicantMenu(sc);
		DocumentsMenu documentsMenu = new DocumentsMenu(sc, applicationService);
		ReportsMenu reportsMenu = new ReportsMenu(sc);
		
		MainMenu mainMenu = new MainMenu(sc, applicantMenu, programMenu,
				applicationMenu, documentsMenu, reportsMenu);
		mainMenu.start();
	}

}
