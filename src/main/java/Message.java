import java.io.Serializable;

enum MessageKind implements Serializable {
  RegisterRequest, // 注册请求
  LoginRequest, // 登陆请求
  FriendRequest, // 好友请求
  Text, // 发送消息
  ChangeNickname, // TODO:修改昵称
  DeleteFriend, // TODO:删除好友
  QueryStatus, // 查看状态
  ChangeStatus, // 修改状态
  QueryFriendList, // 查询好友列表
  QueryChatHistory, // 查询聊天记录
  CloseConnection // 关闭链接
}

class RegisterRequest implements Serializable {
  String username, secret;

  public RegisterRequest(String username, String secret) {
    this.username = username;
    this.secret = secret;
  }
}

class LoginRequest implements Serializable {
  String username, secret;

  public LoginRequest(String username, String secret) {
    this.username = username;
    this.secret = secret;
  }
}

class FriendRequest implements Serializable {
  String fromID, toID;

  public FriendRequest(String fromID, String toID) {
    this.fromID = fromID;
    this.toID = toID;
  }
}

class Text implements Serializable {
  String fromID, toID, context;

  public Text(String fromID, String toID, String context) {
    this.fromID = fromID;
    this.toID = toID;
    this.context = context;
  }
}

class ChangeNickname implements Serializable {
  String username, nickname;

  public ChangeNickname(String username, String nickname) {
    this.username = username;
    this.nickname = nickname;
  }
}

class DeleteFriend implements Serializable {
  String fromID, toID;

  public DeleteFriend(String fromID, String toID) {
    this.fromID = fromID;
    this.toID = toID;
  }
}

class QueryStatus implements Serializable {
  String username;

  public QueryStatus(String username) {
    this.username = username;
  }
}

class ChangeStatus implements Serializable {
  String username, status;

  public ChangeStatus(String username, String status) {
    this.username = username;
    this.status = status;
  }
}

class QueryFriendList implements Serializable {
  String username;

  public QueryFriendList(String username) {
    this.username = username;
  }
}

class QueryChatHistory implements Serializable {
  String fromID, toID;

  public QueryChatHistory(String fromID, String toID) {
    this.fromID = fromID;
    this.toID = toID;
  }
}
