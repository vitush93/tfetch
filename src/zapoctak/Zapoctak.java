
package zapoctak;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class containing program's entry point.
 * 
 * @author Vít
 */
public class Zapoctak extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainOverview.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    
    @Override
    public void stop() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

class InvalidArgumentException extends Exception {

    public InvalidArgumentException(String message) {
        super(message);
    }
}

class InvalidOperationException extends Exception {

    public InvalidOperationException(String message) {
        super(message);
    }
}