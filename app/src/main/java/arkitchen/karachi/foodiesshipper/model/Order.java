package arkitchen.karachi.foodiesshipper.model;

/**
 * Created by hp on 2/7/2018.
 */

public class Order {
    public String id;
    public String discount;
    public String quantity;
    public String name;
    public String price;
    public Integer rowId;

    public Order() {
    }


    public Order(String id, String name, String discount, String price, String quantity, Integer rowId) {
        this.id = id;
        this.discount = discount;
        this.quantity = quantity;
        this.name = name;
        this.price = price;
        this.rowId = rowId;
    }
    public Order(String id, String name, String discount, String price, String quantity) {
        this.id = id;
        this.discount = discount;
        this.quantity = quantity;
        this.name = name;
        this.price = price;
    }

    public Order(String name, String discount, String s, String quantity) {
        this.name = name;
        this.discount = discount;
        this.quantity = quantity;
        this.price = s;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
