
package dao;

import util.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import dao.ReportDAO.ApplicantParticipationRow;

public class ReportDAO {

    private DBConnection dbConn;

    public ReportDAO(DBConnection dbConn) {
        this.dbConn = dbConn;
    }

    public record ApplicantParticipationRow(String studno, String fullname, long totalSubmissions) {

    }

    public ArrayList<ApplicantParticipationRow> getApplicantSubmissionParticipation(int year) throws SQLException {

        String query = """
            SELECT a.studno, a.fullname,
                COUNT(ap.appno) AS total_submissions
            FROM applicant a
            LEFT JOIN application ap
                ON a.studno = ap.studno
                AND ap.submitdt >= ?
                AND ap.submitdt < ?
            GROUP BY a.studno, a.fullname
            ORDER BY total_submissions DESC, a.studno
            """;

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = start.plusYears(1);
        ArrayList<ApplicantParticipationRow> rows = new ArrayList<>();

        try (Connection conn = dbConn.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDate(1, Date.valueOf(start));
            pstmt.setDate(2, Date.valueOf(end));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(new ApplicantParticipationRow(
                        rs.getString("studno"),
                        rs.getString("fullname"),
                        rs.getLong("total_submissions")
                    ));
                }
            }
        }

        return rows;
    }
}
