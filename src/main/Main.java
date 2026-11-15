package main;
import ui.*;
import service.*;
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
		
		
		//services
		
		
		//menus/uis
		MainMenu mainMenu = new MainMenu(sc);
		mainMenu.start();
	}

}
