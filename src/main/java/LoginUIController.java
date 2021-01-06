import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class LoginUIController implements Initializable {
  @FXML private JFXTextField textField;
  @FXML private JFXPasswordField passwordField;
  @FXML private JFXButton registerButton;
  @FXML private JFXButton loginButton;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    registerButton.setOnAction(
        event -> {
          String username = textField.getText();
          String password = passwordField.getText();
          if (!Utility.checkUserNameValid(username) || !Utility.checkPasswordValid(password)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("请检查用户名和密码是否规范!");
            alert.showAndWait();
          } else if (AicyUI.getClient().register(username, Utility.getHash(password))) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("注册成功!");
            alert.showAndWait();
          } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("该用户已注册!");
            alert.showAndWait();
          }
        });
    loginButton.setOnAction(
        event -> {
          String username = textField.getText();
          String secret = Utility.getHash(passwordField.getText());
          if (AicyUI.getClient().login(username, secret)) {
            AicyUI.switchToClient();
          } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("用户" + username + "未注册或密码错误!");
            alert.showAndWait();
          }
        });
  }
}
