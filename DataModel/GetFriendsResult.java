package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class GetFriendsResult extends ResultBase {
    public String[] friends;

    public GetFriendsResult(ErrorCode code){
        super(code);
    }
    public GetFriendsResult(String[] friends){
        this.friends = friends;
    }
}
