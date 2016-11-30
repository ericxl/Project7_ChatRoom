package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class LeaveGroupRequest extends RequestBase {
    public String groupName;
    public LeaveGroupRequest(String groupName){
        this.groupName = groupName;
    }
}
