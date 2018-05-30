package hijack;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 *
 * @author diegues
 */
public class HIjACk extends Application {
    private static Stage currentStage;

    @Override
    public void start(Stage stage) throws Exception {
        currentStage = stage;
        VBox root = FXMLLoader.load(getClass().getResource("appLauncher.fxml"));        
        Scene scene = new Scene(root);
        currentStage.setTitle("HIjACk");
        currentStage.setScene(scene);
        currentStage.show();
    }

    public static Stage getCurrentStage() {
        return currentStage;
    }

    public static void setCurrentStage(Stage currentStage) {
        HIjACk.currentStage = currentStage;
    }
    
    public static Alert createAlert(Alert.AlertType type, String title, String contextText, Node graphic, ButtonType... buttons){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(contextText);
        alert.setGraphic(graphic);
        alert.getButtonTypes().addAll(buttons);
        
        return alert;
    }
    
    public static Notifications createNotification(String text, Duration duration, String title, Node graphic, Pos position){
        Notifications notification = Notifications.create()
                .text(text)
                .hideAfter(duration)
                .title(title)
                .graphic(graphic)
                .position(position);
        
        return notification;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
