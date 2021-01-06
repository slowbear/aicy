import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import org.bson.Document;

public class Client {
  private final Socket upstream;
  private String username;
  private ObjectInputStream in;
  private ObjectOutputStream out;

  public String getUsername() {
    return username;
  }

  Client(Socket upstream) {
    this.upstream = upstream;
    try {
      this.out = new ObjectOutputStream(upstream.getOutputStream());
      this.in = new ObjectInputStream(upstream.getInputStream());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void close() {
    try {
      out.writeObject(MessageKind.CloseConnection);
      out.close();
      in.close();
      upstream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean register(String username, String password) {
    try {
      String secret = Utility.getHash(password);
      out.writeObject(MessageKind.RegisterRequest);
      out.writeObject(new RegisterRequest(username, secret));
      return in.readBoolean();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean login(String username, String password) {
    try {
      String secret = Utility.getHash(password);
      out.writeObject(MessageKind.LoginRequest);
      out.writeObject(new LoginRequest(username, secret));
      boolean flag = in.readBoolean();
      if (flag) this.username = username;
      return flag;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public void sendText(String target, String context) {
    try {
      out.writeObject(MessageKind.Text);
      out.writeObject(new Text(username, target, context));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean addFriend(String target) {
    try {
      out.writeObject(MessageKind.FriendRequest);
      out.writeObject(new FriendRequest(username, target));
      return in.readBoolean();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public void deleteFriend(String target) {
    try {
      out.writeObject(MessageKind.DeleteFriend);
      out.writeObject(new DeleteFriend(username, target));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public List<Document> getFriendList() {
    try {
      out.writeObject(MessageKind.QueryFriendList);
      out.writeObject(new QueryFriendList(username));
      return (List<Document>) in.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<String> getChatHistory(String target) {
    try {
      out.writeObject(MessageKind.QueryChatHistory);
      out.writeObject(new QueryChatHistory(username, target));
      return (List<String>) in.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public void setStatus(String status) {
    try {
      out.writeObject(MessageKind.ChangeStatus);
      out.writeObject(new ChangeStatus(username, status));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getUserStatus(String username) {
    try {
      out.writeObject(MessageKind.QueryStatus);
      out.writeObject(new QueryStatus(username));
      return (String) in.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
