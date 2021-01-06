import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AicyUI extends Application {
  private static Stage stage = null;

  private static Client client;

  public static boolean logged = false;

  public static Client getClient() {
    return client;
  }

  static {
    try {
      client = new Client(new Socket(InetAddress.getLocalHost(), 3456));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    stage = primaryStage;
    FXMLLoader loginUI = new FXMLLoader(AicyUI.class.getResource("LoginUI.fxml"));
    stage.setOnCloseRequest(
        event -> {
          client.setStatus("Offline");
          client.close();
        });
    stage.setScene(new Scene(loginUI.load()));
    stage.setResizable(false);
    stage.setTitle("Login");
    stage.show();
  }

  public static void switchToClient() {
    try {
      logged = true;
      FXMLLoader clientUI = new FXMLLoader(AicyUI.class.getResource("ClientUI.fxml"));
      stage.close();
      stage.getScene().setRoot(clientUI.load());
      stage.setTitle("Aicy v0.1.0 当前用户: " + getClient().getUsername());
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
