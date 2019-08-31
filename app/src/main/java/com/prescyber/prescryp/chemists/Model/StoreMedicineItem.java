package com.prescyber.prescryp.chemists.Model;

public class StoreMedicineItem {
    private String id;
    private String medicineName;
    private String companyName;
    private String form;
    private String packaging;
    private int quantity;
    private String checkPrescription;
    private String checkAdded;

    public StoreMedicineItem(String id, String medicineName, String companyName, String form, String packaging, int quantity, String checkPrescription, String checkAdded) {
        this.id = id;
        this.medicineName = medicineName;
        this.companyName = companyName;
        this.form = form;
        this.packaging = packaging;
        this.quantity = quantity;
        this.checkPrescription = checkPrescription;
        this.checkAdded = checkAdded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCheckPrescription() {
        return checkPrescription;
    }

    public void setCheckPrescription(String checkPrescription) {
        this.checkPrescription = checkPrescription;
    }

    public String getCheckAdded() {
        return checkAdded;
    }

    public void setCheckAdded(String checkAdded) {
        this.checkAdded = checkAdded;
    }
}
