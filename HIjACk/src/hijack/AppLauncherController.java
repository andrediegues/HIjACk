package hijack;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class AppLauncherController {
    
    @FXML
    private VBox vbox;
    
    @FXML
    void handleLoadFolderAction(ActionEvent event) {
        Stage s = (Stage) vbox.getScene().getWindow();
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        File choice = dc.showDialog(s);
        if(choice == null){
        }
        else{
            try {
                s.close();
                // change scene to app
                Stage stage                         = new Stage();
                FXMLLoader loading                  = new FXMLLoader(getClass().getResource("loading.fxml"));
                Parent loadingRoot                  = loading.load();
                LoadingController loadController    = loading.getController();
                
                Scene loadingScene = new Scene(loadingRoot);
                stage.setScene(loadingScene);
                stage.show();
                FXMLLoader app                      = new FXMLLoader(getClass().getResource("application.fxml"));
                Parent appRoot                      = app.load();
                ApplicationController appController = app.getController();

                appController.initData(choice);
                loadController.handleProgression(choice);
                //stage.close();
            } catch (IOException ex) {
                Logger.getLogger(AppLauncherController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
