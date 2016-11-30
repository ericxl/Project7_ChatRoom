package assignment7.DataModel;

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
    public static final byte GetActiveGroupsRequest = 6;
    public static final byte GetFriendsRequest = 7;
    public static final byte JoinGroupRequest = 8;
    public static final byte LeaveGroupRequest = 9;
    public static final byte GetOnlineGroupMembersRequest = 10;
    public static final byte ChangePasswordRequest = 11;

    // Server -> Client
    public static final byte RegisterResult = RegisterRequest + _offset;
    public static final byte LoginResult = LoginRequest + _offset;
    public static final byte LogoutResult = LogoutRequest + _offset;
    public static final byte AddFriendResult = AddFriendRequest + _offset;
    public static final byte SendPrivateMessageResult = SendPrivateMessageRequest + _offset;
    public static final byte SendGroupMessageResult = SendGroupMessageRequest + _offset;
    public static final byte GetActiveGroupsResult = GetActiveGroupsRequest + _offset;
    public static final byte GetFriendsResult = GetFriendsRequest + _offset;
    public static final byte JoinGroupResult = JoinGroupRequest + _offset;
    public static final byte LeaveGroupResult = LeaveGroupRequest + _offset;
    public static final byte GetOnlineGroupMembersResult = GetOnlineGroupMembersRequest + _offset;
    public static final byte ChangePasswordResult = ChangePasswordRequest + _offset;

    public static final byte JoinedGroupMessage = 126;
    public static final byte ChatMessage = 127;
}
