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

    // Server-> Client
    public static final byte RegisterResult = RegisterRequest + _offset;
    public static final byte LoginResult = LoginRequest + _offset;
    public static final byte LogoutResult = LogoutRequest + _offset;
}
