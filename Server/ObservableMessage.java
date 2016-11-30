package assignment7.Server;
import assignment7.DataModel.ServerMessageBase;
/**
 * Created by Eric on 11/29/16.
 */
public class ObservableMessage {
    public byte channel;
    public ServerMessageBase message;

    public ObservableMessage(byte channel, ServerMessageBase message){
        this.channel = channel;
        this.message = message;
    }
}
