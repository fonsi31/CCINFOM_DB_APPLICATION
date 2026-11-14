package util;
import java.util.Scanner;

public class TerminalManager {
	
	private TerminalManager() {
		// Prevent Instantiation
	}
	
	public static void clearScreen() {
		System.out.print("\n".repeat(50));
	}
	
	public static void pause(Scanner sc) {
		System.out.println("\nPress Enter to continue...");
		sc.nextLine();
	}
}
