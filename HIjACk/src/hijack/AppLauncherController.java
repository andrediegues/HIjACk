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
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


public class AppLauncherController {
    
    @FXML
    private VBox vbox;
    
    @FXML
    void handleLoadFolderAction(ActionEvent event) {
        Stage s = (Stage) vbox.getScene().getWindow();
        getFolderFromStage(s);
    }

    public void getFolderFromStage(Stage s) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        File choice = dc.showDialog(s);
        if(choice == null){
        }
        else{
            try {                
                FXMLLoader app                      = new FXMLLoader(getClass().getResource("application.fxml"));
                Parent appRoot                      = app.load();
                ApplicationController appController = app.getController();
                
                List<String> listOfNames = new ArrayList<>();
                for(File f: choice.listFiles((File dir, String name) -> (name.toLowerCase().contains(".jpg") || name.toLowerCase().contains(".png")))){
                    listOfNames.add(f.getName());
                }     
                if(listOfNames.isEmpty()){
                    Alert emptyDirectory = new Alert(Alert.AlertType.NONE, "Didn't find images. Please choose a different folder.", new ButtonType("Ok"));
                    emptyDirectory.setTitle("Error");
                    emptyDirectory.showAndWait();
                }
                else{
                    s.close();
                    listOfNames.sort((String o1, String o2) -> o1.toUpperCase().compareTo(o2.toUpperCase()));

                    appController.initData(choice, listOfNames);
                    Stage application = new Stage();
                    application.setScene(new Scene(appRoot));
                    application.setTitle("HIjACk");
                    application.show();
                }
            } catch (IOException ex) {
                Logger.getLogger(AppLauncherController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
