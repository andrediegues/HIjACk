/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hijack;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author diegues
 */
public class HIjACk extends Application {

    Stage stage;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        VBox root = FXMLLoader.load(getClass().getResource("appLauncher.fxml"));        
        Scene scene = new Scene(root);
        stage.setTitle("HIjACk");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
