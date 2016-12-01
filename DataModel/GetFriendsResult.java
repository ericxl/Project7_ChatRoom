package assignment7.DataModel;

import java.util.Set;

/**
 * Created by Eric on 11/29/16.
 */
public class GetFriendsResult extends ResultBase {
    public String[] friends;
    public Set<String> onlineFriends;

    public GetFriendsResult(ErrorCode code){
        super(code);
    }
    public GetFriendsResult(String[] friends){
        this.friends = friends;
    }
    
    public GetFriendsResult(String[] friends,Set<String> onlineFriends){
        this.friends = friends;
        this.onlineFriends=onlineFriends;
    }
}
