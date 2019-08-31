package com.prescyber.prescryp.chemists.Model;

public class RejectMedicineItem {
    private String medicineName;
    private String medicineContains;
    private boolean isSelected;

    public RejectMedicineItem(String medicineName, String medicineContains, boolean isSelected) {
        this.medicineName = medicineName;
        this.medicineContains = medicineContains;
        this.isSelected = isSelected;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineContains() {
        return medicineContains;
    }

    public void setMedicineContains(String medicineContains) {
        this.medicineContains = medicineContains;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
