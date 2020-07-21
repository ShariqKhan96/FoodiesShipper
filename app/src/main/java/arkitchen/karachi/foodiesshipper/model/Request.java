package arkitchen.karachi.foodiesshipper.model;

import java.util.List;

/**
 * Created by hp on 2/7/2018.
 */

public class Request {
    public String phone;
    public String name;
    public String address;
    public String total;
    public List<Order> orders;
    public String comment;
    public String status;
    public String latLng;
    public String payment_method;
    public String orderId;
    public Shipper assigned_to;
    public String delivery_time;
    public String punch_status;


    public Request() {
    }

    public Request(String punch_status, String phone, String delivery_time, Shipper assigned_to, String name, String address, String total, List<Order> orders, String status, String comment, String latLng, String payment_method, String orderId) {
        this.phone = phone;
        this.punch_status = punch_status;
        this.delivery_time = delivery_time;
        this.assigned_to = assigned_to;
        this.name = name;
        this.orderId = orderId;
        this.address = address;
        this.total = total;
        this.orders = orders;
        this.status = status;
        this.comment = comment;
        this.latLng = latLng;
        this.payment_method = payment_method;
        //0 for placed , 1:shipping, 2:shipped
    }


}
