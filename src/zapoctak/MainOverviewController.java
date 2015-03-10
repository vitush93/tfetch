/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zapoctak;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

public class MainOverviewController implements Initializable {

    private TumblrFetchingService fetchingService;

    @FXML
    public MenuBar menuBar;

    @FXML
    public Button fetchButton;

    @FXML
    public TextField textField;

    @FXML
    public Label footerLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public static void MessageBox(Stage stage, String msg) {
        Dialogs.create()
                .owner(stage)
                .title("Message")
                .masthead(null)
                .message(msg)
                .showInformation();
    }

    @FXML
    public void initFetch(ActionEvent e) {
        Stage s = (Stage) menuBar.getScene().getWindow();

        if (textField.getText().length() == 0) {
            MessageBox(s, "Please enter a valid blog ID.");

            return;
        }

        try {
            URL test = new URL("http://" + textField.getText() + ".tumblr.com/");

            HttpURLConnection conn = (HttpURLConnection) test.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != 200) {
                MessageBox(s, "Blog ID is invalid or blog no longer exists.");
                return;
            }

            textField.setDisable(true);
            fetchButton.setDisable(true);

            fetchingService = new TumblrFetchingService(textField.getText());
            fetchingService.execute();
        } catch (Exception ex) {
            MessageBox(s, "Error occured while fetching the URL."); // bad luck huh
        }
    }

    @FXML
    public void menuClose(ActionEvent e) {
        Stage stage = (Stage) menuBar.getScene().getWindow();

        stage.close();
    }

}
