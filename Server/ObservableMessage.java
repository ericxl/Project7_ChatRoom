/* EE422C Project 7 submission by
 * Xiaoyong Liang
 * XL5432
 * 16480
 * Yuankai Yue
 * yy7347
 * 16465
 * Slip days used: <1>
 * Fall 2016
 */
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
