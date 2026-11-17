package model;
import enums.*;
import java.time.LocalDate;
import enums.*;

public class OfferingModel {

    private final String offerno;
    private final String progcode;
    private String title;
    private LocalDate opendt;
    private LocalDate closedt;
    private LocalDate coverstart;
    private LocalDate coverend;
    private int slots;
    private OfferingStatus status;
    private String remarks;
    private int revno;

    // Constructor for creation
    public OfferingModel(
            String offerno,
            String progcode,
            String title,
            LocalDate opendt,
            LocalDate closedt,
            LocalDate coverstart,
            LocalDate coverend,
            int slots) {

        this.offerno = offerno;
        this.progcode = progcode;
        this.title = title;
        this.opendt = opendt;
        this.closedt = closedt;
        this.coverstart = coverstart;
        this.coverend = coverend;
        
        if(slots < 0) {
        	this.slots = 0;
        }
        else {
        	this.slots = slots;
        }
    }
    
    //constructor for retrieval
    public OfferingModel(
    		String offerno,
            String progcode,
            String title,
            LocalDate opendt,
            LocalDate closedt,
            LocalDate coverstart,
            LocalDate coverend,
            int slots,
            OfferingStatus status,
            String remarks,
            int revno) {
    	
    	this.offerno = offerno;
        this.progcode = progcode;
        this.title = title;
        this.opendt = opendt;
        this.closedt = closedt;
        this.coverstart = coverstart;
        this.coverend = coverend;
        this.slots = slots;
        this.status = status;
        this.remarks = remarks;
        this.revno = revno;
    }

    // Getters
    public String getOfferno() {
        return offerno;
    }

    public String getProgcode() {
        return progcode;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getOpendt() {
        return opendt;
    }

    public LocalDate getClosedt() {
        return closedt;
    }

    public LocalDate getCoverstart() {
        return coverstart;
    }

    public LocalDate getCoverend() {
        return coverend;
    }

    public int getSlots() {
        return slots;
    }

    public OfferingStatus getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    public int getRevno() {
        return revno;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setOpendt(LocalDate opendt) {
        this.opendt = opendt;
    }

    public void setClosedt(LocalDate closedt) {
        this.closedt = closedt;
    }

    public void setCoverstart(LocalDate coverstart) {
        this.coverstart = coverstart;
    }

    public void setCoverend(LocalDate coverend) {
        this.coverend = coverend;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public void setStatus(OfferingStatus status) {
        this.status = status;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void updateRevno() {
        this.revno++;
    }
}
