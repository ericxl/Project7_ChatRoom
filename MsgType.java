package assignment7;

/**
 * Created by Eric on 11/27/16.
 */
public class MsgType {
    private static final byte _offset = 64;

    // Client -> Server
    public static final byte RegisterRequest = 0;
    public static final byte LoginRequest = 1;
    public static final byte LogoutRequest = 2;
    public static final byte AddFriendRequest = 3;
    public static final byte SendPrivateMessageRequest = 4;
    public static final byte SendGroupMessageRequest = 5;

    // Server -> Client
    public static final byte RegisterResult = RegisterRequest + _offset;
    public static final byte LoginResult = LoginRequest + _offset;
    public static final byte LogoutResult = LogoutRequest + _offset;
    public static final byte AddFriendResult = AddFriendRequest + _offset;
    public static final byte SendPrivateMessageResult = SendPrivateMessageRequest + _offset;
    public static final byte SendGroupMessageResult = SendGroupMessageRequest + _offset;
}
