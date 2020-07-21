package arkitchen.karachi.foodiesshipper.model;

/**
 * Created by hp on 3/7/2018.
 */

public class Token {
    public String token;
    public boolean isServerToken;

    public Token() {
    }

    public Token(String token, boolean isServerToken) {
        this.token = token;
        this.isServerToken = isServerToken;
    }

}
