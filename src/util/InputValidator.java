package util;
import java.util.Scanner;

public class InputValidator {
	
	private InputValidator(){
		// Prevent Instantiation
	}
	
	/**
	 * Gets valid integer user input and also checks if it's within the permitted range
	 * 
	 * @param sc input scanner
	 * @param ub the maximum allowable input
	 * @return the parsed integer if valid, 0 otherwise
	 */
	public static int getValidInt(Scanner sc, int ub) {
		String choice = sc.nextLine();
		int num;
		
		try{
			num = Integer.parseInt(choice);
			
			if(num < 1 || num > ub) {
				return 0;
			}
			
			return num;
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}
}
