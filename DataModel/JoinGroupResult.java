package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class JoinGroupResult extends ResultBase {
    public String groupName;
    public String[] currentMembers;
    public JoinGroupResult(ErrorCode code){
        super(code);
    }
    public JoinGroupResult(String groupName, String[] members){
        this.groupName = groupName;
        this.currentMembers = members;
    }
}
