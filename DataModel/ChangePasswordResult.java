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
 * Created by Eric on 11/30/16.
 */
public class ChangePasswordResult extends ResultBase{
    public String username;
    public ChangePasswordResult(ErrorCode code){
        super(code);
    }
    public ChangePasswordResult(String _username){
        this.username = _username;
    }
}
