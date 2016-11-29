package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class AddToGroupRequest extends RequestBase{
    public String name;
    public AddToGroupRequest(){}
    public AddToGroupRequest(String name){
        this.name = name;
    }
}
