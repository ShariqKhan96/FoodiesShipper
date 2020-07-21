package arkitchen.karachi.foodiesshipper.model;

public class Shipper {
    public String active, name, password, phone;
    public Token token;

    public Shipper() {
    }

    public Shipper(String active, String name, String password, String phone,Token token) {
        this.active = active;
        this.token = token;
        this.name = name;
        this.password = password;
        this.phone = phone;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
