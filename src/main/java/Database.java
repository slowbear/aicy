import com.alibaba.fastjson.JSON;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Projections.*;

public class Database {

  private static MongoDatabase database;

  // 获取服务器配置文件
  public static Configuration getConfiguration() {
    ClassLoader classLoader = Utility.class.getClassLoader();
    String configFile = Objects.requireNonNull(classLoader.getResource("config.json")).getPath();
    try {
      String context = Files.readString(Path.of(configFile.substring(1)));
      return JSON.parseObject(context, Configuration.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  // 建立数据库链接
  public static void initialize() {
    Configuration config = getConfiguration();
    assert config != null;
    MongoCredential credential =
        MongoCredential.createCredential(
            config.getUser(), "admin", config.getSecret().toCharArray());
    MongoClient mongoClient =
        MongoClients.create(
            MongoClientSettings.builder()
                .applyToClusterSettings(
                    builder ->
                        builder.hosts(
                            Collections.singletonList(
                                new ServerAddress(config.getHost(), config.getPort()))))
                .credential(credential)
                .build());
    database = mongoClient.getDatabase(config.getDatabase());
  }

  // 查询用户是否存在(注册)
  public static boolean getUser(String username) {
    MongoCollection<Document> collection = database.getCollection("account");
    return collection.find(eq("username", username)).iterator().hasNext();
  }

  // 查询用户密码(若不存在该用户返回null)
  public static String getUserSecret(String username) {
    MongoCollection<Document> collection = database.getCollection("account");
    try (MongoCursor<Document> cursor = collection.find(eq("username", username)).iterator()) {
      if (cursor.hasNext()) return (String) cursor.next().get("secret");
      else return null;
    }
  }

  // 添加用户
  public static void createUser(String username, String secret, String nickname) {
    MongoCollection<Document> collection = database.getCollection("account");
    collection.insertOne(
        new Document("username", username)
            .append("secret", secret)
            .append("nickname", username)
            .append("status", "Offline"));
  }

  // 查询用户状态(若不存在该用户返回null)
  public static String getUserStatus(String username) {
    MongoCollection<Document> collection = database.getCollection("account");
    try (MongoCursor<Document> cursor = collection.find(eq("username", username)).iterator()) {
      if (cursor.hasNext()) return (String) cursor.next().get("status");
      else return null;
    }
  }

  // 修改用户状态
  public static void setUserStatus(String username, String status) {
    MongoCollection<Document> collection = database.getCollection("account");
    collection.updateOne(eq("username", username), set("status", status));
  }

  // 修改用户昵称
  public static void setUserNickname(String username, String nickname) {
    MongoCollection<Document> collection = database.getCollection("account");
    collection.updateOne(eq("username", username), set("nickname", nickname));
  }

  // 添加好友(单向)
  public static void addFriend(String fromID, String toID) {
    MongoCollection<Document> collection = database.getCollection(fromID);
    collection.insertOne(
        new Document("friend", toID).append("alias", toID).append("chat", Collections.emptyList()));
  }

  // 删除好友(单向)
  public static void deleteFriend(String fromID, String toID) {
    MongoCollection<Document> collection = database.getCollection(fromID);
    collection.deleteOne(eq("friend", toID));
  }

  // 添加聊天记录(单向)
  public static void addChatContext(String fromID, String toID, String chat) {
    MongoCollection<Document> collection = database.getCollection(fromID);
    List<String> history =
        (List<String>)
            Objects.requireNonNull(collection.find(eq("friend", toID)).first()).get("chat");
    history.add(chat);
    collection.updateOne(eq("friend", toID), set("chat", history));
  }

  // 查询好友列表
  public static List<Document> getFriendList(String username) {
    MongoCollection<Document> collection = database.getCollection(username);
    List<Document> friendList = new ArrayList<>();
    for (Document doc : collection.find().projection(fields(include("friend", "alias")))) {
      friendList.add(doc);
    }
    return friendList;
  }

  // 查询聊天记录
  public static List<String> getChatHistory(String fromID, String toID) {
    MongoCollection<Document> collection = database.getCollection(fromID);
    try (MongoCursor<Document> cursor = collection.find(eq("friend", toID)).iterator()) {
      if (cursor.hasNext()) return (List<String>) cursor.next().get("chat");
      else return null;
    }
  }
}
