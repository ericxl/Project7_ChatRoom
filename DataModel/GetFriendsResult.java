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

import java.util.*;

/**
 * Created by Eric on 11/29/16.
 */
public class GetFriendsResult extends ResultBase {
    public String[] friends;
    public String[] onlineFriends;

    public GetFriendsResult(ErrorCode code){
        super(code);
    }
    public GetFriendsResult(String[] friends, String[] onlineFriends){
        this.friends = friends;
        this.onlineFriends=onlineFriends;
    }
}
