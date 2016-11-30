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
