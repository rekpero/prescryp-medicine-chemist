package com.prescyber.prescryp.chemists.Model;

public class CartItem {
    private String MedicineName;
    private String Quantity;
    private String Price;
    private String PackageContain;
    private String ItemStatus;

    public CartItem(String medicineName, String quantity, String price, String packageContain, String itemStatus) {
        MedicineName = medicineName;
        Quantity = quantity;
        Price = price;
        PackageContain = packageContain;
        ItemStatus = itemStatus;
    }

    public String getMedicineName() {
        return MedicineName;
    }

    public void setMedicineName(String medicineName) {
        MedicineName = medicineName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getPackageContain() {
        return PackageContain;
    }

    public void setPackageContain(String packageContain) {
        PackageContain = packageContain;
    }

    public String getItemStatus() {
        return ItemStatus;
    }

    public void setItemStatus(String itemStatus) {
        ItemStatus = itemStatus;
    }
}
