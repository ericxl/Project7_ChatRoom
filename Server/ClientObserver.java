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

import java.io.*;
import java.util.*;
import assignment7.DataModel.*;

public class ClientObserver extends ObjectOutputStream implements Observer {
	private String userName;
	private ArrayList<String> friends;
	private Set<String> joinedGroups = new HashSet<>();
	public ClientObserver(OutputStream out) throws IOException {
		super(out);
	}

	public void joinGroup(String groupName){
		if(groupName == null) return;
		joinedGroups.add(groupName);
	}
	public boolean hasJoinedGroup(String groupName){
		if(groupName == null) return false;
		if(joinedGroups.contains(groupName)){
			return true;
		}
		return false;
	}
	public void leaveGroup(String groupName){
		if(groupName == null) return;
		if(joinedGroups.contains(groupName)){
			joinedGroups.remove(groupName);
		}
	}

	public void setName(String userName){
		this.userName = userName;
	}
	public void setFriends(ArrayList<String> _friends){
		this.friends = _friends;
	}
	@Override
	public void update(Observable o, Object arg) {
		try {
			ObservableMessage message = (ObservableMessage) arg;
			if(message.channel == MsgType.ChatMessage){
				ChatMessage msg = (ChatMessage) message.message;
				if(msg.toGroup != null){
					if(joinedGroups.contains(msg.toGroup) || msg.from.equals(userName)){
						try {
							this.writeByte(MsgType.ChatMessage);
							this.writeObject(msg);
							this.flush();
						}
						catch (IOException e){
						}
					}
				} else if(msg.toUser != null){
					if(msg.toUser.equals(this.userName) || msg.from.equals(userName)){
						try {
							this.writeByte(MsgType.ChatMessage);
							this.writeObject(msg);
							this.flush();
						}
						catch (IOException e){
						}
					}
				}
			}
			else if(message.channel == MsgType.JoinedGroupMessage){
				JoinedGroupMessage msg = (JoinedGroupMessage) message.message;

				if(joinedGroups.contains(msg.joinedGroup)){
					try {
						this.writeByte(MsgType.JoinedGroupMessage);
						this.writeObject(msg);
						this.flush();
					}
					catch (IOException e){
					}
				}
			}
			else if (message.channel == MsgType.FriendOnlineMessage){
				FriendOnlineMessage msg = (FriendOnlineMessage) message.message;
				if(friends.contains(msg.friendUsername)){
					try {
						this.writeByte(MsgType.FriendOnlineMessage);
						this.writeObject(msg);
						this.flush();
					}
					catch (IOException e){
					}
				}
			}
			else if (message.channel == MsgType.FriendOfflineMessage){
				FriendOfflineMessage msg = (FriendOfflineMessage) message.message;
				if(friends.contains(msg.friendUsername)){
					try {
						this.writeByte(MsgType.FriendOfflineMessage);
						this.writeObject(msg);
						this.flush();
					}
					catch (IOException e){
					}
				}
			}

		}
		catch (ClassCastException e){

		}
	}
}