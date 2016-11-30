package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class LoginRequest extends RequestBase {
    public String username;
    public String password;

    public LoginRequest(String _username, String _password){
        this.username=_username;
        this.password=_password;
    }
}
