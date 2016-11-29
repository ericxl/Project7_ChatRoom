package assignment7.DataModel;

/**
 * Created by Eric on 11/29/16.
 */
public abstract class ResultBase extends ServerMessageBase {
    public ErrorCode error;

    public ResultBase(){}
    public ResultBase(ErrorCode code){
        this.error = code;
    }
}