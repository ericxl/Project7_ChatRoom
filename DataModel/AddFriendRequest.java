package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class AddFriendRequest extends RequestBase {
    public String username;

    public AddFriendRequest (String _username) {
        this.username = _username;
    }
}
