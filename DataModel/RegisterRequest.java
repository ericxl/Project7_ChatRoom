package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class RegisterRequest extends RequestBase {
    public String username;
    public String password;
    public String displayName;
    public String email;

    public RegisterRequest(){}
    public RegisterRequest(String _username, String _password, String _displayName, String _email){
        this.username = _username;
        this.password = _password;
        this.displayName = _displayName;
        this.email = _email;
    }

}
