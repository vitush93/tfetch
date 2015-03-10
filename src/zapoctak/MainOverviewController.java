/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zapoctak;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
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
        // TODO

    }

    @FXML
    public void initFetch(ActionEvent e) {
        if (this.textField.getText().length() == 0) {
            Dialogs.create()
                    .owner((Stage) menuBar.getScene().getWindow())
                    .title("Error")
                    .masthead(null)
                    .message("Please enter a valid blog ID.")
                    .showInformation();

            return;
        }

        this.textField.setDisable(true);
        this.fetchButton.setDisable(true);

        try {
            URL website = new URL("http://36.media.tumblr.com/d4b9d1508973efd0b1cfb49c4363333b/tumblr_n0dv3vlZYh1smzycho1_500.jpg");
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            
            FileOutputStream fos = new FileOutputStream("image.jpg");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception ex) {
                
        }
    }

    @FXML
    public void menuClose(ActionEvent e) {
        Stage stage = (Stage) menuBar.getScene().getWindow();

        stage.close();
    }

}
