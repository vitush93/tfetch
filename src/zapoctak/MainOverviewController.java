package zapoctak;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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

/**
 * MainOverview controller.
 *
 * @author Vít Habada
 */
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
        cancelButton.setDisable(true);
        footerLabel.setText("Stopping job..");

        Thread t = new Thread(() -> {
            try {
                fetchingService.stop();

                cancelButton.setDisable(true);
                fetchButton.setDisable(false);
                textField.setDisable(false);
                // TODO: throws exception footerLabel.setText("No job running");
            } catch (InterruptedException ex) {
                Logger.getLogger(MainOverviewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        t.start();
    }

    /**
     * Initialize the fetching process.
     *
     * @param e
     */
    @FXML
    public void initFetch(ActionEvent e) {
        Stage s = (Stage) menuBar.getScene().getWindow();
        s.setOnHidden((WindowEvent e1) -> {
            try {
                fetchingService.stop();
            } catch (InterruptedException ex) {
                Logger.getLogger(MainOverviewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        if (textField.getText().length() == 0) {
            MessageBox(s, "Please enter a valid blog ID.");

            return;
        }

        try {
            URL test = new URL(textField.getText());

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
            footerLabel.setText("Job running..");
            startFetchingService();
        } catch (InvalidArgumentException ex) {
            MessageBox(s, ex.getMessage());
        } catch (IOException ex) {
            MessageBox(s, "Error occured while fetching the URL."); // bad luck huh
        }
    }

    /**
     * Start the fetching process.
     *
     * @throws InvalidArgumentException
     */
    private void startFetchingService() throws InvalidArgumentException {
        fetchingService = new TumblrFetchingService(textField.getText());
        fetchingService.start();
        
        // natural stop - all worker threads finish
        Runnable r = () -> {
            boolean fetchersStillAlive = Fetcher.deadCount.get() != TumblrFetchingService.FETCHER_COUNT;
            boolean crawlersStillAlive = Crawler.deadCount.get() != TumblrFetchingService.CRAWLER_COUNT;
            while(fetchersStillAlive && crawlersStillAlive) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TumblrFetchingService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            try {
                fetchingService.stop();
                
                cancelButton.setDisable(true);
                fetchButton.setDisable(false);
                textField.setDisable(false);
                textField.setText("");
                // TODO: throws exception footerLabel.setText("No job running");
            } catch (InterruptedException ex) {
                Logger.getLogger(MainOverviewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        
        new Thread(r).start();
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
