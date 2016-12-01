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

import java.io.*;
import java.util.*;

/**
 * Created by Eric on 11/28/16.
 */
public class AccountInfo implements Serializable{
    public String username;
    public String password;
    public ArrayList<String> friends = new ArrayList<>();
}
