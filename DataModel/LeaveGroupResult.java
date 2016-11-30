package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class LeaveGroupResult extends ResultBase {
    public String groupName;
    public LeaveGroupResult(ErrorCode code){
        super(code);
    }
    public LeaveGroupResult(String groupName){
        this.groupName = groupName;
    }
}
