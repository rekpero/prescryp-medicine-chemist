package com.prescyber.prescryp.chemists.Model;

public class User {
    private String name;
    private String mobileNumber;
    private String password;
    private String storeName;
    private String storeContact;
    private String storeAddress;
    private String storeGSTIN;
    private String addressLat;
    private String addressLong;

    public User(String name, String mobileNumber, String password, String storeName, String storeContact, String storeAddress, String storeGSTIN, String addressLat, String addressLong) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.storeName = storeName;
        this.storeContact = storeContact;
        this.storeAddress = storeAddress;
        this.storeGSTIN = storeGSTIN;
        this.addressLat = addressLat;
        this.addressLong = addressLong;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreContact() {
        return storeContact;
    }

    public void setStoreContact(String storeContact) {
        this.storeContact = storeContact;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStoreGSTIN() {
        return storeGSTIN;
    }

    public void setStoreGSTIN(String storeGSTIN) {
        this.storeGSTIN = storeGSTIN;
    }

    public String getAddressLat() {
        return addressLat;
    }

    public void setAddressLat(String addressLat) {
        this.addressLat = addressLat;
    }

    public String getAddressLong() {
        return addressLong;
    }

    public void setAddressLong(String addressLong) {
        this.addressLong = addressLong;
    }
}
