import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;

public class PopController implements Initializable {
  @FXML private JFXTextField friend;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    friend.setOnKeyPressed(
        event -> {
          if (event.getCode().equals(KeyCode.ENTER)) {
            String target = friend.getText();
            if (AicyUI.getClient().addFriend(target)) {
              Alert alert = new Alert(AlertType.CONFIRMATION);
              alert.setContentText("添加成功!");
              alert.showAndWait();
            } else {
              Alert alert = new Alert(AlertType.ERROR);
              alert.setContentText("添加失败!(用户不存在)");
              alert.showAndWait();
            }
          }
        });
  }
}
