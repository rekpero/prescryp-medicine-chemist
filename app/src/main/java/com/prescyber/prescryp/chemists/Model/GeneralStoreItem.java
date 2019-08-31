package com.prescyber.prescryp.chemists.Model;

public class GeneralStoreItem {
    private String Id;
    private String Name;
    private String Title;
    private String Category;
    private String UpperCategory;
    private String ImageUrl;
    private int Quantity;
    private int Price;
    private String CheckAdded;

    public GeneralStoreItem(String id, String name, String title, String category, String upperCategory, String imageUrl, int quantity, int price, String checkAdded) {
        Id = id;
        Name = name;
        Title = title;
        Category = category;
        UpperCategory = upperCategory;
        ImageUrl = imageUrl;
        Quantity = quantity;
        Price = price;
        CheckAdded = checkAdded;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getUpperCategory() {
        return UpperCategory;
    }

    public void setUpperCategory(String upperCategory) {
        UpperCategory = upperCategory;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getCheckAdded() {
        return CheckAdded;
    }

    public void setCheckAdded(String checkAdded) {
        CheckAdded = checkAdded;
    }
}
