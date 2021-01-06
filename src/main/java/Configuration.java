import com.alibaba.fastjson.annotation.JSONField;

class Configuration {
  @JSONField(name = "host")
  private String host;

  @JSONField(name = "database")
  private String database;

  @JSONField(name = "port")
  private int port;

  @JSONField(name = "user")
  private String user;

  @JSONField(name = "secret")
  private String secret;

  // fastjson需要
  public Configuration() {}

  public Configuration(String host, String database, int port, String user, String secret) {
    this.host = host;
    this.database = database;
    this.port = port;
    this.user = user;
    this.secret = secret;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }
}
