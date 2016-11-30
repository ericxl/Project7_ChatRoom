package assignment7.DataModel;

/**
 * Created by Eric on 11/30/16.
 */
public class ChangePasswordResult extends ResultBase{
    public String username;
    public ChangePasswordResult(ErrorCode code){
        super(code);
    }
    public ChangePasswordResult(String _username){
        this.username = _username;
    }
}
