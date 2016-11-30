package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class LoginResult extends ResultBase {
    public String username;
    public LoginResult(ErrorCode code){
        super(code);
    }
    public LoginResult(String name){
        username = name;
    }
}