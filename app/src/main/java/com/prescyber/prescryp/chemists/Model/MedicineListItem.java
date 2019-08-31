package com.prescyber.prescryp.chemists.Model;

public class MedicineListItem {
    private String medicineName;
    private String medicineStrength;
    private String medicineDose;
    private String medicineDuration;

    public MedicineListItem(String medicineName, String medicineStrength, String medicineDose, String medicineDuration) {
        this.medicineName = medicineName;
        this.medicineStrength = medicineStrength;
        this.medicineDose = medicineDose;
        this.medicineDuration = medicineDuration;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineStrength() {
        return medicineStrength;
    }

    public void setMedicineStrength(String medicineStrength) {
        this.medicineStrength = medicineStrength;
    }

    public String getMedicineDose() {
        return medicineDose;
    }

    public void setMedicineDose(String medicineDose) {
        this.medicineDose = medicineDose;
    }

    public String getMedicineDuration() {
        return medicineDuration;
    }

    public void setMedicineDuration(String medicineDuration) {
        this.medicineDuration = medicineDuration;
    }
}
