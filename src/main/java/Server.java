import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
//import java.util.HashMap;

class Handler implements Runnable {
  // 客户端链接
  Socket client;

  // 全局连接池(TODO: 利用全局连接池实现即时更新客户端信息)
  // static HashMap<String,Socket> connections =new HashMap<>();

  // 日志输出流
  final static PrintStream logger = new PrintStream(System.out);

  public Handler(Socket client) {
    this.client = client;
  }

  @Override
  public void run() {
    logger.println("[info]IP" + client.getInetAddress() + "已连接服务器");
    // 保留当前登陆用户
    String username = null;
    try (ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(client.getInputStream())) {
      boolean finished = false;
      while (!finished) {
        MessageKind kind = (MessageKind) in.readObject();
        // 判别信息类型
        switch (kind) {
          case RegisterRequest -> {
            RegisterRequest message = (RegisterRequest) in.readObject();
            String secret = Database.getUserSecret(message.username);
            if(secret == null) {
              Database.createUser(message.username, message.secret,message.username);
              out.writeBoolean(true);
            } else {
              out.writeBoolean(false);
            }
            out.flush();
          }
          case LoginRequest -> {
            LoginRequest message = (LoginRequest) in.readObject();
            String secret = Database.getUserSecret(message.username);
            if (secret == null || !secret.equals(message.secret)) {
              out.writeBoolean(false);
            } else {
              Database.setUserStatus(message.username, "Online");
              // TODO: 维护连接池
              // connections.put(message.username, client);
              logger.println("[info]用户" + message.username + "已登录");
              username = message.username;
              out.writeBoolean(true);
            }
            out.flush();
          }
          case FriendRequest -> {
            FriendRequest message = (FriendRequest) in.readObject();
            if(Database.getUser(message.toID)){
              Database.addFriend(message.fromID, message.toID);
              Database.addFriend(message.toID, message.fromID);
              out.writeBoolean(true);
            } else {
              out.writeBoolean(false);
            }
            out.flush();
          }
          case Text -> {
            Text message = (Text) in.readObject();
            Database.addChatContext(message.fromID, message.toID, message.context);
            Database.addChatContext(message.toID, message.fromID, message.context);
          }
          case ChangeNickname -> {
            ChangeNickname message = (ChangeNickname) in.readObject();
            Database.setUserNickname(message.username, message.nickname);
          }
          case DeleteFriend -> {
            DeleteFriend message = (DeleteFriend) in.readObject();
            Database.deleteFriend(message.fromID, message.toID);
            Database.deleteFriend(message.toID, message.fromID);
          }
          case QueryStatus -> {
            QueryStatus message = (QueryStatus) in.readObject();
            out.writeObject(Database.getUserStatus(message.username));
            out.flush();
          }
          case ChangeStatus -> {
            ChangeStatus message = (ChangeStatus) in.readObject();
            Database.setUserStatus(message.username, message.status);
          }
          case QueryFriendList -> {
            QueryFriendList message = (QueryFriendList) in.readObject();
            out.writeObject(Database.getFriendList(message.username));
            out.flush();
          }
          case QueryChatHistory -> {
            QueryChatHistory message = (QueryChatHistory) in.readObject();
            out.writeObject(Database.getChatHistory(message.fromID, message.toID));
            out.flush();
          }
          case CloseConnection -> {
            finished = true;
            client.close();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      logger.println("[info]IP" + client.getInetAddress() + "已断开服务器");
      if (username !=null) Database.setUserStatus(username, "Offline");
    }
  }
}

public class Server {
  public static void main(String[] args) throws IOException {
    // 初始化数据库
    Database.initialize();
    // 监听3456端口
    try (ServerSocket server = new ServerSocket(3456)) {
      while (true) {
        // 为每个链接启动一个线程来处理
        Socket client = server.accept();
        new Thread(new Handler(client)).start();
      }
    }
  }
}
