package zapoctak;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainOverviewController implements Initializable {

    private TumblrFetchingService fetchingService;

    @FXML
    public MenuBar menuBar;

    @FXML
    public Button fetchButton;

    @FXML
    public Button cancelButton;

    @FXML
    public TextField textField;

    @FXML
    public Label footerLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Displays a info box.
     *
     * @param stage parent stage
     * @param msg message to display
     */
    public static void MessageBox(Stage stage, String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        alert.showAndWait();

    }

    /**
     * Stop the fetching process.
     *
     * @param e
     */
    @FXML
    public void cancelFetch(ActionEvent e) {
        fetchingService.stop();

        cancelButton.setDisable(true);
        fetchButton.setDisable(false);
        textField.setDisable(false);
    }

    /**
     * Start the fetching process.
     *
     * @param e
     */
    @FXML
    public void initFetch(ActionEvent e) {
        Stage s = (Stage) menuBar.getScene().getWindow();
        s.setOnHidden((WindowEvent e1) -> {
            fetchingService.stop();
        });

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
            cancelButton.setDisable(false);
            startFetchingService();
        } catch (InvalidArgumentException ex) {
            MessageBox(s, ex.getMessage());
        } catch (Exception ex) {
            MessageBox(s, "Error occured while fetching the URL."); // bad luck huh
        }
    }

    private void startFetchingService() throws InvalidArgumentException {
        fetchingService = new TumblrFetchingService(textField.getText());
        Thread t = new Thread(() -> fetchingService.start());
        t.start();
    }

    /**
     * Close the application via MenuBar.
     *
     * @param e
     */
    @FXML
    public void menuClose(ActionEvent e) {
        Stage stage = (Stage) menuBar.getScene().getWindow();

        stage.close();
    }

}
