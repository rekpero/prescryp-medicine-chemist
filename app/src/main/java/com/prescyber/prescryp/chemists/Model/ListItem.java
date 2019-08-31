package com.prescyber.prescryp.chemists.Model;

public class ListItem {
    private String PrescriptionId;
    private String Date;
    private String Status;
    private String ImagePath;

    public ListItem(String prescriptionId, String date, String status, String imagePath) {
        this.PrescriptionId = prescriptionId;
        this.Date = date;
        this.Status = status;
        this.ImagePath = imagePath;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getPrescriptionId() {
        return PrescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        PrescriptionId = prescriptionId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
