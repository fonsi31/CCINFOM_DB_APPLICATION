
package service;

import dao.ReportDAO;
import dao.ReportDAO.ApplicantParticipationRow;
import dao.ReportDAO.OfferingApplicationsRow;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReportService {

    private ReportDAO reportDAO;

    public ReportService(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    public ArrayList<ApplicantParticipationRow> getApplicantSubmissionParticipation(int year) throws SQLException {

        if (year < 1900 || year > 9998) {
            throw new IllegalArgumentException("Invalid reporting year.");
        }

        return reportDAO.getApplicantSubmissionParticipation(year);
    }
	
	public ArrayList<OfferingApplicationsRow> getApplicationsReceivedByOffering(int year) throws SQLException {

		if (year < 1900 || year > 9998) {
			throw new IllegalArgumentException("Invalid reporting year.");
		}

		return reportDAO.getApplicationsReceivedByOffering(year);
	}		
}
