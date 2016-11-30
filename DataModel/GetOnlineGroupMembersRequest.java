package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class GetOnlineGroupMembersRequest extends RequestBase {
    public String groupName;
    public GetOnlineGroupMembersRequest(){

    }
    public GetOnlineGroupMembersRequest(String groupName){
        this.groupName = groupName;
    }
}
