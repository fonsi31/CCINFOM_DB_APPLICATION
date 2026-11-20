package service;
import dao.*;
import java.util.Scanner;
import model.*;
import java.sql.*;

public class ProgramService {
	private ProgramDAO programDAO;
	
	public ProgramService(ProgramDAO programDAO) {
		this.programDAO = programDAO;
	}
	
	public ProgramModel getProgByProgCode(String progcode) throws SQLException{
		ProgramModel prog =  programDAO.retrieveProg(progcode);
		return prog;
	}
}
