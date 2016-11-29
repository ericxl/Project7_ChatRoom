package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class AddFriendResult extends ResultBase {
    public String friendUsername;
    public String friendDisplayName;
    public AddFriendResult (){}
    public AddFriendResult(ErrorCode code){
        super(code);
    }
    public AddFriendResult (String _friendUsername, String _friendDisplayName){
        this.friendUsername = _friendUsername;
        this.friendDisplayName = _friendDisplayName;
    }
}