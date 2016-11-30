package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class GetOnlineGroupMembersResult extends ResultBase{
    public String[] members;
    public GetOnlineGroupMembersResult(ErrorCode code){
        super(code);
    }
    public GetOnlineGroupMembersResult(String[] members){
        this.members = members;
    }
}
