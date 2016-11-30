package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class JoinGroupRequest extends RequestBase {
    public String groupName;
    public JoinGroupRequest(String groupName){
        this.groupName = groupName;
    }
}
