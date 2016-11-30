package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class RegisterResult extends ResultBase {
    public String username;
    public String password;

    public RegisterResult(ErrorCode code) {
        super(code);
    }
    public RegisterResult(String _username, String _password){
        this.username = _username;
        this.password = _password;
    }
}
