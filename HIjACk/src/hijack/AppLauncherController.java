package hijack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


public class AppLauncherController {
    
    @FXML
    void handleExitAction(ActionEvent event) {
        Stage stage = HIjACk.getCurrentStage();
        stage.close();
    }

    @FXML
    void handleHelpAction(ActionEvent event) {
        Alert about = HIjACk.createAlert(Alert.AlertType.NONE, "About HIjACK", "", new Hyperlink("https://github.com/andrediegues/HIjACk"), new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE));
        about.showAndWait();
    }
    
    @FXML
    void handleLoadFolderAction(ActionEvent event) {
        Stage s = HIjACk.getCurrentStage();
        getFolderFromStage(s);
    }

    public void getFolderFromStage(Stage s) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        File choice = dc.showDialog(s);
        if(choice != null){
            try {                
                FXMLLoader app = new FXMLLoader(getClass().getResource("application.fxml"));
                Parent appRoot = app.load();
                ApplicationController appController = app.getController();
                
                List<String> listOfNames = new ArrayList<>();
                for(File f: choice.listFiles((File dir, String name) -> name.toLowerCase().contains(".jp") || name.toLowerCase().contains(".png") 
                        || name.toLowerCase().endsWith("if"))){
                    listOfNames.add(f.getName());
                }     
                if(listOfNames.isEmpty()){
                    Alert emptyFolder = HIjACk.createAlert(Alert.AlertType.NONE, "Found 0 images!", "Didn't find images. Please choose a different folder.", 
                            new ImageView("images/ic_error_black_48dp.png"), new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
                    emptyFolder.showAndWait();
                }
                else{
                    s.close();
                    listOfNames.sort((String o1, String o2) -> {
                        int c = o1.length() - o2.length();
                        if(c == 0){
                            c = o1.compareTo(o2);
                        }
                        return c;
                    });
                    Stage application = new Stage();
                    application.setScene(new Scene(appRoot));
                    application.setTitle("HIjACk");
                    HIjACk.setCurrentStage(application);
                    application.show();
                    appController.initData(choice, listOfNames);
                }
            } catch (IOException ex) {
                Logger.getLogger(AppLauncherController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
