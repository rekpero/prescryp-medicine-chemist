package com.prescyber.prescryp.chemists.Model;

public class DailyOrderItem {
    private String OrderNumber;
    private String DeliveryTime;

    public DailyOrderItem(String orderNumber, String deliveryTime) {
        OrderNumber = orderNumber;
        DeliveryTime = deliveryTime;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public String getDeliveryTime() {
        return DeliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        DeliveryTime = deliveryTime;
    }
}
