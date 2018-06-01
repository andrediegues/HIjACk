package hijack;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static HashMap<String, String> eunis;
    private static HashMap<String, String> aphiaID;

    @Override
    public void start(Stage stage) throws Exception {
        eunis = readLocalDB(new File("src/db/EunisCode.csv"));
        aphiaID = readLocalDB(new File("src/db/AphiaIDspecies.csv"));
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

    private HashMap<String, String> readLocalDB(File file) {
        HashMap<String, String> hm = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while((line = br.readLine()) != null){
                hm.put(line.split(",")[0], line.split(",")[1]);
            }
        } catch (IOException ex) {
            Logger.getLogger(HIjACk.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return hm;
    }

    public static HashMap<String, String> getEunis() {
        return eunis;
    }

    public static HashMap<String, String> getAphiaID() {
        return aphiaID;
    }
}
