package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class RegisterRequest extends RequestBase {
    public String username;
    public String password;

    public RegisterRequest(){}
    public RegisterRequest(String _username, String _password){
        this.username = _username;
        this.password = _password;
    }

}
