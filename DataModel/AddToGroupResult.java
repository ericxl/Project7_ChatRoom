package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class AddToGroupResult extends ResultBase{
    public String name;
    public AddToGroupResult(ErrorCode code){
        super(code);
    }
    public AddToGroupResult(String name){
        this.name = name;
    }
}
