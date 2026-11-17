package model;

import java.math.BigDecimal;
import enums.ProgramType;

public class ProgramModel {

    // Common attributes
    private final String progcode;
    private String progname;
    private String agencode;
    private final ProgramType progtype;
    private int active;
    private int revno;

    // FirstAward-specific attributes
    private BigDecimal entrymin;
    private BigDecimal incomecp;

    // Renewal-specific attributes
    private int minunits;
    private BigDecimal maxgpa;

    // FirstAward creation constructor
    public ProgramModel(String progcode, String progname,
                        String agencode, BigDecimal entrymin,
                        BigDecimal incomecp) {

        this.progcode = progcode;
        this.progname = progname;
        this.agencode = agencode;
        this.progtype = ProgramType.FIRSTAWARD;

        this.entrymin = entrymin;
        this.incomecp = incomecp;
    }

    // Renewal creation constructor
    public ProgramModel(String progcode, String progname,
                        String agencode, int minunits,
                        BigDecimal maxgpa) {

        this.progcode = progcode;
        this.progname = progname;
        this.agencode = agencode;
        this.progtype = ProgramType.RENEWAL;

        this.minunits = minunits;
        this.maxgpa = maxgpa;
    }

    // Retrieval constructor
    public ProgramModel(String progcode, String progname,
                        String agencode, ProgramType progtype,
                        BigDecimal entrymin, BigDecimal incomecp,
                        Integer minunits, BigDecimal maxgpa,
                        int active, int revno) {

        this.progcode = progcode;
        this.progname = progname;
        this.agencode = agencode;
        this.progtype = progtype;

        this.entrymin = entrymin;
        this.incomecp = incomecp;
        this.minunits = minunits;
        this.maxgpa = maxgpa;

        this.active = active;
        this.revno = revno;
    }

    // Getters
    public String getProgcode() {
        return progcode;
    }

    public String getProgname() {
        return progname;
    }

    public String getAgencode() {
        return agencode;
    }

    public ProgramType getProgtype() {
        return progtype;
    }

    public BigDecimal getEntrymin() {
        return entrymin;
    }

    public BigDecimal getIncomecp() {
        return incomecp;
    }

    public Integer getMinunits() {
        return minunits;
    }

    public BigDecimal getMaxgpa() {
        return maxgpa;
    }

    public int isActive() {
        return active;
    }

    public int getRevno() {
        return revno;
    }

    // Setters
    public void setProgname(String progname) {
        this.progname = progname;
    }

    public void setAgencode(String agencode) {
        this.agencode = agencode;
    }

    public void setEntrymin(BigDecimal entrymin) {
        this.entrymin = entrymin;
    }

    public void setIncomecp(BigDecimal incomecp) {
        this.incomecp = incomecp;
    }

    public void setMinunits(Integer minunits) {
        this.minunits = minunits;
    }

    public void setMaxgpa(BigDecimal maxgpa) {
        this.maxgpa = maxgpa;
    }

    public void setActive(int active) {
        this.active = active;
    }
    
    public void updateRevno() {
    	this.revno++;
    }
}
