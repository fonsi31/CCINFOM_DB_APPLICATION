package model;

import java.math.BigDecimal;
import java.time.LocalDate;

import enums.*;

public class ApplicationModel {

    private final String appno;
    private final String studno;
    private final String offerno;
    private LocalDate submitdt;
    private BigDecimal entryavg;
    private BigDecimal income;
    private BigDecimal score;
    private LocalDate evaldate;
    private String evalby;
    private ApplicationStatus status;
    private LocalDate decdate;
    private String remarks;
    private int revno;

    // Constructor for a new application
    public ApplicationModel(
            String appno,
            String studno,
            String offerno,
            LocalDate submitdt,
            BigDecimal entryavg,
            BigDecimal income) {

        this.appno = appno;
        this.studno = studno;
        this.offerno = offerno;
        this.submitdt = submitdt;
        this.entryavg = entryavg;
        this.income = income;
        this.status = ApplicationStatus.SUBMITTED;
        this.revno = 1;
    }

    // Constructor for retrieving an existing application
    public ApplicationModel(
            String appno,
            String studno,
            String offerno,
            LocalDate submitdt,
            BigDecimal entryavg,
            BigDecimal income,
            BigDecimal score,
            LocalDate evaldate,
            String evalby,
            ApplicationStatus status,
            LocalDate decdate,
            String remarks,
            int revno) {

        this.appno = appno;
        this.studno = studno;
        this.offerno = offerno;
        this.submitdt = submitdt;
        this.entryavg = entryavg;
        this.income = income;
        this.score = score;
        this.evaldate = evaldate;
        this.evalby = evalby;
        this.status = status;
        this.decdate = decdate;
        this.remarks = remarks;
        this.revno = revno;
    }

    // Getters
    public String getAppno() {
        return appno;
    }

    public String getStudno() {
        return studno;
    }

    public String getOfferno() {
        return offerno;
    }

    public LocalDate getSubmitdt() {
        return submitdt;
    }

    public BigDecimal getEntryavg() {
        return entryavg;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public BigDecimal getScore() {
        return score;
    }

    public LocalDate getEvaldate() {
        return evaldate;
    }

    public String getEvalby() {
        return evalby;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public LocalDate getDecdate() {
        return decdate;
    }

    public String getRemarks() {
        return remarks;
    }

    public int getRevno() {
        return revno;
    }

    // Setters
    public void setSubmitdt(LocalDate submitdt) {
        this.submitdt = submitdt;
    }

    public void setEntryavg(BigDecimal entryavg) {
        this.entryavg = entryavg;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public void setEvaldate(LocalDate evaldate) {
        this.evaldate = evaldate;
    }

    public void setEvalby(String evalby) {
        this.evalby = evalby;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void setDecdate(LocalDate decdate) {
        this.decdate = decdate;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
