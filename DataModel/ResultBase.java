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