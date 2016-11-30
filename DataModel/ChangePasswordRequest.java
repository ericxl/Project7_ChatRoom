package assignment7.DataModel;

/**
 * Created by Eric on 11/30/16.
 */
public class ChangePasswordRequest extends RequestBase{
    public String oldPassword;
    public String newPassword;
    public String username;

    public ChangePasswordRequest(String username, String old, String newPassword){
        this.username = username;
        this.oldPassword = old;
        this.newPassword = newPassword;
    }
}
