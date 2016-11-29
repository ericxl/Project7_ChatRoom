package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class SendGroupMessageRequest extends RequestBase {
    public String to;
    public String message;
    public SendGroupMessageRequest(){}
    public SendGroupMessageRequest(String _to, String _message){
        this.to=_to;
        this.message = _message;
    }
}