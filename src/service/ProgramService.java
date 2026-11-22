package service;
import dao.*;
import enums.ProgramType;
import model.*;
import java.sql.*;
import java.util.ArrayList;

public class ProgramService {
	private ProgramDAO programDAO;
	
	public ProgramService(ProgramDAO programDAO) {
		this.programDAO = programDAO;
	}
	
	public ProgramModel getProgByProgCode(String progcode) throws SQLException{
		ProgramModel prog =  programDAO.retrieveProg(progcode);
		return prog;
	}
	
	public ArrayList<ProgramModel> getAllPrograms() throws SQLException{
		return programDAO.retrieveAllPrograms();
	}
	
	public ArrayList<ProgramModel> getProgramsBy(int mode, String identifier) throws SQLException{
		
		if(identifier == null || identifier.trim().isEmpty()) {
			throw new IllegalArgumentException("Search value cannot be empty");
		}
		
		identifier = identifier.trim();
		String query;
		
		switch(mode) {
		
		case 1: //by progcode
			query = ProgramDAO.BASE_SELECT + """
					WHERE p.progcode = ?
					ORDER BY p.progcode
					""";
			break;
			
		case 2: //by progname (partial match)
			query = ProgramDAO.BASE_SELECT + """
					WHERE p.progname LIKE ?
					ORDER BY p.progname
					""";
			identifier = "%" + identifier + "%";
			break;
			
		case 3: //by agencode
			query = ProgramDAO.BASE_SELECT + """
					WHERE p.agencode = ?
					ORDER BY p.progcode
					""";
			break;
			
		case 4: //by agency name (partial match)
			query = ProgramDAO.BASE_SELECT + """
					WHERE a.agenname LIKE ?
					ORDER BY p.progcode
					""";
			identifier = "%" + identifier + "%";
			break;
			
		case 5: //by progtype (accepts "First Award", "FirstAward", "Renewal")
			//fromDatabaseValue throws IllegalArgumentException for an unknown type
			ProgramType progtype = ProgramType.fromDatabaseValue(identifier.replace(" ", ""));
			identifier = progtype.toDatabaseValue();
			
			query = ProgramDAO.BASE_SELECT + """
					WHERE p.progtype = ?
					ORDER BY p.progcode
					""";
			break;
			
		case 6: //by active status (1 = active, 0 = inactive)
			if(!identifier.equals("1") && !identifier.equals("0")) {
				throw new IllegalArgumentException("Active status must be 1 or 0");
			}
			
			query = ProgramDAO.BASE_SELECT + """
					WHERE p.active = ?
					ORDER BY p.progcode
					""";
			break;
			
		default:
			throw new IllegalArgumentException("Invalid Search mode!");
		}
		
		return this.programDAO.getProgramsBy(query, identifier);
	}
}
