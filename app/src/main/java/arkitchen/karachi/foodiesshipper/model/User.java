package arkitchen.karachi.foodiesshipper.model;

public class User {
    public String Password;
    public String Name;
    public String Phone;
    public String security_code;
    public String isStaff;
    public Address home;
    public Token token;



    public User() {
    }

    public User(String password, String name, String phone, String security_code, String isStaff, Address home, Token token) {
        Password = password;
        Name = name;
        this.token = token;
        Phone = phone;
        this.security_code = security_code;
        this.isStaff = isStaff;
        this.home = home;
    }
}
