import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.bson.Document;

public class ClientUIController implements Initializable {
  @FXML private JFXTextArea inputArea;
  @FXML private JFXButton sendButton;
  @FXML private JFXListView<Label> friendList;
  @FXML private JFXListView<Label> chatBoard;
  @FXML private MenuItem friendAdder;

  private String chosen = null;
  private ArrayList<String> friends = new ArrayList<>();

  private void refreshFriendList() {
    friendList.getItems().clear();
    friends.clear();
    for (Document doc : AicyUI.getClient().getFriendList()) {
      String user = doc.getString("friend");
      friends.add(user);
      friendList
          .getItems()
          .add(
              new Label(
                  String.format(
                      "%s (%s)", doc.getString("alias"), AicyUI.getClient().getUserStatus(user))));
    }
  }

  private void refreshChatBoard() {
    chatBoard.getItems().clear();
    if (chosen != null) {
      for (String chat : AicyUI.getClient().getChatHistory(chosen)) {
        chatBoard.getItems().add(new Label(chat));
      }
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    refreshFriendList();
    sendButton.setOnAction(
        event -> {
          AicyUI.getClient().sendText(chosen, inputArea.getText());
          refreshChatBoard();
          inputArea.setText("");
        });
    friendAdder.setOnAction(
        event -> {
          try {
            Stage stage = new Stage();
            FXMLLoader pop = new FXMLLoader(AicyUI.class.getResource("Pop.fxml"));
            stage.setScene(new Scene(pop.load()));
            stage.setResizable(false);
            stage.setTitle("添加好友");
            stage.show();
            stage.setOnCloseRequest(__ -> refreshFriendList());
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
    friendList.setOnMouseClicked(
        event -> {
          if (friends.isEmpty()) return;
          if (friendList.getSelectionModel().getSelectedItem() == null) return;
          chosen = friends.get(friendList.getSelectionModel().getSelectedIndex());
          refreshChatBoard();
        });
  }
}
