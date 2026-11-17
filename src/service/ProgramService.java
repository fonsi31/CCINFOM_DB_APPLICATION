package service;
import dao.*;
import java.util.Scanner;
import model.*;
import java.sql.*;

public class ProgramService {
	private ProgramDAO programDAO;
	private Scanner sc;
	
	public ProgramService(ProgramDAO programDAO, Scanner sc) {
		this.programDAO = programDAO;
		this.sc = sc;
	}
	
	public ProgramModel getProgByProgCode(String progcode) throws SQLException{
		ProgramModel prog =  programDAO.retrieveProg(progcode);
		return prog;
	}
}
