package util;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;

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
	
	public static BigDecimal getValidEntryavg(Scanner sc) {
		System.out.print("Please enter entryavg (0-100): ");
		String input = sc.nextLine();
		
		try {
			BigDecimal avg = new BigDecimal(input);
			
			if(avg.compareTo(BigDecimal.ZERO) < 0 ||
			   avg.compareTo(new BigDecimal("100")) > 0) {
				return null;
			}
			
			return avg;
		}
		
		catch(NumberFormatException e) {
			return null;
		}
	}
	
	public static BigDecimal getValidIncome(Scanner sc) {
		System.out.print("Please enter income: ");
		String input = sc.nextLine();
		
		try {
			BigDecimal income = new BigDecimal(input);
			
			if(income.compareTo(BigDecimal.ZERO) < 0) {
				return null;
			}
			
			return income;
		}
		
		catch(NumberFormatException e) {
			return null;
		}
	}
	
	public static boolean isValidDate(String date) {
		if(date == null) {
			return false;
		}
		
		try {
			LocalDate.parse(date.trim());
			return true;
		}
		catch(DateTimeParseException e) {
			return false;
		}
	}  
	
	public static boolean isValidBigDecimal(String input) {
		if(input == null) {
			return false;
		}
		
		BigDecimal deci;
		
		try {
			deci = new BigDecimal(input);
			
			if (deci.compareTo(BigDecimal.ZERO) < 0) {
				return false;
			}
			
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}
}
