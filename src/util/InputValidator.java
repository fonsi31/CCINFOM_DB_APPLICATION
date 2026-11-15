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
	 * @param lb the minimum allowable integer input
	 * @param ub the maximum allowable integer input
	 * @return the parsed integer if valid, -1 otherwise
	 */
	public static int getValidInt(Scanner sc, int lb, int ub) {
		String choice = sc.nextLine();
		int num;
		
		try{
			num = Integer.parseInt(choice);
			
			if(num < lb || num > ub) {
				return -1;
			}
			
			return num;
		}
		catch(NumberFormatException e) {
			return -1;
		}
	}
}
