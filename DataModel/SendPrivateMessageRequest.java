package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public class SendPrivateMessageRequest extends RequestBase {
    public String to;
    public String message;
    public SendPrivateMessageRequest(){}
    public SendPrivateMessageRequest(String _to, String _message){
        this.to = _to;
        this.message = _message;
    }
}
